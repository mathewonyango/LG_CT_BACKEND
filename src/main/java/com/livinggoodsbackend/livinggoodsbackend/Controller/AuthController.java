package com.livinggoodsbackend.livinggoodsbackend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.livinggoodsbackend.livinggoodsbackend.Service.AuthService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.RegisterRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ForgotPasswordRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ResetPasswordRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.PasswordUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse(
                true, 
                "Login successful. Welcome " + response.getUsername(), 
                response
            ));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(
                    false, 
                    "Login failed: " + e.getMessage(),
                    null
                ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(
                    true, 
                    "Registration successful",
                    response
                ));

                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password-initiate")
    public ResponseEntity<?> initiatePasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(new ApiResponse(
                true,
                "If your email is registered, you will receive reset instructions",
                null
            ));
        } catch (Exception e) {
            // Always return same message to prevent email enumeration
            return ResponseEntity.ok(new ApiResponse(
                true,
                "If your email is registered, you will receive reset instructions",
                null
            ));
        }
    }

    @PostMapping("/reset-password-complete")
    public ResponseEntity<?> completePasswordReset(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            // Validate password confirmation
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Passwords do not match", null));
            }

            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse(
                true,
                "Password has been reset successfully",
                null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        try {
            authService.changePassword(request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse(
                true,
                "Password changed successfully",
                null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}