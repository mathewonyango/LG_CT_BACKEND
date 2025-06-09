package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.relation.Role;
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
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateUserRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.UpdateUserRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

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

    // Create user
//     @PostMapping
// public ResponseEntity<?> createUser(@RequestBody User user) {
//     try {
//         // Basic validation
//         if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
//             return ResponseEntity
//                 .badRequest()
//                 .body(new ApiResponse(false, "Username is required"));
//         }
        
//         if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
//             return ResponseEntity
//                 .badRequest()
//                 .body(new ApiResponse(false, "Email is required"));
//         }

//         User createdUser = userService.createUser(user);
//         return ResponseEntity
//             .status(HttpStatus.CREATED)
//             .body(new ApiResponse(true, "User created successfully", createdUser));
            
//     } catch (IllegalArgumentException e) {
//         // Handle duplicate username/email
//         return ResponseEntity
//             .badRequest()
//             .body(new ApiResponse(false, e.getMessage()));
//     } catch (Exception e) {
//         return ResponseEntity
//             .status(HttpStatus.INTERNAL_SERVER_ERROR)
//             .body(new ApiResponse(false, "Error creating user: " + e.getMessage()));
//     }
// }
    
    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(new ApiResponse(
                true,
                "User updated successfully",
                updatedUser
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
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
}