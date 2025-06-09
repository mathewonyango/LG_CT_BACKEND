package com.livinggoodsbackend.livinggoodsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

     @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Allow everything

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
            .allowedOriginPatterns("*")  
            .allowedOrigins(
                "http://localhost:9000", 
                "capacitor://localhost", 
                "ionic://localhost", 
                "http://localhost",
                "http://16.170.239.185:9000",
                 "capacitor://org.livinggoods.commoditytracker",  // Your specific app ID
                "capacitor://org.livinggoods.commoditytracker"
            )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
                    

            registry.addMapping("/uploads/**")
                    .allowedOrigins(
                        "http://localhost:9000", 
                        "capacitor://localhost", 
                        "ionic://localhost", 
                        "http://localhost", 
                        "http://192.168.100.10:3000",
                        "capacitor://org.livinggoods.commoditytracker",  // Your specific app ID
                        "capacitor://org.livinggoods.commoditytracker",  // Alternative format
                        "*"                                     // For development only
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        }
    };
}
}