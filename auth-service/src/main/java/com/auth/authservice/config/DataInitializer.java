package com.auth.authservice.config;

import com.auth.authservice.entity.User;
import com.auth.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-password}")  // extract values from config
    private String adminDefaultPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    } // constructor injection is happening

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode(adminDefaultPassword),
                "ADMIN",
                true
            ));
            System.out.println("Default admin created: admin");
        }
    }
}
