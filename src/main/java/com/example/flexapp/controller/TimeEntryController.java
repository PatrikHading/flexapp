package com.example.flexapp.controller;

import com.example.flexapp.dto.FlexBalanceResponse;
import com.example.flexapp.dto.ManualTimeEntryRequest;
import com.example.flexapp.dto.TimeEntryResponse;
import com.example.flexapp.service.TimeEntryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/time")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    @PostMapping("/check-in")
    public TimeEntryResponse checkIn() {
        return timeEntryService.checkIn();
    }

    @PostMapping("/lunch-out")
    public TimeEntryResponse lunchOut() {
        return timeEntryService.lunchOut();
    }

    @PostMapping("/lunch-in")
    public TimeEntryResponse lunchIn() {
        return timeEntryService.lunchIn();
    }

    @PostMapping("/check-out")
    public TimeEntryResponse checkOut() {
        return timeEntryService.checkOut();
    }

    @PostMapping("/manual")
    public TimeEntryResponse registerManualEntry(@Valid @RequestBody ManualTimeEntryRequest request) {
        return timeEntryService.registerManualEntry(request);
    }

    @GetMapping("/today")
    public TimeEntryResponse getTodayEntry() {
        return timeEntryService.getTodayEntry();
    }

    @GetMapping("/history")
    public Page<TimeEntryResponse> getHistory(
            @PageableDefault(sort = "workDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return timeEntryService.getHistory(pageable);
    }

    @GetMapping("/flex-balance")
    public FlexBalanceResponse getFlexBalance() {
        return timeEntryService.getFlexBalance();
    }
}