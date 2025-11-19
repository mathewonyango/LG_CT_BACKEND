package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.RegisterRequest;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;
import com.livinggoodsbackend.livinggoodsbackend.exception.AuthenticationException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Import;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.livinggoodsbackend.livinggoodsbackend.TestConfig; // ✅ add this line

import jakarta.mail.MessagingException; // ✅ add this line

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // ==================== REGISTRATION INTEGRATION TESTS ====================

    @Test
    void register_CreatesUserInDatabase() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("integrationuser");
        request.setEmail("integration@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("+254712345678");
        request.setRole(Role.USER);

        // When
        LoginResponse response = authService.register(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("integrationuser", response.getUsername());
        assertEquals(Role.USER, response.getRole());

        // Verify user exists in database
        User savedUser = userRepository.findByUsername("integrationuser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("integration@gmail.com", savedUser.getEmail());
        assertEquals("+254712345678", savedUser.getPhoneNumber());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPasswordHash()));
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getLastLogin());
        assertEquals(0L, savedUser.getVersion());
    }

    @Test
    void register_WithAdminRole_CreatesAdminUser() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminuser");
        request.setEmail("admin@gmail.com");
        request.setPassword("adminpass123");
        request.setPhoneNumber("+254712345678");
        request.setRole(Role.ADMIN);

        // When
        LoginResponse response = authService.register(request);

        // Then
        assertEquals(Role.ADMIN, response.getRole());

        User savedUser = userRepository.findByUsername("adminuser").orElseThrow();
        assertEquals(Role.ADMIN, savedUser.getRole());
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        // Given - Create first user
        createTestUser("duplicateuser", "user1@gmail.com");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicateuser");
        request.setEmail("user2@gmail.com");
        request.setPassword("password123");

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.register(request);
        });

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        // Given - Create first user
        createTestUser("user1", "duplicate@gmail.com");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("user2");
        request.setEmail("duplicate@gmail.com");
        request.setPassword("password123");

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    // ==================== LOGIN INTEGRATION TESTS ====================

    @Test
    void login_ValidCredentials_ReturnsTokenAndUpdatesLastLogin() throws InterruptedException {
        // Given
        User user = createTestUser("loginuser", "login@gmail.com");
        LocalDateTime initialLastLogin = user.getLastLogin();
        
        Thread.sleep(10); // Ensure time difference

        LoginRequest request = new LoginRequest();
        request.setUsername("loginuser");
        request.setPassword("password123");

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("loginuser", response.getUsername());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(Role.USER, response.getRole());

        // Verify lastLogin was updated
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNotNull(updatedUser.getLastLogin());
        assertTrue(updatedUser.getLastLogin().isAfter(initialLastLogin));
    }

    @Test
    void login_InvalidUsername_ThrowsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        // Given
        createTestUser("loginuser", "login@gmail.com");

        LoginRequest request = new LoginRequest();
        request.setUsername("loginuser");
        request.setPassword("wrongpassword");

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    // ==================== PASSWORD RESET INTEGRATION TESTS ====================

    @Test
    void initiatePasswordReset_ValidEmail_SavesTokenToDatabase() throws Exception {
        // Given
        User user = createTestUser("resetuser", "reset@gmail.com");
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // When
        authService.initiatePasswordReset("reset@gmail.com");

        // Then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNotNull(updatedUser.getResetToken());
        assertNotNull(updatedUser.getResetTokenExpiry());
        assertTrue(updatedUser.getResetTokenExpiry().isAfter(LocalDateTime.now()));

        verify(emailService).sendPasswordResetEmail(eq("reset@gmail.com"), eq(updatedUser.getResetToken()));
    }

    @Test
    void initiatePasswordReset_InvalidEmail_ThrowsException() throws Exception {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.initiatePasswordReset("notfound@gmail.com");
        });

        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_ValidToken_ChangesPasswordInDatabase() 
            throws UnsupportedEncodingException, MessagingException {
        // Given
        User user = createTestUser("resetuser", "reset@gmail.com");
        String oldPasswordHash = user.getPasswordHash();
        
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
        authService.initiatePasswordReset("reset@gmail.com");

        User userWithToken = userRepository.findById(user.getId()).orElseThrow();
        String resetToken = userWithToken.getResetToken();

        // When
        authService.resetPassword(resetToken, "newSecurePassword123");

        // Then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNotEquals(oldPasswordHash, updatedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches("newSecurePassword123", updatedUser.getPasswordHash()));
        assertNull(updatedUser.getResetToken());
        assertNull(updatedUser.getResetTokenExpiry());
    }

    @Test
    void resetPassword_ExpiredToken_ThrowsException() throws UnsupportedEncodingException, MessagingException {
        // Given
        User user = createTestUser("resetuser", "reset@gmail.com");
        user.setResetToken("expired-token");
        user.setResetTokenExpiry(LocalDateTime.now().minusHours(2));
        userRepository.save(user);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.resetPassword("expired-token", "newPassword");
        });

        assertEquals("Reset token has expired", exception.getMessage());
    }

    @Test
    void resetPassword_InvalidToken_ThrowsException() throws MessagingException {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.resetPassword("invalid-token-xyz", "newPassword");
        });

        assertEquals("Invalid or expired reset token", exception.getMessage());
    }

    // ==================== CHANGE PASSWORD INTEGRATION TESTS ====================

    @Test
    void changePassword_ValidCurrentPassword_ChangesPasswordInDatabase() {
        // Given
        User user = createTestUser("changepassuser", "change@gmail.com");
        String oldPasswordHash = user.getPasswordHash();

        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("changepassuser");
        SecurityContextHolder.setContext(securityContext);

        // When
        authService.changePassword("password123", "newStrongPassword456");

        // Then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertNotEquals(oldPasswordHash, updatedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches("newStrongPassword456", updatedUser.getPasswordHash()));
    }

    @Test
    void changePassword_WrongCurrentPassword_ThrowsException() {
        // Given
        createTestUser("changepassuser", "change@gmail.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("changepassuser");
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.changePassword("wrongPassword", "newPassword");
        });

        assertEquals("Current password is incorrect", exception.getMessage());
    }

    // ==================== END-TO-END FLOW TESTS ====================

    @Test
    void completeUserJourney_RegisterLoginChangePassword() throws InterruptedException {
        // 1. Register
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("journeyuser");
        registerRequest.setEmail("journey@gmail.com");
        registerRequest.setPassword("initialPassword123");
        registerRequest.setPhoneNumber("+254712345678");

        LoginResponse registerResponse = authService.register(registerRequest);
        assertNotNull(registerResponse.getToken());

        Thread.sleep(10);

        // 2. Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("journeyuser");
        loginRequest.setPassword("initialPassword123");

        LoginResponse loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse.getToken());
        assertNotEquals(registerResponse.getToken(), loginResponse.getToken());

        // 3. Change Password
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("journeyuser");
        SecurityContextHolder.setContext(securityContext);

        authService.changePassword("initialPassword123", "updatedPassword456");

        // 4. Login with new password
        loginRequest.setPassword("updatedPassword456");
        LoginResponse newLoginResponse = authService.login(loginRequest);
        assertNotNull(newLoginResponse.getToken());

        // 5. Old password should not work
        loginRequest.setPassword("initialPassword123");
        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
    }

    // ==================== HELPER METHODS ====================

    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);
        user.setPhoneNumber("+254712345678");
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setVersion(0L);
        return userRepository.save(user);
    }
}
