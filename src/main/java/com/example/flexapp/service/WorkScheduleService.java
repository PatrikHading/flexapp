package com.example.flexapp.service;

import com.example.flexapp.dto.WorkScheduleRequest;
import com.example.flexapp.dto.WorkScheduleResponse;
import com.example.flexapp.entity.User;
import com.example.flexapp.entity.WorkSchedule;
import com.example.flexapp.exception.BadRequestException;
import com.example.flexapp.exception.ResourceNotFoundException;
import com.example.flexapp.repository.WorkScheduleRepository;
import com.example.flexapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final UserRepository userRepository;

    public WorkScheduleService(WorkScheduleRepository workScheduleRepository, UserRepository userRepository) {
        this.workScheduleRepository = workScheduleRepository;
        this.userRepository = userRepository;
    }

    public WorkSchedule createOrUpdateSchedule(Long userId,
                                               LocalDate workDate,
                                               LocalTime plannedStartTime,
                                               LocalTime plannedEndTime,
                                               Integer paidLunchMinutes) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        int expectedWorkMinutes = calculateExpectedWorkMinutes(plannedStartTime, plannedEndTime);

        WorkSchedule schedule = workScheduleRepository.findByUserIdAndWorkDate(userId, workDate)
                .orElseGet(WorkSchedule::new);

        schedule.setUser(user);
        schedule.setWorkDate(workDate);
        schedule.setPlannedStartTime(plannedStartTime);
        schedule.setPlannedEndTime(plannedEndTime);
        schedule.setPaidLunchMinutes(paidLunchMinutes);
        schedule.setExpectedWorkMinutes(expectedWorkMinutes);

        return workScheduleRepository.save(schedule);
    }

    public WorkScheduleResponse createOrUpdateSchedule(Long userId, WorkScheduleRequest request) {
        WorkSchedule saved = createOrUpdateSchedule(
                userId,
                request.getWorkDate(),
                request.getPlannedStartTime(),
                request.getPlannedEndTime(),
                request.getPaidLunchMinutes()
        );

        return toResponse(saved);
    }

    public WorkScheduleResponse getScheduleForDate(Long userId, LocalDate workDate) {
        WorkSchedule schedule = workScheduleRepository.findByUserIdAndWorkDate(userId, workDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No work schedule found for userId " + userId + " and date " + workDate
                ));

        return toResponse(schedule);
    }

    public List<WorkScheduleResponse> getSchedules(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return workScheduleRepository.findByUserIdAndWorkDateBetween(
                        userId,
                        LocalDate.of(2000, 1, 1),
                        LocalDate.of(2100, 1, 1)
                )
                .stream()
                .sorted((a, b) -> b.getWorkDate().compareTo(a.getWorkDate()))
                .map(this::toResponse)
                .toList();
    }

    private void validateScheduleInput(LocalTime plannedStartTime,
                                       LocalTime plannedEndTime,
                                       Integer paidLunchMinutes) {
        if (plannedStartTime == null || plannedEndTime == null) {
            throw new BadRequestException("Planned start time and end times are required.");
        }

        if (!plannedEndTime.isAfter(plannedStartTime)) {
            throw new BadRequestException("Planned end time must be after planned start time.");
        }

        if (paidLunchMinutes == null || paidLunchMinutes < 0) {
            throw new BadRequestException("Paid lunch minutes must be 0 or greater.");
        }
    }

    private int calculateExpectedWorkMinutes(LocalTime plannedStartTime, LocalTime plannedEndTime) {
        return plannedEndTime.getHour() * 60 + plannedEndTime.getMinute()
                - (plannedStartTime.getHour() * 60 + plannedStartTime.getMinute());
    }

    private WorkScheduleResponse toResponse(WorkSchedule schedule) {
        return new WorkScheduleResponse(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getWorkDate(),
                schedule.getPlannedStartTime(),
                schedule.getPlannedEndTime(),
                schedule.getPaidLunchMinutes(),
                schedule.getExpectedWorkMinutes()
        );
    }
}
