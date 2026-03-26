package com.example.flexapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class RecurringWorkScheduleRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private Integer paidLunchMinutes;

}
