package com.example.flexapp.config;

import com.example.flexapp.entity.User;
import com.example.flexapp.enums.Role;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.service.WorkScheduleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkScheduleService workScheduleService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           WorkScheduleService workScheduleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workScheduleService = workScheduleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createOrUpdateUser(
                "admin@flexapp.com",
                "Admin",
                "User",
                "temp123",
                Role.ADMIN
        );

        createOrUpdateUser(
                "user@flexapp.com",
                "Normal",
                "User",
                "temp123",
                Role.USER
        );

        User admin = userRepository.findByEmail("admin@flexapp.com").orElseThrow();
        User user = userRepository.findByEmail("user@flexapp.com").orElseThrow();

        workScheduleService.createOrUpdateSchedule(
            admin.getId(),
            LocalDate.now(),
            LocalTime.of(8, 0),
            LocalTime.of(16, 0),
            30
        );

        workScheduleService.createOrUpdateSchedule(
                user.getId(),
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                30
        );

        System.out.println("Admin user: admin@flexapp.com / temp123 / id=" + admin.getId());
        System.out.println("Normal user: user@flexapp.com / temp123 / id=" + user.getId());
    }

    private void createOrUpdateUser(String email,
                                    String firstName,
                                    String lastName,
                                    String rawPassword,
                                    Role role) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            return newUser;
        });

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setActive(true);

        if (user.getPassword() == null
                || !user.getPassword().startsWith("$2a$")
                && !user.getPassword().startsWith("$2b$")
                && !user.getPassword().startsWith("$2y$")) {
            user.setPassword(passwordEncoder.encode(rawPassword));

        }
        userRepository.save(user);
    }
}

