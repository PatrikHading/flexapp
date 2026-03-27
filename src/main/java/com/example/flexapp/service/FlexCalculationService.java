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
        int plannedMinutes = (int) Duration.between(
                schedule.getPlannedStartTime(),
                schedule.getPlannedEndTime()
        ).toMinutes() - schedule.getPaidLunchMinutes();

        int actualLunchMinutes = calculateLunchMinutes(timeEntry);
        int workedMinutes = calculateWorkedMinutes(
                timeEntry.getCheckInTime(),
                timeEntry.getCheckOutTime(),
                actualLunchMinutes
        );

        return workedMinutes - plannedMinutes;
    }
}
