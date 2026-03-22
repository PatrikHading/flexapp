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

    public DataInitializer(UserRepository userRepository, WorkScheduleService workScheduleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workScheduleService = workScheduleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String email = "admin@flexapp.com";
        String rawPassword = "temp123";

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setFirstName("Admin");
            newUser.setLastName("User");
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setRole(Role.ADMIN);
            newUser.setActive(true);
            return userRepository.save(newUser);
        });

        if (!user.getPassword().startsWith("$2a$") &&
                !user.getPassword().startsWith("$2b$") &&
                !user.getPassword().startsWith("$2y$")) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }

        workScheduleService.createOrUpdateSchedule(
                user.getId(),
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                30);

        System.out.println("Admin user exists: " + email);
        System.out.println("Admin password: " + rawPassword);
        System.out.println("Work schedule exists for today.");
        System.out.println("Admin user id: " + user.getId());

    }
}

