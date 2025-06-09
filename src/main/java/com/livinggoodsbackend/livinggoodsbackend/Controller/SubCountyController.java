package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.SubCounty;
import com.livinggoodsbackend.livinggoodsbackend.Service.SubCountyService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateSubCountyRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.SubCountyDTO;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/sub-counties")
@CrossOrigin(origins = "*")
public class SubCountyController {

    @Autowired
    private SubCountyService subCountyService;

    @GetMapping
    public ResponseEntity<?> getAllSubCounties() {
        try {
            List<SubCountyDTO> subCounties = subCountyService.getAllSubCounties();
            return ResponseEntity.ok(new ApiResponse(true, "Sub-counties retrieved successfully", subCounties));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving sub-counties: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubCountyById(@PathVariable Long id) {
        try {
            SubCountyDTO subCounty = subCountyService.getSubCountyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-county not found with id: " + id));
            return ResponseEntity.ok(new ApiResponse(true, "Sub-county found successfully", subCounty));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/county/{countyId}")
    public ResponseEntity<?> getSubCountiesByCounty(@PathVariable Long countyId) {
        try {
            List<SubCountyDTO> subCounties = subCountyService.getSubCountiesByCounty(countyId);
            return ResponseEntity.ok(new ApiResponse(true, "Sub-counties retrieved successfully", subCounties));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving sub-counties: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createSubCounty(@RequestBody CreateSubCountyRequest request) {
        try {
            SubCountyDTO created = subCountyService.createSubCounty(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Sub-county created successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error creating sub-county: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubCounty(
            @PathVariable Long id, 
            @Validated @RequestBody CreateSubCountyRequest request) {
        try {
            SubCountyDTO updated = subCountyService.updateSubCounty(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Sub-county updated successfully", updated));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubCounty(@PathVariable Long id) {
        try {
            subCountyService.deleteSubCounty(id);
            return ResponseEntity.ok(new ApiResponse(true, "Sub-county deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
