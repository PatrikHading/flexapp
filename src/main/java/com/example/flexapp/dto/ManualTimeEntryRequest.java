package com.example.flexapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ManualTimeEntryRequest {

    @NotNull(message = "Work date is required.")
    private LocalDate workDate;

    @NotNull(message = "Check-in time is required.")
    private LocalDateTime checkInTime;

    private LocalDateTime lunchOutTime;
    private LocalDateTime lunchInTime;

    @NotNull(message = "Check-out time is required.")
    private LocalDateTime checkOutTime;

    private String comment;
}
