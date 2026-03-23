package com.example.flexapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ManualTimeEntryRequest {

    private LocalDate workDate;
    private LocalDateTime checkInTime;
    private LocalDateTime lunchOutTime;
    private LocalDateTime lunchInTime;
    private LocalDateTime checkOutTime;
    private String comment;
}
