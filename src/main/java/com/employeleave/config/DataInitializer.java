package com.employeleave.config;

import com.employeleave.entity.User;
import com.employeleave.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ADMIN
        User admin = userRepository
                .findByUsername("admin")
                .orElse(null);

        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
        }

        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        userRepository.save(admin);


        // MANAGER
        User manager = userRepository
                .findByUsername("manager")
                .orElse(null);

        if (manager == null) {
            manager = new User();
            manager.setUsername("manager");
        }

        manager.setPassword(passwordEncoder.encode("manager123"));
        manager.setRole("MANAGER");
        manager.setStatus("ACTIVE");
        userRepository.save(manager);


        // EMPLOYEE
        User employee = userRepository
                .findByUsername("employee")
                .orElse(null);

        if (employee == null) {
            employee = new User();
            employee.setUsername("employee");
        }

        employee.setPassword(passwordEncoder.encode("employee123"));
        employee.setRole("EMPLOYEE");
        employee.setStatus("ACTIVE");
        userRepository.save(employee);


        System.out.println("Admin, Manager and Employee initialized successfully.");
    }
}