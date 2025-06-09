package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Service.CountyService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
 import com.livinggoodsbackend.livinggoodsbackend.dto.CountyDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCountyRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/counties")
@CrossOrigin(origins = "*")
public class CountyController {
    
    @Autowired
    private CountyService countyService;

    @GetMapping
    public ResponseEntity<?> getAllCounties() {
        try {
            List<CountyDTO> counties = countyService.getAllCounties();
            return ResponseEntity.ok(new ApiResponse(true, "Counties retrieved successfully", counties));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving counties: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCountyById(@PathVariable Long id) {
        try {
            CountyDTO county = countyService.getCountyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("County not found with id: " + id));
            return ResponseEntity.ok(new ApiResponse(true, "County found successfully", county));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCounty(@RequestBody CreateCountyRequest request) {
        try {
            CountyDTO created = countyService.createCounty(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "County created successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCounty(@PathVariable Long id, @Validated @RequestBody CreateCountyRequest request) {
        try {
            CountyDTO updated = countyService.updateCounty(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "County updated successfully", updated));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCounty(@PathVariable Long id) {
        try {
            countyService.deleteCounty(id);
            return ResponseEntity.ok(new ApiResponse(true, "County deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
