package com.example.flexapp.config;

import com.example.flexapp.entity.User;
import com.example.flexapp.enums.Role;
import com.example.flexapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner{

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        String email = "admin@flexapp.com";

        if (userRepository.existsByEmail(email)) {
            System.out.println("Admin user already exists");
            return;
        }

        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("temp123");
        user.setRole(Role.ADMIN);
        user.setActive(true);

        userRepository.save(user);

        System.out.println("Admin user created successfully" + email);
    }
}
