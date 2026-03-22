package com.example.flexapp.service;

import com.example.flexapp.entity.TimeEntry;
import com.example.flexapp.entity.User;
import com.example.flexapp.enums.TimeEntryStatus;
import com.example.flexapp.repository.TimeEntryRepository;
import com.example.flexapp.repository.UserRepository;
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

    public TimeEntryService(TimeEntryRepository timeEntryRepository, UserRepository userRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
    }

    public TimeEntry checkIn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        LocalDate today = LocalDate.now();

        TimeEntry existingEntry = timeEntryRepository.findByUserIdAndWorkDate(userId, today).orElse(null);

        if (existingEntry != null && existingEntry.getCheckInTime() != null) {
            throw new IllegalStateException("User is already checked in for today");
        }

        TimeEntry timeEntry = existingEntry != null ? existingEntry : new TimeEntry();

        timeEntry.setUser(user);
        timeEntry.setWorkDate(today);
        timeEntry.setCheckInTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.OPEN);
        timeEntry.setManualEntry(false);

        return timeEntryRepository.save(timeEntry);
    }

    public TimeEntry lunchOut(Long userId) {
        TimeEntry timeEntry = getTodayEntry(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new IllegalStateException("User must check in before starting lunch");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new IllegalStateException("User has already checked out for today");
        }

        if (timeEntry.getLunchOutTime() != null) {
            throw new IllegalStateException("Lunch has already been started for today");
        }

        timeEntry.setLunchOutTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.LUNCH);

        return timeEntryRepository.save(timeEntry);
    }

    public TimeEntry lunchIn(Long userId) {
        TimeEntry timeEntry = getTodayEntry(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new IllegalStateException("User must check in before ending lunch");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new IllegalStateException("User has already checked out for today");
        }

        if (timeEntry.getLunchOutTime() == null) {
            throw new IllegalStateException("Lunch has not been started for today");
        }

        if (timeEntry.getLunchInTime() != null) {
            throw new IllegalStateException("Lunch has already been ended.");
        }

        timeEntry.setLunchInTime(LocalDateTime.now());
        timeEntry.setStatus(TimeEntryStatus.OPEN);

        return timeEntryRepository.save(timeEntry);
    }

    public TimeEntry checkOut(Long userId) {
        TimeEntry timeEntry = getTodayEntry(userId);

        if (timeEntry.getCheckInTime() == null) {
            throw new IllegalStateException("User must check in before checking out");
        }

        if (timeEntry.getCheckOutTime() != null) {
            throw new IllegalStateException("User has already checked out for today");
        }

        if (timeEntry.getLunchOutTime() != null && timeEntry.getLunchInTime() == null) {
            throw new IllegalStateException("User cannot check out while lunch is still in progress");
        }

        LocalDateTime checkOutTime = LocalDateTime.now();
        timeEntry.setCheckOutTime(checkOutTime);

        int lunchMinutes = calculateLunchMinutes(timeEntry);
        int workedMinutes = calculateWorkedMinutes(timeEntry.getCheckInTime(), checkOutTime, lunchMinutes);

        timeEntry.setLunchMinutes(lunchMinutes);
        timeEntry.setWorkedMinutes(workedMinutes);
        timeEntry.setStatus(TimeEntryStatus.COMPLETED);

        return timeEntryRepository.save(timeEntry);
    }

    public TimeEntry getTodayEntry(Long userId) {
        return timeEntryRepository.findByUserIdAndWorkDate(userId, LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No time entry found for today."));
    }

    private int calculateLunchMinutes(TimeEntry timeEntry) {
        if (timeEntry.getLunchOutTime() == null || timeEntry.getLunchInTime() == null) {
            return 0;
        }
        return (int) Duration.between(timeEntry.getLunchOutTime(), timeEntry.getLunchInTime()).toMinutes();
    }

    private int calculateWorkedMinutes(LocalDateTime checkInTime, LocalDateTime checkOutTime, int lunchMinutes) {
        int totalMinutes = (int) Duration.between(checkInTime, checkOutTime).toMinutes();
        return totalMinutes - lunchMinutes;
    }

}
