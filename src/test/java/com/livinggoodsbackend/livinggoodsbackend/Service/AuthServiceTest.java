package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.RegisterRequest;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;
import com.livinggoodsbackend.livinggoodsbackend.exception.AuthenticationException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.security.JwtUtil;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.livinggoodsbackend.livinggoodsbackend.TestConfig; 

import jakarta.mail.MessagingException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtSecret", "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm");
        ReflectionTestUtils.setField(authService, "jwtExpiration", 3600000L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$encodedPassword");
        testUser.setRole(Role.USER);
        testUser.setPhoneNumber("+254712345678");
        testUser.setCreatedAt(LocalDateTime.now());

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhoneNumber("+254712345678");
        registerRequest.setRole(Role.USER);
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void login_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals(1L, response.getUserId());
        assertEquals(Role.USER, response.getRole());
        assertNotNull(response.getToken());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPasswordHash());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
            authService.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
            authService.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPasswordHash());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_UpdatesLastLoginTime() {
        LocalDateTime beforeLogin = LocalDateTime.now();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.login(loginRequest);

        verify(userRepository).save(argThat(user ->
            user.getLastLogin() != null &&
            !user.getLastLogin().isBefore(beforeLogin)
        ));
    }

    // ==================== REGISTER TESTS ====================

    @Test
    void register_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals(2L, response.getUserId());
        assertEquals(Role.USER, response.getRole());
        assertNotNull(response.getToken());
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
            authService.register(registerRequest)
        );

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void register_EmailExists_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.of(testUser));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
            authService.register(registerRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void register_DefaultRoleWhenNotProvided() {
        registerRequest.setRole(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        LoginResponse response = authService.register(registerRequest);

        assertEquals(Role.USER, response.getRole());
    }

    // ==================== PASSWORD RESET TESTS ====================

    @Test
    void initiatePasswordReset_Success() throws UnsupportedEncodingException, MessagingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        authService.initiatePasswordReset("test@example.com");

        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString());
    }

    @Test
    void initiatePasswordReset_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            authService.initiatePasswordReset("notfound@example.com")
        );
    }

    @Test
    void initiatePasswordReset_EmailServiceFails_ThrowsRuntimeException() throws MessagingException, UnsupportedEncodingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doThrow(new MessagingException("Email failed")).when(emailService)
            .sendPasswordResetEmail(anyString(), anyString());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            authService.initiatePasswordReset("test@example.com")
        );

        assertTrue(exception.getMessage().contains("Failed to send password reset email"));
    }

    @Test
    void resetPassword_Success() throws MessagingException {
        String token = "valid-reset-token";
        String newPassword = "newPassword123";
        testUser.setResetToken(token);
        testUser.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("$2a$10$encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.resetPassword(token, newPassword);

        verify(passwordEncoder).encode(newPassword);
    }

    @Test
    void resetPassword_InvalidToken_ThrowsException() {
        when(userRepository.findByResetToken("invalid-token")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            authService.resetPassword("invalid-token", "newPassword")
        );

        assertEquals("Invalid or expired reset token", exception.getMessage());
    }

    @Test
    void resetPassword_ExpiredToken_ThrowsException() {
        String token = "expired-token";
        testUser.setResetToken(token);
        testUser.setResetTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            authService.resetPassword(token, "newPassword")
        );

        assertEquals("Reset token has expired", exception.getMessage());
    }

    // ==================== CHANGE PASSWORD TESTS ====================

    @Test
    void changePassword_Success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.changePassword("currentPassword", "newPassword123");

        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            authService.changePassword("currentPassword", "newPassword123")
        );
    }

    @Test
    void changePassword_WrongCurrentPassword_ThrowsException() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPasswordHash())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            authService.changePassword("wrongPassword", "newPassword123")
        );

        assertEquals("Current password is incorrect", exception.getMessage());
    }
}
