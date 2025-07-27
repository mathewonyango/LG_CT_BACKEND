package com.livinggoodsbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // Disable CORS in Security
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**", "/uploads/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();
        
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    // List specific origins for credentials support
                    .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:9001",  // ← Add this line for your frontend

                        "http://localhost:8080", 
                        "http://localhost:8081",
                        "http://localhost:8100",
                        "capacitor://localhost",
                        "ionic://localhost"
                    )
                    // Use patterns for broader matching
                    .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://192.168.*.*:*",
                        "capacitor://*",
                        "ionic://*"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}