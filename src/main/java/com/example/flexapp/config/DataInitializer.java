package com.example.flexapp.config;

import com.example.flexapp.entity.User;
import com.example.flexapp.enums.Role;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.service.WorkScheduleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkScheduleService workScheduleService;

    public DataInitializer(UserRepository userRepository, WorkScheduleService workScheduleService) {
        this.userRepository = userRepository;
        this.workScheduleService = workScheduleService;
    }

    @Override
    public void run(String... args) {
        String email = "admin@flexapp.com";

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setFirstName("Admin");
            newUser.setLastName("User");
            newUser.setEmail(email);
            newUser.setPassword("temp123");
            newUser.setRole(Role.ADMIN);
            newUser.setActive(true);
            return userRepository.save(newUser);
        });

        workScheduleService.createOrUpdateSchedule(
                user.getId(),
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                30
        );

        System.out.println("Admin user exists: " + email);
        System.out.println("Work schedule created/updated for today");
    }
}
