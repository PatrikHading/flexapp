package com.example.flexapp.config;

import com.example.flexapp.entity.User;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.service.WorkScheduleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Profile("seed")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkScheduleService workScheduleService;

    public DataInitializer(UserRepository userRepository,
                           WorkScheduleService workScheduleService) {
        this.userRepository = userRepository;
        this.workScheduleService = workScheduleService;
    }

    @Override
    public void run(String... args) {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            System.out.println("No users found. Skipping schedule seed.");
            System.out.println("Create the first admin user manually in the database.");
            return;
        }

        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().plusDays(30);

        for (User user : users) {
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                workScheduleService.seedSchedule(
                        user.getId(),
                        date,
                        LocalTime.of(8, 0),
                        LocalTime.of(16, 0),
                        30
                );
            }
        }

        System.out.println("Seeded default schedules for existing users in seed profile.");
    }
}