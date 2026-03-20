package com.example.flexapp.service;

import com.example.flexapp.entity.User;
import com.example.flexapp.entity.WorkSchedule;
import com.example.flexapp.repository.WorkScheduleRepository;
import com.example.flexapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;

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

    public WorkSchedule getScheduleForDate(Long userId, LocalDate workDate) {
        return workScheduleRepository.findByUserIdAndWorkDate(userId, workDate)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No work schedule found for userId " + userId + " and date " + workDate));
    }

    private int calculateExpectedWorkMinutes(LocalTime plannedStartTime, LocalTime plannedEndTime) {
        return plannedEndTime.getHour() * 60 + plannedEndTime.getMinute()
                - (plannedStartTime.getHour() * 60 + plannedStartTime.getMinute());
    }
}
