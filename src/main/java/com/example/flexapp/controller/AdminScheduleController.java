package com.example.flexapp.controller;

import com.example.flexapp.dto.RecurringWorkScheduleRequest;
import com.example.flexapp.dto.WorkScheduleResponse;
import com.example.flexapp.service.WorkScheduleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users/{userId}/schedules")
public class AdminScheduleController {

    private final WorkScheduleService workScheduleService;

    public AdminScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping("/recurring")
    public List<WorkScheduleResponse> createRecurringSchedules(@PathVariable Long userId,
                                                               @RequestBody RecurringWorkScheduleRequest request) {
        return workScheduleService.createRecurringSchedules(userId, request);
    }


}
