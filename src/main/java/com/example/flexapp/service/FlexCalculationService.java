package com.example.flexapp.service;

import com.example.flexapp.entity.TimeEntry;
import com.example.flexapp.entity.WorkSchedule;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class FlexCalculationService {

    private static final LocalDate PLANNED_MINUTES_ANCHOR_DATE = LocalDate.of(2000, 1, 1);

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
        int plannedMinutes = calculatePlannedWorkedMinutes(schedule);

        int actualLunchMinutes = calculateLunchMinutes(timeEntry);
        int workedMinutes = calculateWorkedMinutes(
                timeEntry.getCheckInTime(),
                timeEntry.getCheckOutTime(),
                actualLunchMinutes
        );

        return workedMinutes - plannedMinutes;
    }

    private int calculatePlannedWorkedMinutes(WorkSchedule schedule) {
        LocalDateTime plannedStart = PLANNED_MINUTES_ANCHOR_DATE.atTime(schedule.getPlannedStartTime());
        LocalDateTime plannedEnd = PLANNED_MINUTES_ANCHOR_DATE.atTime(schedule.getPlannedEndTime());

        if (!plannedEnd.isAfter(plannedStart)) {
            plannedEnd = plannedEnd.plusDays(1);
        }

        return Math.toIntExact(Duration.between(plannedStart, plannedEnd).toMinutes())
                - schedule.getPaidLunchMinutes();
    }
}