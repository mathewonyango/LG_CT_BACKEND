package com.livinggoodsbackend.livinggoodsbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livinggoodsbackend.livinggoodsbackend.Controller.AuthController;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.Service.AuthService;
import com.livinggoodsbackend.livinggoodsbackend.dto.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;
    

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();

        authController = new AuthController();
        ReflectionTestUtils.setField(authController, "authService", authService);
        ReflectionTestUtils.setField(authController, "userRepository", userRepository);
        ReflectionTestUtils.setField(authController, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(authController, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(authController, "apiToken", "dummy-token");
        ReflectionTestUtils.setField(authController, "accountId", "dummy-account");
    }

    // ------------------- LOGIN TESTS -------------------

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password");

        LoginResponse response = new LoginResponse();
        response.setUsername("testUser");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        ResponseEntity<?> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) result.getBody();
        assertTrue(apiResponse.isSuccess());
        assertTrue(apiResponse.getMessage().contains("Login successful"));
    }

    @Test
    void testLogin_Failure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("wrong");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<?> result = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) result.getBody();
        assertFalse(apiResponse.isSuccess());
        assertTrue(apiResponse.getMessage().contains("Login failed"));
    }

    // ------------------- REGISTER TESTS -------------------

    @Test
    void testRegister_Success() {
        RegisterRequest req = new RegisterRequest();
        LoginResponse response = new LoginResponse();
        response.setUsername("newUser");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        ResponseEntity<ApiResponse> result = authController.register(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        verify(authService, times(1)).register(req);
    }

    @Test
    void testRegister_Failure() {
        RegisterRequest req = new RegisterRequest();
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ApiResponse> result = authController.register(req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
    }

    // ------------------- PASSWORD RESET TESTS -------------------

    // @Test
    // void testInitiatePasswordReset_Success() {
    //     ForgotPasswordRequest req = new ForgotPasswordRequest();
    //     req.setEmail("test@example.com");

    //     ResponseEntity<?> result = authController.initiatePasswordReset(req);
    //     ApiResponse body = (ApiResponse) result.getBody();

    //     assertTrue(body.isSuccess());
    //     verify(authService, times(1)).initiatePasswordReset(req.getEmail());
    // }

    // @Test
    // void testCompletePasswordReset_Success() {
    //     ResetPasswordRequest req = new ResetPasswordRequest();
    //     req.setToken("1234");
    //     req.setNewPassword("pass");
    //     req.setConfirmPassword("pass");

    //     ResponseEntity<?> result = authController.completePasswordReset(req);
    //     ApiResponse body = (ApiResponse) result.getBody();

    //     assertTrue(body.isSuccess());
    // }

    @Test
    void testCompletePasswordReset_Mismatch() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("token");
        req.setNewPassword("pass1");
        req.setConfirmPassword("pass2");

        ResponseEntity<?> result = authController.completePasswordReset(req);
        ApiResponse body = (ApiResponse) result.getBody();

        assertFalse(body.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    // ------------------- CHANGE PASSWORD TESTS -------------------

    @Test
    void testChangePassword_Success() {
        PasswordUpdateRequest req = new PasswordUpdateRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("new");

        ResponseEntity<?> result = authController.changePassword(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(authService).changePassword("old", "new");
    }

    @Test
    void testChangePassword_Failure() {
        PasswordUpdateRequest req = new PasswordUpdateRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("new");

        doThrow(new IllegalArgumentException("Wrong password"))
                .when(authService).changePassword(anyString(), anyString());

        ResponseEntity<?> result = authController.changePassword(req);
        ApiResponse body = (ApiResponse) result.getBody();

        assertFalse(body.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    // ------------------- IMAGE UPLOAD TESTS -------------------

    @Test
    void testUploadProfileImage_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "profileImage", "test.jpg", "image/jpeg", "dummy".getBytes());

        String mockResponse = """
                {"success":true,"result":{"variants":["https://img.cloudflare.com/test%image.jpg"]}}
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        ResponseEntity<ApiResponse> result = authController.uploadProfileImage(file);
        ApiResponse body = result.getBody();

        assertTrue(body.isSuccess());
        assertTrue(((String) body.getData()).contains("cloudflare"));
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testUploadProfileImage_InvalidFile() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "profileImage", "test.txt", "text/plain", "bad".getBytes());

        ResponseEntity<ApiResponse> result = authController.uploadProfileImage(invalidFile);

        assertFalse(result.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void testUploadProfileImage_NoFile() {
        ResponseEntity<ApiResponse> result = authController.uploadProfileImage(null);

        assertFalse(result.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
