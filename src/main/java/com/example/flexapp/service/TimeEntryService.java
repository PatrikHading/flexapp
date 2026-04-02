package com.example.flexapp.service;

import com.example.flexapp.dto.FlexBalanceResponse;
import com.example.flexapp.dto.ManualTimeEntryRequest;
import com.example.flexapp.dto.TimeEntryResponse;
import com.example.flexapp.entity.TimeEntry;
import com.example.flexapp.entity.User;
import com.example.flexapp.entity.WorkSchedule;
import com.example.flexapp.enums.TimeEntryStatus;
import com.example.flexapp.exception.BadRequestException;
import com.example.flexapp.exception.ResourceNotFoundException;
import com.example.flexapp.repository.TimeEntryRepository;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.repository.WorkScheduleRepository;
import com.example.flexapp.security.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TimeEntryService {

    private static final int MANUAL_ENTRY_MAX_DAYS_BACK = 7;

    private static final List<String> ALLOWED_HISTORY_SORT_FIELDS = List.of("workDate", "createdAt", "id");
    private static final Set<String> ALLOWED_HISTORY_SORT_FIELD_SET = Set.copyOf(ALLOWED_HISTORY_SORT_FIELDS);
    private static final Sort DEFAULT_HISTORY_SORT = Sort.by(
            Sort.Order.desc("workDate"),
            Sort.Order.desc("id")
    );

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final FlexCalculationService flexCalculationService;
    private final SecurityService securityService;

    public TimeEntryService(TimeEntryRepository timeEntryRepository,
                            UserRepository userRepository,
                            WorkScheduleRepository workScheduleRepository,
                            FlexCalculationService flexCalculationService,
                            SecurityService securityService) {
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
        this.workScheduleRepository = workScheduleRepository;
        this.flexCalculationService = flexCalculationService;
        this.securityService = securityService;
    }

    @Transactional
    public TimeEntryResponse checkIn() {
        User currentUser = securityService.getCurrentUser();
        User lockedUser = lockUser(currentUser.getId());
        Long userId = lockedUser.getId();
        LocalDate today = LocalDate.now();

        workScheduleRepository.findByUserIdAndWorkDate(userId, today)
                .orElseThrow(() -> new BadRequestException(
                        "No work schedule found for today. Please contact your administrator to create a work schedule."
                ));

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(userId, today).orElse(null);

        if (existingEntry != null && existingEntry.getCheckInTime() != null) {
            throw new BadRequestException("You are already checked in for today.");
        }

        TimeEntry timeEntry = existingEntry != null ? existingEntry : new TimeEntry();

        timeEntry.setUser(lockedUser);
        timeEntry.setWorkDate(today);
        timeEntry.setCheckInTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.OPEN);
        timeEntry.setManualEntry(false);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    @Transactional
    public TimeEntryResponse lunchOut() {
        User currentUser = securityService.getCurrentUser();
        lockUser(currentUser.getId());

        TimeEntry timeEntry = getTodayEntryEntity(currentUser.getId());

        if (timeEntry.getCheckInTime() == null) {
            throw new BadRequestException("You must check in before starting lunch.");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new BadRequestException("You have already checked out for today.");
        }

        if (timeEntry.getLunchOutTime() != null) {
            throw new BadRequestException("Lunch has already been started.");
        }

        timeEntry.setLunchOutTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.LUNCH);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    @Transactional
    public TimeEntryResponse lunchIn() {
        User currentUser = securityService.getCurrentUser();
        lockUser(currentUser.getId());

        TimeEntry timeEntry = getTodayEntryEntity(currentUser.getId());

        if (timeEntry.getCheckInTime() == null) {
            throw new BadRequestException("You must check in before ending lunch.");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new BadRequestException("You have already checked out for today.");
        }

        if (timeEntry.getLunchOutTime() == null) {
            throw new BadRequestException("Lunch has not been started.");
        }

        if (timeEntry.getLunchInTime() != null) {
            throw new BadRequestException("Lunch has already been ended.");
        }

        timeEntry.setLunchInTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.OPEN);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    @Transactional
    public TimeEntryResponse checkOut() {
        User currentUser = securityService.getCurrentUser();
        lockUser(currentUser.getId());

        Long userId = currentUser.getId();
        TimeEntry timeEntry = getTodayEntryEntity(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new BadRequestException("You must check in before checking out.");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new BadRequestException("You have already checked out for today.");
        }

        if (timeEntry.getLunchOutTime() != null && timeEntry.getLunchInTime() == null) {
            throw new BadRequestException("You cannot check out while lunch is active.");
        }

        timeEntry.setCheckOutTime(LocalDateTime.now());

        workScheduleRepository.findByUserIdAndWorkDate(userId, timeEntry.getWorkDate())
                .ifPresent(schedule -> applyCalculatedFields(timeEntry, schedule));

        timeEntry.setStatus(TimeEntryStatus.COMPLETED);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    public TimeEntryResponse getTodayEntry() {
        User currentUser = securityService.getCurrentUser();
        return toResponse(getTodayEntryEntity(currentUser.getId()));
    }

    public Page<TimeEntryResponse> getHistory(Pageable pageable) {
        User currentUser = securityService.getCurrentUser();
        Pageable validatedPageable = buildValidatedHistoryPageable(pageable);

        return timeEntryRepository.findByUserId(currentUser.getId(), validatedPageable)
                .map(this::toResponse);
    }

    public FlexBalanceResponse getFlexBalance() {
        User currentUser = securityService.getCurrentUser();
        Long userId = currentUser.getId();

        Long totalFlexMinutes = timeEntryRepository.sumFlexMinutesByUserId(userId);
        Long safeTotalFlexMinutes = totalFlexMinutes != null ? totalFlexMinutes : 0L;

        return new FlexBalanceResponse(userId, safeTotalFlexMinutes);
    }

    @Transactional
    public TimeEntryResponse registerManualEntry(ManualTimeEntryRequest request) {
        User currentUser = securityService.getCurrentUser();
        User lockedUser = lockUser(currentUser.getId());
        LocalDate today = LocalDate.now();

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(lockedUser.getId(), request.getWorkDate())
                .orElse(null);

        validateManualRequest(request, today, existingEntry);

        return saveManualEntry(lockedUser, request, existingEntry);
    }

    @Transactional
    public TimeEntryResponse registerManualEntryAsAdmin(Long userId, ManualTimeEntryRequest request) {
        securityService.validateAdminAccess();

        User targetUser = lockUser(userId);

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(targetUser.getId(), request.getWorkDate())
                .orElse(null);

        validateAdminManualRequest(request, existingEntry);

        return saveManualEntry(targetUser, request, existingEntry);
    }

    private Pageable buildValidatedHistoryPageable(Pageable pageable) {
        Sort requestedSort = pageable.getSort().isSorted() ? pageable.getSort() : DEFAULT_HISTORY_SORT;

        for (Sort.Order order : requestedSort) {
            if (!ALLOWED_HISTORY_SORT_FIELD_SET.contains(order.getProperty())) {
                throw new BadRequestException(
                        "Sorting is only allowed for fields: " + String.join(", ", ALLOWED_HISTORY_SORT_FIELDS)
                );
            }
        }

        boolean includesIdSort = requestedSort.stream()
                .anyMatch(order -> "id".equals(order.getProperty()));

        Sort safeSort = includesIdSort
                ? requestedSort
                : requestedSort.and(Sort.by(Sort.Direction.DESC, "id"));

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
    }

    private TimeEntryResponse saveManualEntry(User targetUser,
                                              ManualTimeEntryRequest request,
                                              TimeEntry existingEntry) {
        WorkSchedule schedule = workScheduleRepository.findByUserIdAndWorkDate(targetUser.getId(), request.getWorkDate())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No work schedule found for " + request.getWorkDate()
                ));

        TimeEntry timeEntry = existingEntry != null ? existingEntry : new TimeEntry();

        timeEntry.setUser(targetUser);
        timeEntry.setWorkDate(request.getWorkDate());
        timeEntry.setCheckInTime(request.getCheckInTime());
        timeEntry.setLunchOutTime(request.getLunchOutTime());
        timeEntry.setLunchInTime(request.getLunchInTime());
        timeEntry.setCheckOutTime(request.getCheckOutTime());
        timeEntry.setComment(request.getComment() != null ? request.getComment().trim() : null);
        timeEntry.setManualEntry(true);

        applyCalculatedFields(timeEntry, schedule);
        timeEntry.setStatus(TimeEntryStatus.MANUAL);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    private void validateManualRequest(ManualTimeEntryRequest request,
                                       LocalDate today,
                                       TimeEntry existingEntry) {
        if (request.getWorkDate() == null) {
            throw new BadRequestException("Work date is required.");
        }

        validateManualTimeRange(request);

        LocalDate earliestAllowedDate = today.minusDays(MANUAL_ENTRY_MAX_DAYS_BACK);

        if (request.getWorkDate().isAfter(today)) {
            throw new BadRequestException("Manual entries cannot be registered for a future date.");
        }

        if (request.getWorkDate().isBefore(earliestAllowedDate)) {
            throw new BadRequestException(
                    "Manual entries can only be registered up to " + MANUAL_ENTRY_MAX_DAYS_BACK + " days back."
            );
        }

        if (existingEntry != null) {
            throw new BadRequestException("A time entry already exists for this date and cannot be overwritten.");
        }
    }

    private void validateAdminManualRequest(ManualTimeEntryRequest request,
                                            TimeEntry existingEntry) {
        if (request.getWorkDate() == null) {
            throw new BadRequestException("Work date is required.");
        }

        validateManualTimeRange(request);

        if (existingEntry != null) {
            throw new BadRequestException("A time entry already exists for this date and cannot be overwritten.");
        }
    }

    private void validateManualTimeRange(ManualTimeEntryRequest request) {
        LocalDate workDate = request.getWorkDate();
        LocalDateTime checkInTime = request.getCheckInTime();
        LocalDateTime lunchOutTime = request.getLunchOutTime();
        LocalDateTime lunchInTime = request.getLunchInTime();
        LocalDateTime checkOutTime = request.getCheckOutTime();

        if (checkInTime == null || checkOutTime == null) {
            return;
        }

        if (!checkOutTime.isAfter(checkInTime) && !checkOutTime.isEqual(checkInTime)) {
            throw new BadRequestException("Check-out time cannot be before check-in time.");
        }

        if (!checkInTime.toLocalDate().isEqual(workDate)) {
            throw new BadRequestException("Work date must match the check-in date.");
        }

        LocalDate checkOutDate = checkOutTime.toLocalDate();
        if (!checkOutDate.isEqual(workDate) && !checkOutDate.isEqual(workDate.plusDays(1))) {
            throw new BadRequestException("Check-out time must be on the work date or the following day.");
        }

        boolean hasLunchOut = lunchOutTime != null;
        boolean hasLunchIn = lunchInTime != null;

        if (hasLunchOut != hasLunchIn) {
            throw new BadRequestException("Lunch out and lunch in must both be provided or both be omitted.");
        }

        if (!hasLunchOut) {
            return;
        }

        if (lunchOutTime.isBefore(checkInTime)) {
            throw new BadRequestException("Lunch out time cannot be before check-in time.");
        }

        if (lunchInTime.isBefore(lunchOutTime)) {
            throw new BadRequestException("Lunch in time cannot be before lunch out time.");
        }

        if (lunchInTime.isAfter(checkOutTime)) {
            throw new BadRequestException("Lunch in time cannot be after check-out time.");
        }
    }

    private void applyCalculatedFields(TimeEntry timeEntry, WorkSchedule schedule) {
        int lunchMinutes = flexCalculationService.calculateLunchMinutes(timeEntry);
        int extraLunchMinutes = flexCalculationService.calculateExtraLunchMinutes(
                lunchMinutes,
                schedule.getPaidLunchMinutes()
        );
        int workedMinutes = flexCalculationService.calculateWorkedMinutes(
                timeEntry.getCheckInTime(),
                timeEntry.getCheckOutTime(),
                lunchMinutes
        );
        int flexMinutes = flexCalculationService.calculateFlexMinutes(schedule, timeEntry);

        timeEntry.setLunchMinutes(lunchMinutes);
        timeEntry.setExtraLunchMinutes(extraLunchMinutes);
        timeEntry.setWorkedMinutes(workedMinutes);
        timeEntry.setFlexMinutes(flexMinutes);
    }

    private TimeEntry getTodayEntryEntity(Long userId) {
        return timeEntryRepository.findByUserIdAndWorkDate(userId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No time entry found for today."));
    }

    private User lockUser(Long userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private TimeEntryResponse toResponse(TimeEntry timeEntry) {
        return new TimeEntryResponse(
                timeEntry.getId(),
                timeEntry.getUser().getId(),
                timeEntry.getWorkDate(),
                timeEntry.getCheckInTime(),
                timeEntry.getLunchOutTime(),
                timeEntry.getLunchInTime(),
                timeEntry.getCheckOutTime(),
                timeEntry.getWorkedMinutes(),
                timeEntry.getLunchMinutes(),
                timeEntry.getExtraLunchMinutes(),
                timeEntry.getFlexMinutes(),
                timeEntry.isManualEntry(),
                timeEntry.getComment(),
                timeEntry.getStatus()
        );
    }
}