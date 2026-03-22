package com.example.flexapp.service;

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
import com.example.flexapp.service.FlexCalculationService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final FlexCalculationService flexCalculationService;

    public TimeEntryService(TimeEntryRepository timeEntryRepository,
                            UserRepository userRepository,
                            WorkScheduleRepository workScheduleRepository,
                            FlexCalculationService flexCalculationService) {
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
        this.workScheduleRepository = workScheduleRepository;
        this.flexCalculationService = flexCalculationService;
    }

    public TimeEntryResponse checkIn(Long userId) {
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

        int lunchMinutes = flexCalculationService.calculateLunchMinutes(timeEntry);
        int extraLunchMinutes = flexCalculationService.calculateExtraLunchMinutes(
                lunchMinutes,
                schedule.getPaidLunchMinutes()
        );
        int workedMinutes = flexCalculationService.calculateWorkedMinutes(
                timeEntry.getCheckInTime(),
                checkOutTime,
                lunchMinutes
        );
        int flexMinutes = flexCalculationService.calculateFlexMinutes(schedule, timeEntry);

        timeEntry.setLunchMinutes(lunchMinutes);
        timeEntry.setExtraLunchMinutes(extraLunchMinutes);
        timeEntry.setWorkedMinutes(workedMinutes);
        timeEntry.setFlexMinutes(flexMinutes);
        timeEntry.setStatus(TimeEntryStatus.COMPLETED);

        return toResponse(timeEntryRepository.save(timeEntry));
    }

    public TimeEntryResponse getTodayEntry(Long userId) {
        return toResponse(getTodayEntryEntity(userId));
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
