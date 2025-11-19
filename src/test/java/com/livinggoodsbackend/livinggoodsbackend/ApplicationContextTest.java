package com.livinggoodsbackend.livinggoodsbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple test to verify that the Spring Application Context loads correctly.
 * If this test fails, there's a configuration issue preventing the app from starting.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class ApplicationContextTest {

    @Test
    void contextLoads() {
        // If the application context loads successfully, this test passes
        assertTrue(true, "Application context should load successfully");
    }
}