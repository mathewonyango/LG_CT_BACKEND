package com.livinggoodsbackend.livinggoodsbackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.RegisterRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.UserKafkaDTO;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;
import com.livinggoodsbackend.livinggoodsbackend.exception.AuthenticationException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
//kafka
import com.livinggoodsbackend.livinggoodsbackend.Service.KafkaProducerService;


@Service
public class AuthService {
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    
    public AuthService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = generateToken(user);
        return new LoginResponse(token, user.getUsername(), user.getId(), user.getRole());
    }

    public LoginResponse register(RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthenticationException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setPhoneNumber(request.getPhoneNumber());

        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setVersion(0L);

        User savedUser = userRepository.save(user);
        String token = generateToken(savedUser);

            UserKafkaDTO kafkaDTO = new UserKafkaDTO(
            user.getUsername(),
            user.getEmail(),
            user.getPhoneNumber()
        );
        kafkaProducerService.sendMessage("user-events", kafkaDTO);
        return new LoginResponse(token, savedUser.getUsername(), savedUser.getId(), savedUser.getRole());
    }

    private String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .setIssuedAt(new java.util.Date())
            .setExpiration(new java.util.Date(System.currentTimeMillis() + 864000000)) // 10 days
            .signWith(key)
            .compact();
    }

    public void initiatePasswordReset(String email) throws UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || 
            user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Update password and clear reset token
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
    }

    public void changePassword(String currentPassword, String newPassword) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update to new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
