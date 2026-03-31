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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimeEntryService {

    private static final int MANUAL_ENTRY_MAX_DAYS_BACK = 7;

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

    public TimeEntryResponse registerManualEntry(ManualTimeEntryRequest request) {
        User currentUser = securityService.getCurrentUser();
        LocalDate today = LocalDate.now();

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(currentUser.getId(), request.getWorkDate())
                .orElse(null);

        validateManualRequest(request, today, existingEntry);

        return saveManualEntry(currentUser, request, existingEntry);
    }

    public TimeEntryResponse registerManualEntryAsAdmin(Long userId, ManualTimeEntryRequest request) {
        securityService.validateAdminAccess();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(userId, request.getWorkDate())
                .orElse(null);

        validateAdminManualRequest(request, existingEntry);

        return saveManualEntry(targetUser, request, existingEntry);
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

    public TimeEntryResponse getTodayEntry() {
        User currentUser = securityService.getCurrentUser();
        return toResponse(getTodayEntryEntity(currentUser.getId()));
    }

    public List<TimeEntryResponse> getHistory() {
        User currentUser = securityService.getCurrentUser();

        return timeEntryRepository.findByUserIdOrderByWorkDateDesc(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FlexBalanceResponse getFlexBalance() {
        User currentUser = securityService.getCurrentUser();
        Long userId = currentUser.getId();

        int totalFlexMinutes = timeEntryRepository.findByUserIdOrderByWorkDateDesc(userId)
                .stream()
                .map(TimeEntry::getFlexMinutes)
                .filter(flexMinutes -> flexMinutes != null)
                .mapToInt(Integer::intValue)
                .sum();

        return new FlexBalanceResponse(userId, totalFlexMinutes);
    }

    private void validateManualRequest(ManualTimeEntryRequest request,
                                       LocalDate today,
                                       TimeEntry existingEntry) {
        if (request.getWorkDate() == null) {
            throw new BadRequestException("Work date is required.");
        }

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

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("Comment is required for manual entries.");
        }

        if (request.getComment().trim().length() < 10) {
            throw new BadRequestException("Comment must be at least 10 characters long.");
        }

        validateCommonManualRequest(request);
    }

    private void validateAdminManualRequest(ManualTimeEntryRequest request,
                                            TimeEntry existingEntry) {
        if (request.getWorkDate() == null) {
            throw new BadRequestException("Work date is required.");
        }

        if (existingEntry != null) {
            throw new BadRequestException("A time entry already exists for this date and cannot be overwritten.");
        }

        validateCommonManualRequest(request);
    }

    private void validateCommonManualRequest(ManualTimeEntryRequest request) {
        if (request.getCheckInTime() == null || request.getCheckOutTime() == null) {
            throw new BadRequestException("Check-in time and check-out time are required.");
        }

        if (!request.getCheckInTime().toLocalDate().equals(request.getWorkDate())) {
            throw new BadRequestException("Check-in time must match the selected work date.");
        }

        if (!request.getCheckOutTime().toLocalDate().equals(request.getWorkDate())) {
            throw new BadRequestException("Check-out time must match the selected work date.");
        }

        if (!request.getCheckOutTime().isAfter(request.getCheckInTime())) {
            throw new BadRequestException("Check-out time must be after check-in time.");
        }

        if (request.getLunchOutTime() != null && request.getLunchInTime() == null) {
            throw new BadRequestException("Lunch in time is required when lunch out time is provided.");
        }

        if (request.getLunchOutTime() == null && request.getLunchInTime() != null) {
            throw new BadRequestException("Lunch out time is required when lunch in time is provided.");
        }

        if (request.getLunchOutTime() != null
                && !request.getLunchOutTime().toLocalDate().equals(request.getWorkDate())) {
            throw new BadRequestException("Lunch out time must match the selected work date.");
        }

        if (request.getLunchInTime() != null
                && !request.getLunchInTime().toLocalDate().equals(request.getWorkDate())) {
            throw new BadRequestException("Lunch in time must match the selected work date.");
        }

        if (request.getLunchOutTime() != null && request.getLunchInTime() != null) {
            if (!request.getLunchOutTime().isAfter(request.getCheckInTime())) {
                throw new BadRequestException("Lunch out time must be after check-in time.");
            }

            if (!request.getLunchInTime().isAfter(request.getLunchOutTime())) {
                throw new BadRequestException("Lunch in time must be after lunch out time.");
            }

            if (!request.getCheckOutTime().isAfter(request.getLunchInTime())) {
                throw new BadRequestException("Check-out time must be after lunch in time.");
            }
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