package com.example.flexapp.service;

import com.example.flexapp.entity.TimeEntry;
import com.example.flexapp.entity.User;
import com.example.flexapp.enums.TimeEntryStatus;
import com.example.flexapp.repository.TimeEntryRepository;
import com.example.flexapp.repository.UserRepository;
import org.springframework.stereotype.Service;
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

    public TimeEntry getTodayEntry(Long userId) {
        return timeEntryRepository.findByUserIdAndWorkDate(userId, LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No time entry found for today."));
    }

}
