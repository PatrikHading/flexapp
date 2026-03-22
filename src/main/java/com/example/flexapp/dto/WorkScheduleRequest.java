package com.example.flexapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class WorkScheduleRequest {

    private LocalDate workDate;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private Integer paidLunchMinutes;
}
