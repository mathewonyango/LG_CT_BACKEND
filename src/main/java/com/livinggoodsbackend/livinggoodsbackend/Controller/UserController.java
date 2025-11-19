package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Service.UserService;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityUnitService;
import com.livinggoodsbackend.livinggoodsbackend.Service.ManagerCountyMappingService;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.*;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.UserDto;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CommodityUnitService commodityUnitService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerCountyMappingService managerCountyMappingService;

    @Value("${cloudflare.images-url}")
    private String cloudflareImagesUrl;

    @Value("${cloudflare.api-token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Existing endpoints remain unchanged...
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/except-chps")
    public ResponseEntity<List<UserDto>> getAllUsersExceptChps() {
        return ResponseEntity.ok(userService.getAllUsersExceptChp());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            return ResponseEntity.ok(new ApiResponse(true, "User found successfully", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<?> getUserForEdit(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    Map<String, Object> editableUser = new HashMap<>();
                    editableUser.put("id", user.getId());
                    editableUser.put("username", user.getUsername());
                    editableUser.put("email", user.getEmail());
                    editableUser.put("role", user.getRole());
                    return ResponseEntity.ok(new ApiResponse(
                            true,
                            "User found successfully",
                            editableUser));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "User not found", null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deleting user: " + e.getMessage()));
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
            return ResponseEntity.ok(new ApiResponse(true, "User found successfully", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/chas")
    public List<UserResponseDTO> getAllCHAs() {
        return userService.getUsersByRole(Role.CHA);
    }

    @GetMapping("/chps")
    public List<UserResponseDTO> getAllCHPs() {
        return userService.getUsersByRole(Role.CHP);
    }

    @PostMapping("/cha-to-cu")
    public ResponseEntity<ChaCuMappingResponseDTO> mapChaToCu(@RequestBody ChaCuMappingRequestDTO request) {
        return ResponseEntity.ok(userService.mapChaToCu(request));
    }

    @GetMapping("/cu/{communityUnitId}/chps/details")
    public ResponseEntity<List<ChpBasicInfoDTO>> getChpDetails(@PathVariable Long communityUnitId) {
        return ResponseEntity.ok(userService.getChpDetailsByCommunityUnit(communityUnitId));
    }

    @GetMapping("/cha/{chaId}/chps")
    public ResponseEntity<ChaDashboardResponseDTO> getChpsForCha(
            @PathVariable Long chaId,
            @RequestParam(required = false) Integer month) {
        int targetMonth = (month != null && month >= 1 && month <= 12) ? month : LocalDate.now().getMonthValue();
        ChaDashboardResponseDTO response = userService.getCHPsByCHA(chaId, targetMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cha/{chaId}/cus/details")
    public ResponseEntity<List<CuBasicInfoDTO>> getCuDetails(@PathVariable Long chaId) {
        return ResponseEntity.ok(userService.getCommunityUnitDetailsByCha(chaId));
    }

    @GetMapping("/community-units/for/cha/{chaId}")
    public ResponseEntity<List<CommodityUnitDTO>> getCommunityUnitsForCha(@PathVariable Long chaId) {
        List<CommodityUnitDTO> dtos = commodityUnitService.getCommunityUnitsByCha(chaId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/cha/{chaId}/cus")
    public ResponseEntity<List<Long>> getCusByCha(@PathVariable Long chaId) {
        List<Long> cuIds = userService.getCusByCha(chaId);
        return ResponseEntity.ok(cuIds);
    }

    @PostMapping("/chp-to-cu")
    public ResponseEntity<ChpCuMappingResponseDTO> mapChpToCu(@RequestBody ChpCuMappingRequestDTO request) {
        return ResponseEntity.ok(userService.mapChpToCu(request));
    }

    @GetMapping("/managers")
    public List<User> getManagers() {
        return userService.getManagers();
    }

   @PostMapping("/{userId}/profile-image")
public ResponseEntity<String> uploadProfileImage(
        @PathVariable Long userId, @RequestParam("file") MultipartFile file) {
    if (file.isEmpty() || !isValidImage(file)) {
        return ResponseEntity.badRequest().body("Invalid or empty image file.");
    }

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + apiToken);

        String customPath = "users/" + userId + "/profile.jpg";
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("id", customPath);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(cloudflareImagesUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String imageUrl = extractImageUrlFromResponse(response.getBody());
            if (imageUrl == null) {
                return ResponseEntity.badRequest().body("Failed to extract image URL from response.");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);
            return ResponseEntity.ok("Success: Image uploaded and profile updated with URL: " + imageUrl);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Upload failed: " + response.getBody());
        }
    } catch (IOException e) {
        return ResponseEntity.internalServerError().body("Error processing file: An unexpected error occurred.");
    }
}

    private String extractImageUrlFromResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        if (root.path("success").asBoolean()) {
            JsonNode variants = root.path("result").path("variants");
            if (variants.isArray() && variants.size() > 0) {
                return variants.get(0).asText();
            }
            JsonNode url = root.path("result").path("url");
            if (!url.isMissingNode()) {
                return url.asText();
            }
        }
        return null;
    }

    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/") && file.getSize() < 5 * 1024 * 1024;
    }
}