package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.reset.token.expiry:3600000}") // 1 hour in milliseconds
    private long resetTokenExpiryMs;

    @Autowired
    private JavaMailSender emailSender;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
  public User createUser(User user) {
    // Validation
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
        throw new IllegalArgumentException("Username already exists");
    }
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email already exists");
    }

    // Set default/required values
    user.setId(null);
    user.setCreatedAt(LocalDateTime.now());
    user.setLastLogin(null);
    user.setVersion(0L);
    
    // Set default role if not provided
    // if (user.getRole() == null) {
    //     user.setRole(Role.USER);
    // }

    // Save and return
    return userRepository.save(user);
}
    
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only update fields that are not null in the request
        if (userDetails.getUsername() != null) {
            // Check if new username is different and not already taken
            if (!userDetails.getUsername().equals(existingUser.getUsername()) && 
                userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            existingUser.setUsername(userDetails.getUsername());
        }
        
        if (userDetails.getEmail() != null) {
            // Check if new email is different and not already taken
            if (!userDetails.getEmail().equals(existingUser.getEmail()) && 
                userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }

        // Never update these fields from request
        // existingUser.setCreatedAt - should never change
        // existingUser.setPasswordHash - should use separate password change endpoint
        // existingUser.setLastLogin - should only be updated on login
        // existingUser.setVersion - handled by @Version annotation
         
        existingUser.setVersion(0L);
        return userRepository.save(existingUser);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // public List<User> getUsersByRole(User role) {
    //     return userRepository.findByRole(role);
    // }

    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update to new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate random token
        String token = UUID.randomUUID().toString();
        
        // Set token and expiry
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpiryMs));
        userRepository.save(user);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n\n" +
                       "http://your-frontend-url/reset-password?token=" + token);
        
        emailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Update password and clear reset token
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
    }
}