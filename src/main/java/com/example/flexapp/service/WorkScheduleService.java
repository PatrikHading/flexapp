package com.example.flexapp.service;

import com.example.flexapp.dto.RecurringWorkScheduleRequest;
import com.example.flexapp.dto.WorkScheduleRequest;
import com.example.flexapp.dto.WorkScheduleResponse;
import com.example.flexapp.entity.User;
import com.example.flexapp.entity.WorkSchedule;
import com.example.flexapp.exception.BadRequestException;
import com.example.flexapp.exception.ResourceNotFoundException;
import com.example.flexapp.repository.WorkScheduleRepository;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.security.SecurityService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class WorkScheduleService {

    private static final long MAX_RECURRING_SCHEDULE_DAYS = 366;

    private final WorkScheduleRepository workScheduleRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public WorkScheduleService(WorkScheduleRepository workScheduleRepository,
                               UserRepository userRepository,
                               SecurityService securityService) {
        this.workScheduleRepository = workScheduleRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    public WorkSchedule createOrUpdateSchedule(Long userId,
                                               LocalDate workDate,
                                               LocalTime plannedStartTime,
                                               LocalTime plannedEndTime,
                                               Integer paidLunchMinutes) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        validateScheduleInput(plannedStartTime, plannedEndTime, paidLunchMinutes);

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
        securityService.validateAdminAccess();

        WorkSchedule saved = createOrUpdateSchedule(
                userId,
                request.getWorkDate(),
                request.getPlannedStartTime(),
                request.getPlannedEndTime(),
                request.getPaidLunchMinutes()
        );

        return toResponse(saved);
    }

    public List<WorkScheduleResponse> createRecurringSchedules(Long userId, RecurringWorkScheduleRequest request) {
        securityService.validateAdminAccess();

        validateRecurringScheduleRequest(request);

        List<WorkScheduleResponse> responses = new java.util.ArrayList<>();

        LocalDate currentDate = request.getStartDate();

        while (!currentDate.isAfter(request.getEndDate())) {
            if (isWeekday(currentDate)) {
                WorkSchedule schedule = createOrUpdateSchedule(
                        userId,
                        currentDate,
                        request.getPlannedStartTime(),
                        request.getPlannedEndTime(),
                        request.getPaidLunchMinutes()
                );
                responses.add(toResponse(schedule));
            }
            currentDate = currentDate.plusDays(1);
        }

        return responses;
    }

    public WorkScheduleResponse getScheduleForDate(Long userId, LocalDate workDate) {
        securityService.validateUserAccess(userId);

        WorkSchedule schedule = workScheduleRepository.findByUserIdAndWorkDate(userId, workDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No work schedule found for userId " + userId + " and date " + workDate
                ));

        return toResponse(schedule);
    }

    public List<WorkScheduleResponse> getSchedules(Long userId) {
        securityService.validateUserAccess(userId);

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

    private void validateRecurringScheduleRequest(RecurringWorkScheduleRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new BadRequestException("Start date and end date are required.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be the same as or after start date.");
        }

        long requestedDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        if (requestedDays > MAX_RECURRING_SCHEDULE_DAYS) {
            throw new BadRequestException(
                    "Recurring schedules can span at most " + MAX_RECURRING_SCHEDULE_DAYS + " days."
            );
        }

        validateScheduleInput(
                request.getPlannedStartTime(),
                request.getPlannedEndTime(),
                request.getPaidLunchMinutes()
        );
    }

    private boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek() != java.time.DayOfWeek.SATURDAY
                && date.getDayOfWeek() != java.time.DayOfWeek.SUNDAY;
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