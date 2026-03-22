package com.example.flexapp.controller;

import com.example.flexapp.dto.TimeEntryResponse;
import com.example.flexapp.service.TimeEntryService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{userId}/today")
    public TimeEntryResponse getTodayEntry(@PathVariable Long userId) {
        return timeEntryService.getTodayEntry(userId);
    }


}
