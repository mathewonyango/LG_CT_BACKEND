package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import javax.management.relation.Role;
// import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Service.UserService;

import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaCuMappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaCuMappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChaDashboardResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpBasicInfoDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpCuMappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpCuMappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChpDashboardDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateUserRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.CuBasicInfoDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.MappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.MappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.UpdateUserRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.UserResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
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
    
    // Get user for editing
    @GetMapping("/{id}/edit")
    public ResponseEntity<?> getUserForEdit(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(user -> {
                // Create DTO with only editable fields
                Map<String, Object> editableUser = new HashMap<>();
                editableUser.put("id", user.getId());
                editableUser.put("username", user.getUsername());
                editableUser.put("email", user.getEmail());
                editableUser.put("role", user.getRole());
                
                return ResponseEntity.ok(new ApiResponse(
                    true,
                    "User found successfully",
                    editableUser
                ));
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "User not found", null)));
    } 
    // Update user
    // @PutMapping("/{id}")
    // public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    //     try {
    //         User updatedUser = userService.updateUser(id, userDetails);
    //         return ResponseEntity.ok(new ApiResponse(
    //             true,
    //             "User updated successfully",
    //             updatedUser
    //         ));
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.badRequest()
    //             .body(new ApiResponse(false, e.getMessage(), null));
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //             .body(new ApiResponse(false, e.getMessage(), null));
    //     }
    // }
    
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
    
    // @GetMapping("/role/{role}")
    // public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
    //     return ResponseEntity.ok(userService.getUsersByRole(role));
    // }
    
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

    // Get all CHPs under a specific Community Unit
        @GetMapping("/cu/{communityUnitId}/chps/details")
        public ResponseEntity<List<ChpBasicInfoDTO>> getChpDetails(@PathVariable Long communityUnitId) {
            return ResponseEntity.ok(userService.getChpDetailsByCommunityUnit(communityUnitId));
        }

        // @GetMapping("/cha/{chaId}/chps")
        // public ResponseEntity<List<ChpBasicInfoDTO>> getChpsByCha(@PathVariable Long chaId) {
        //     List<ChpBasicInfoDTO> chps = userService.getChpsByCha(chaId);
        //     return ResponseEntity.ok(chps);
        // }

          @GetMapping("/cha/{chaId}/chps")
    public ResponseEntity<ChaDashboardResponseDTO> getChpsForCha(@PathVariable Long chaId) {
        ChaDashboardResponseDTO response = userService.getCHPsByCHA(chaId);
        return ResponseEntity.ok(response);
    }


        @GetMapping("/cha/{chaId}/cus/details")
        public ResponseEntity<List<CuBasicInfoDTO>> getCuDetails(@PathVariable Long chaId) {
            return ResponseEntity.ok(userService.getCommunityUnitDetailsByCha(chaId));
        }

        // Get all CUs assigned to a specific CHA
        @GetMapping("/cha/{chaId}/cus")
        public ResponseEntity<List<Long>> getCusByCha(@PathVariable Long chaId) {
            List<Long> cuIds = userService.getCusByCha(chaId);
            return ResponseEntity.ok(cuIds);
        }

    @PostMapping("/chp-to-cu")
    public ResponseEntity<ChpCuMappingResponseDTO> mapChpToCu(@RequestBody ChpCuMappingRequestDTO request) {
        return ResponseEntity.ok(userService.mapChpToCu(request));
    }
    
    //  @GetMapping("/cha/{chaId}/chps")
    // public ChaDashboardResponseDTO getCHPsForCHA(@PathVariable Long chaId) {
    //     return userService.getCHPsByCHA(chaId);
    // }
}