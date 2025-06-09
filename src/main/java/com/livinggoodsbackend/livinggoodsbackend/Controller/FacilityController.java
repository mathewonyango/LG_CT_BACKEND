package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.Facility;
import com.livinggoodsbackend.livinggoodsbackend.Service.FacilityService;
import com.livinggoodsbackend.livinggoodsbackend.dto.FacilityDTO;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateFacilityRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "*")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping
    public ResponseEntity<?> getAllFacilities() {
        try {
            List<FacilityDTO> facilities = facilityService.getAllFacilities();
            return ResponseEntity.ok(new ApiResponse(true, "Facilities retrieved successfully", facilities));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving facilities: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacilityById(@PathVariable Long id) {
        try {
            return facilityService.getFacilityById(id)
                .map(facility -> ResponseEntity.ok(new ApiResponse(true, "Facility found successfully", facility)))
                .orElse(ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Facility not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving facility: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createFacility( @RequestBody CreateFacilityRequest request) {
        try {
            FacilityDTO created = facilityService.createFacility(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Facility created successfully", created));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error creating facility: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFacility(
            @PathVariable Long id,
            @Validated @RequestBody CreateFacilityRequest request) {
        try {
            FacilityDTO updated = facilityService.updateFacility(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Facility updated successfully", updated));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error updating facility: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFacility(@PathVariable Long id) {
        try {
            facilityService.deleteFacility(id);
            return ResponseEntity.ok(new ApiResponse(true, "Facility deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error deleting facility: " + e.getMessage()));
        }
    }

    @GetMapping("/ward/{wardId}")
    public ResponseEntity<?> getFacilitiesByWard(@PathVariable Long wardId) {
        try {
            List<FacilityDTO> facilities = facilityService.getFacilitiesByWard(wardId);
            return ResponseEntity.ok(new ApiResponse(true, "Facilities retrieved successfully", facilities));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving facilities: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getFacilitiesByType(@PathVariable String type) {
        try {
            List<FacilityDTO> facilities = facilityService.getFacilitiesByType(type);
            return ResponseEntity.ok(new ApiResponse(true, "Facilities retrieved successfully", facilities));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving facilities: " + e.getMessage()));
        }
    }
}