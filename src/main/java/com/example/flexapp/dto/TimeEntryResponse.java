package com.example.flexapp.dto;

import com.example.flexapp.enums.TimeEntryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TimeEntryResponse {

    private Long id;
    private Long userId;
    private LocalDate workDate;
    private LocalDateTime checkInTime;
    private LocalDateTime lunchOutTime;
    private LocalDateTime lunchInTime;
    private LocalDateTime checkOutTime;
    private Integer workedMinutes;
    private Integer lunchMinutes;
    private Integer extraLunchMinutes;
    private Integer flexMinutes;
    private boolean manualEntry;
    private String comment;
    private TimeEntryStatus status;


}
