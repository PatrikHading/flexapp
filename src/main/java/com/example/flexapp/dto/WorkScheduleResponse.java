package com.example.flexapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class WorkScheduleResponse {

    private Long id;
    private Long userId;
    private LocalDate workDate;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private Integer paidLunchMinutes;
    private Integer expectedWorkMinutes;
}
