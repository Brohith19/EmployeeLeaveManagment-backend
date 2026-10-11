
package com.employeleave.config;

import com.employeleave.entity.Department;
import com.employeleave.entity.Employee;
import com.employeleave.entity.User;
import com.employeleave.repository.DepartmentRepository;
import com.employeleave.repository.EmployeeRepository;
import com.employeleave.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Locale;

@Configuration
public class EmployeeAccountInitializer {

    @Bean
    CommandLineRunner initializeEmployeeAccounts(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            Department department = departmentRepository.findById(1L)
                    .orElseThrow(() -> new IllegalStateException(
                            "Department ID 1 was not found."));

            int usersCreated = 0;
            int profilesCreated = 0;
            int existingUsers = 0;
            int existingProfiles = 0;

            for (int i = 1; i <= 60; i++) {

                String username = "emp" + i;

                User user = userRepository.findByUsername(username)
                        .orElse(null);

                if (user == null) {
                    user = new User();
                    user.setUsername(username);
                    user.setPassword(
                            passwordEncoder.encode("employee123"));
                    user.setRole("EMPLOYEE");
                    user.setStatus("ACTIVE");

                    user = userRepository.save(user);
                    usersCreated++;

                    System.out.println("Created account: " + username);
                } else {
                    existingUsers++;
                    System.out.println(
                            "Account already exists: " + username);
                }

                Employee employee =
                        employeeRepository.findByUserUsername(username)
                                .orElse(null);

                if (employee != null) {
                    existingProfiles++;
                    System.out.println(
                            "Profile already exists for: " + username);
                    continue;
                }

                String employeeCode;
                int suffix = i;

                do {
                    employeeCode = String.format(
                            Locale.ROOT, "EMP%03d", suffix);
                    suffix++;
                } while (
                        employeeRepository.existsByEmployeeCode(
                                employeeCode));

                String email = username + "@example.com";
                int emailSuffix = 1;

                while (employeeRepository.existsByEmail(email)) {
                    email = username + "." + emailSuffix
                            + "@example.com";
                    emailSuffix++;
                }

                employee = new Employee();
                employee.setUser(user);
                employee.setEmployeeCode(employeeCode);
                employee.setEmail(email);
                employee.setFullName("Employee " + i);
                employee.setDepartment(department);
                employee.setJoiningDate(LocalDate.now());
                employee.setPhone(null);
                employee.setManager(null);

                employeeRepository.save(employee);
                profilesCreated++;

                System.out.println(
                        "Created profile for " + username
                                + " with code " + employeeCode);
            }

            System.out.println("================================");
            System.out.println("EMPLOYEE INITIALIZATION FINISHED");
            System.out.println("New accounts: " + usersCreated);
            System.out.println("Existing accounts: " + existingUsers);
            System.out.println("New profiles: " + profilesCreated);
            System.out.println("Existing profiles: " + existingProfiles);
            System.out.println("================================");
        };
    }
}
