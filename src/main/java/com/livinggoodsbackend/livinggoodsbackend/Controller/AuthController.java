package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Service.AuthService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.LoginResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.RegisterRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ForgotPasswordRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ResetPasswordRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.PasswordUpdateRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Value("${cloudflare.images-url}")
    private String cloudflareImagesUrl;

     @Value("${cloudflare.account-id}")
    private String accountId;


    private static final String IMAGE_UPLOAD_URL = "https://api.cloudflare.com/client/v4/accounts/%s/images/v1";

    @Value("${cloudflare.api-token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Login successful. Welcome " + response.getUsername(),
                    response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(
                            false,
                            "Login failed: " + e.getMessage(),
                            null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Registration successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Registration failed", null));
        }
    }

    @PostMapping("/upload/profile-image")
@CrossOrigin(origins = {"http://localhost:9002", "http://localhost:9001", "http://localhost:8000", "null"})
public ResponseEntity<ApiResponse> uploadProfileImage(
        @RequestParam("profileImage") MultipartFile profileImage) {

    log.info("Starting profile image upload");
    try {
        if (profileImage == null || profileImage.isEmpty()) {
            log.warn("No image file provided");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "No image file provided", null));
        }

        if (!isValidImage(profileImage)) {
            log.warn("Invalid image file. ContentType: {}, Size: {}", 
                     profileImage.getContentType(), profileImage.getSize());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid image file", null));
        }

        // Generate a unique custom ID (e.g., using timestamp or UUID)
        String customId = "profile_" + System.currentTimeMillis(); // Example: profile_1634567890123
        log.debug("Using custom ID path: {}", customId);

        // Build request with file and custom ID
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(apiToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new HttpEntity<>(profileImage.getBytes(), getFileHeaders(profileImage)));
        body.add("id", customId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        log.info("Uploading image to Cloudflare with custom ID: {}", customId);
        ResponseEntity<String> uploadResponse = restTemplate.postForEntity(
                String.format(IMAGE_UPLOAD_URL, accountId),
                requestEntity,
                String.class);

        if (uploadResponse.getStatusCode().is2xxSuccessful()) {
            String imageUrl = extractImageUrlFromResponse(uploadResponse.getBody());
            if (imageUrl != null) {
                log.info("Image uploaded successfully. URL: {}", imageUrl);
                // If you still need to associate with a user, fetch userId from context or skip this part
                return ResponseEntity.ok()
                        .body(new ApiResponse(true, "Profile image uploaded successfully", imageUrl));
            } else {
                log.error("Failed to extract image URL from response: {}", uploadResponse.getBody());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(false, "Failed to parse image URL", null));
            }
        } else {
            log.error("Image upload failed. Status: {}, Body: {}", 
                      uploadResponse.getStatusCode(), uploadResponse.getBody());
            return ResponseEntity.status(uploadResponse.getStatusCode())
                    .body(new ApiResponse(false, "Upload failed: " + uploadResponse.getBody(), null));
        }

    } catch (Exception e) {
        log.error("Image upload failed. Error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Image upload failed: " + e.getMessage(), null));
    }
}
    private HttpHeaders getFileHeaders(MultipartFile file) {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
        fileHeaders.set("Content-Disposition", 
                        "form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"");
        return fileHeaders;
    }

    private String extractImageUrlFromResponse(String responseBody) throws IOException {
        log.debug("Parsing Cloudflare response: {}", responseBody);
        JsonNode root = objectMapper.readTree(responseBody);
        if (root.path("success").asBoolean()) {
            JsonNode variants = root.path("result").path("variants");
            if (variants.isArray() && variants.size() > 0) {
                String url = variants.get(0).asText();
                log.debug("Extracted variant URL: {}", url);
                // Encode % to %25 as per Cloudflare documentation
                String encodedUrl = url.replace("%", "%25");
                log.debug("Encoded URL: {}", encodedUrl);
                return encodedUrl;
            }
            log.warn("No variants found in response");
        } else {
            log.error("Cloudflare response indicates failure: {}", responseBody);
        }
        return null;
    }

    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        boolean isValid = contentType != null && 
                         contentType.startsWith("image/") && 
                         file.getSize() < 5 * 1024 * 1024; // 5MB limit
        log.debug("Image validation: ContentType: {}, Size: {}, Valid: {}", 
                    contentType, file.getSize(), isValid);
        return isValid;
    }

    @PostMapping("/reset-password-initiate")
    public ResponseEntity<?> initiatePasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "If your email is registered, you will receive reset instructions",
                    null));
        } catch (Exception e) {
            // Always return same message to prevent email enumeration
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "If your email is registered, you will receive reset instructions",
                    null));
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
                    null));
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
                    null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}