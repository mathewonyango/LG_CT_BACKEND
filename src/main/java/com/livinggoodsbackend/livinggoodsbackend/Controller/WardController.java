package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import com.livinggoodsbackend.livinggoodsbackend.Service.WardService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.WardDTO;

@RestController
@RequestMapping("/api/wards")
@CrossOrigin(origins = "*")
public class WardController {

    @Autowired
    private WardService wardService;

    @GetMapping
    public ResponseEntity<?> getAllWards() {
        try {
            List<WardDTO> wards = wardService.getAllWards();
            return ResponseEntity.ok(new ApiResponse(true, "Wards retrieved successfully", wards));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving wards: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWardById(@PathVariable Long id) {
        try {
            Optional<WardDTO> ward = wardService.getWardById(id);
            return ward.map(w -> ResponseEntity.ok(new ApiResponse(true, "Ward found successfully", w)))
                .orElse(ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Ward not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving ward: " + e.getMessage()));
        }
    }

    @GetMapping("/sub-county/{subCountyId}")
    public ResponseEntity<?> getWardsBySubCounty(@PathVariable Long subCountyId) {
        try {
            List<WardDTO> wards = wardService.getWardsBySubCounty(subCountyId);
            if (wards.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No wards found for sub-county with id: " + subCountyId));
            }
            return ResponseEntity.ok(new ApiResponse(true, "Wards retrieved successfully", wards));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving wards: " + e.getMessage()));
        }
    }

   @PostMapping
public ResponseEntity<?> createWard(@RequestBody WardDTO wardDTO) {
    try {
        // Basic validation
        if (wardDTO.getName() == null || wardDTO.getName().trim().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, "Ward name is required"));
        }
        
        if (wardDTO.getSubCountyId() == null) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, "Sub-county ID is required"));
        }

        WardDTO createdWard = wardService.createWard(wardDTO);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse(true, "Ward created successfully", createdWard));
            
    } catch (IllegalArgumentException e) {
        // Handle cases like duplicate ward names or invalid sub-county
        return ResponseEntity
            .badRequest()
            .body(new ApiResponse(false, e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse(false, "Error creating ward: " + e.getMessage()));
    }
}

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWard(@PathVariable Long id, @RequestBody WardDTO wardDTO) {
        try {
            // Basic validation
            if (wardDTO.getName() == null || wardDTO.getName().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Ward name is required"));
            }

            WardDTO updatedWard = wardService.updateWard(id, wardDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Ward updated successfully", updatedWard));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "Ward not found with id: " + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWard(@PathVariable Long id) {
        try {
            wardService.deleteWard(id);
            return ResponseEntity.ok(new ApiResponse(true, "Ward deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "Ward not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error deleting ward: " + e.getMessage()));
        }
    }
}