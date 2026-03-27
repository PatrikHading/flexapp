package com.example.flexapp.controller;

import com.example.flexapp.dto.FlexBalanceResponse;
import com.example.flexapp.dto.ManualTimeEntryRequest;
import com.example.flexapp.dto.TimeEntryResponse;
import com.example.flexapp.service.TimeEntryService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/time")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    @PostMapping("/{userId}/check-in")
    public TimeEntryResponse checkIn(@PathVariable Long userId) {
        return timeEntryService.checkIn(userId);
    }

    @PostMapping("/{userId}/lunch-out")
    public TimeEntryResponse lunchOut(@PathVariable Long userId) {
        return timeEntryService.lunchOut(userId);
    }

    @PostMapping("/{userId}/lunch-in")
    public TimeEntryResponse lunchIn(@PathVariable Long userId) {
        return timeEntryService.lunchIn(userId);
    }

    @PostMapping("/{userId}/check-out")
    public TimeEntryResponse checkOut(@PathVariable Long userId) {
        return timeEntryService.checkOut(userId);
    }

    @PostMapping("/{userId}/manual")
    public TimeEntryResponse registerManualEntry(@PathVariable Long userId,
                                                 @Valid @RequestBody ManualTimeEntryRequest request) {
        return timeEntryService.registerManualEntry(userId, request);
    }

    @GetMapping("/{userId}/today")
    public TimeEntryResponse getTodayEntry(@PathVariable Long userId) {
        return timeEntryService.getTodayEntry(userId);
    }

    @GetMapping("/{userId}/history")
    public List<TimeEntryResponse> getHistory(@PathVariable Long userId) {
        return timeEntryService.getHistory(userId);
    }

    @GetMapping("/{userId}/flex-balance")
    public FlexBalanceResponse getFlexBalance(@PathVariable Long userId) {
        return timeEntryService.getFlexBalance(userId);
    }


}
