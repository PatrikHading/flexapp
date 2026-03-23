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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimeEntryService {

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

    public TimeEntryResponse checkIn(Long userId) {
        securityService.validateUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDate today = LocalDate.now();

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(userId, today).orElse(null);

        if (existingEntry != null && existingEntry.getCheckInTime() != null) {
            throw new BadRequestException("User is already checked in for today.");
        }

        TimeEntry timeEntry = existingEntry != null ? existingEntry : new TimeEntry();

        timeEntry.setUser(user);
        timeEntry.setWorkDate(today);
        timeEntry.setCheckInTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.OPEN);
        timeEntry.setManualEntry(false);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    public TimeEntryResponse lunchOut(Long userId) {
        securityService.validateUserAccess(userId);

        TimeEntry timeEntry = getTodayEntryEntity(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new BadRequestException("User must check in before starting lunch.");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new BadRequestException("User has already checked out for today.");
        }

        if (timeEntry.getLunchOutTime() != null) {
            throw new BadRequestException("Lunch has already been started.");
        }

        timeEntry.setLunchOutTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.LUNCH);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    public TimeEntryResponse lunchIn(Long userId) {
        securityService.validateUserAccess(userId);

        TimeEntry timeEntry = getTodayEntryEntity(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new BadRequestException("User must check in before ending lunch.");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new BadRequestException("User has already checked out for today.");
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

    public TimeEntryResponse checkOut(Long userId) {
        securityService.validateUserAccess(userId);

        TimeEntry timeEntry = getTodayEntryEntity(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new BadRequestException("User must check in before checking out.");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new BadRequestException("User has already checked out for today.");
        }

        if (timeEntry.getLunchOutTime() != null && timeEntry.getLunchInTime() == null) {
            throw new BadRequestException("User cannot check out while lunch is active.");
        }

        WorkSchedule schedule = workScheduleRepository.findByUserIdAndWorkDate(userId, timeEntry.getWorkDate())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No work schedule found for userId " + userId + " and date " + timeEntry.getWorkDate()
                ));

        LocalDateTime checkOutTime = LocalDateTime.now();
        timeEntry.setCheckOutTime(checkOutTime);

        applyCalculatedFields(timeEntry, schedule);
        timeEntry.setStatus(TimeEntryStatus.COMPLETED);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    public TimeEntryResponse registerManualEntry(Long userId, ManualTimeEntryRequest request) {
        securityService.validateUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        validateManualRequest(request);

        WorkSchedule schedule = workScheduleRepository.findByUserIdAndWorkDate(userId, request.getWorkDate())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No work schedule found for userId " + userId + " and date " + request.getWorkDate()
                ));

        TimeEntry timeEntry = timeEntryRepository.findByUserIdAndWorkDate(userId, request.getWorkDate())
                .orElseGet(TimeEntry::new);

        timeEntry.setUser(user);
        timeEntry.setWorkDate(request.getWorkDate());
        timeEntry.setCheckInTime(request.getCheckInTime());
        timeEntry.setLunchOutTime(request.getLunchOutTime());
        timeEntry.setLunchInTime(request.getLunchInTime());
        timeEntry.setCheckOutTime(request.getCheckOutTime());
        timeEntry.setComment(request.getComment());
        timeEntry.setManualEntry(true);

        applyCalculatedFields(timeEntry, schedule);
        timeEntry.setStatus(TimeEntryStatus.MANUAL);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    public TimeEntryResponse getTodayEntry(Long userId) {
        securityService.validateUserAccess(userId);
        return toResponse(getTodayEntryEntity(userId));
    }

    public List<TimeEntryResponse> getHistory(Long userId) {
        securityService.validateUserAccess(userId);
        validateUserExists(userId);

        return timeEntryRepository.findByUserIdOrderByWorkDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FlexBalanceResponse getFlexBalance(Long userId) {
        securityService.validateUserAccess(userId);
        validateUserExists(userId);

        int totalFlexMinutes = timeEntryRepository.findByUserIdOrderByWorkDateDesc(userId)
                .stream()
                .map(TimeEntry::getFlexMinutes)
                .filter(flexMinutes -> flexMinutes != null)
                .mapToInt(Integer::intValue)
                .sum();

        return new FlexBalanceResponse(userId, totalFlexMinutes);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }

    private void validateManualRequest(ManualTimeEntryRequest request) {
        if (request.getWorkDate() == null) {
            throw new BadRequestException("Work date is required.");
        }

        if (request.getCheckInTime() == null || request.getCheckOutTime() == null) {
            throw new BadRequestException("Check-in time and check-out time are required.");
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