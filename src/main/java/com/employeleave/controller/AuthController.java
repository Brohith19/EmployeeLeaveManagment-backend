
package com.employeleave.controller;

import com.employeleave.entity.User;
import com.employeleave.repository.UserRepository;
import com.employeleave.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String username,
            @RequestParam String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"));

        if (!passwordEncoder.matches(
                password, user.getPassword())) {
            throw new RuntimeException(
                    "Invalid username or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException(
                    "User account is not active");
        }

        String token =
                jwtService.generateToken(user.getUsername());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    /*
     * TEMPORARY LOCAL DEVELOPMENT ENDPOINT.
     * Use it only to generate the BCrypt hash for employee123.
     * Remove this endpoint after copying the hash.
     */
    @GetMapping("/generate-test-password")
    public ResponseEntity<String> generateTestPassword() {
        return ResponseEntity.ok(
                passwordEncoder.encode("employee123"));
    }
}
