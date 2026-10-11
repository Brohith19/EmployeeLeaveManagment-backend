
package com.employeleave.config;

import com.employeleave.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList()
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()
            ))

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authorizeHttpRequests(auth -> auth

                // Allow browser CORS preflight requests.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public authentication endpoints.
                .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/generate-test-password",
                    "/health",
                        "/error"
                ).permitAll()

                // Admin-only endpoints.
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/departments/**").hasRole("ADMIN")
                .requestMatchers("/api/leave-types/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Manager and admin endpoints.
                .requestMatchers("/api/leave-approvals/**")
                    .hasAnyRole("MANAGER", "ADMIN")

                .requestMatchers("/api/reports/**")
                    .hasAnyRole("MANAGER", "ADMIN")

                // Employee, manager and admin endpoints.
                .requestMatchers("/api/leave-requests/**")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")

                .requestMatchers("/api/leave-balances/**")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")

                .requestMatchers("/api/employees/**")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")

                .requestMatchers("/api/notifications/**")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")

                // Everything else requires authentication.
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
