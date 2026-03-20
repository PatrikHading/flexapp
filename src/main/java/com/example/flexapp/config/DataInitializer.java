package com.example.flexapp.config;

import com.example.flexapp.entity.User;
import com.example.flexapp.enums.Role;
import com.example.flexapp.repository.TimeEntryRepository;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.service.TimeEntryService;
import com.example.flexapp.service.WorkScheduleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkScheduleService workScheduleService;
    private final TimeEntryService timeEntryService;
    private final TimeEntryRepository timeEntryRepository;



    public DataInitializer(UserRepository userRepository,
                           WorkScheduleService workScheduleService,
                           TimeEntryService timeEntryService,
                           TimeEntryRepository timeEntryRepository) {
        this.userRepository = userRepository;
        this.workScheduleService = workScheduleService;
        this.timeEntryService = timeEntryService;
        this.timeEntryRepository = timeEntryRepository;
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

        if (timeEntryRepository.findByUserIdAndWorkDate(user.getId(), LocalDate.now()).isEmpty()) {
            timeEntryService.checkIn(user.getId());
            System.out.println("Check-in created for today.");
        } else {
            System.out.println("Time entry already exists for today.");
        }

        System.out.println("Admin user exists: " + email);
        System.out.println("Work schedule created/updated for today");
    }
}
