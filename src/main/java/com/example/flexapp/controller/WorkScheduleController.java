package com.example.flexapp.controller;

import com.example.flexapp.dto.WorkScheduleRequest;
import com.example.flexapp.dto.WorkScheduleResponse;
import com.example.flexapp.service.WorkScheduleService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping("/{userId}")
    public WorkScheduleResponse createOrUpdateSchedule(@PathVariable Long userId,
                                                       @RequestBody WorkScheduleRequest request) {
        return workScheduleService.createOrUpdateSchedule(userId, request);
    }

    @GetMapping("/{userId}/today")
    public WorkScheduleResponse getTodaySchedule(@PathVariable Long userId) {
        return workScheduleService.getScheduleForDate(userId, LocalDate.now());
    }

    @GetMapping("/{userId}")
    public List<WorkScheduleResponse> getSchedules(@PathVariable Long userId) {
        return workScheduleService.getSchedules(userId);
    }
}