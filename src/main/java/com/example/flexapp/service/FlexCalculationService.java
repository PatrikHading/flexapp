package com.example.flexapp.service;

import com.example.flexapp.entity.TimeEntry;
import com.example.flexapp.entity.WorkSchedule;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class FlexCalculationService {

    public int calculateLunchMinutes(TimeEntry timeEntry) {
        if (timeEntry.getLunchOutTime() == null || timeEntry.getLunchInTime() == null) {
            return 0;
        }
        return (int) Duration.between(timeEntry.getLunchOutTime(), timeEntry.getLunchInTime()).toMinutes();
    }

    public int calculateExtraLunchMinutes(int actualLunchMinutes, int paidLunchMinutes) {
        return Math.max(actualLunchMinutes - paidLunchMinutes, 0);
    }

    public int calculateWorkedMinutes(LocalDateTime checkInTime, LocalDateTime checkOutTime, int lunchMinutes) {
        int totalMinutes = (int) Duration.between(checkInTime, checkOutTime).toMinutes();
        return totalMinutes - lunchMinutes;
    }

    public int calculateFlexMinutes(WorkSchedule schedule, TimeEntry timeEntry) {
        int earlyMinutes = calculateEarlyMinutes(schedule, timeEntry);
        int lateMinutes = calculateLateMinutes(schedule, timeEntry);
        int actualLunchMinutes = calculateLunchMinutes(timeEntry);
        int extraLunchMinutes = calculateExtraLunchMinutes(actualLunchMinutes, schedule.getPaidLunchMinutes());

        return earlyMinutes + lateMinutes + extraLunchMinutes;
    }

    private int calculateEarlyMinutes(WorkSchedule schedule, TimeEntry timeEntry) {
        LocalDateTime plannedStart = LocalDateTime.of(
                timeEntry.getWorkDate(),
                schedule.getPlannedStartTime());

        if (timeEntry.getCheckInTime().isBefore(plannedStart)) {
            return (int) Duration.between(timeEntry.getCheckInTime(), plannedStart).toMinutes();
        }
        return 0;
    }

    private int calculateLateMinutes(WorkSchedule schedule, TimeEntry timeEntry) {
        LocalDateTime plannedEnd = LocalDateTime.of(
                timeEntry.getWorkDate(),
                schedule.getPlannedEndTime());

        if (timeEntry.getCheckOutTime().isAfter(plannedEnd)) {
            return (int) Duration.between(plannedEnd, timeEntry.getCheckOutTime()).toMinutes();
        }
        return 0;
    }
}
