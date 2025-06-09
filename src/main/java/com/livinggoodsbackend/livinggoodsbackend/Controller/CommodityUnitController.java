package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityUnit;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityUnitService;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityUnitDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityUnitRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;

@RestController
@RequestMapping("/api/community-units")
@CrossOrigin(origins = "*")
public class CommodityUnitController {

    @Autowired
    private CommodityUnitService CommodityUnitService;

    @GetMapping
    public ResponseEntity<?> getAllCommunityUnits() {
        try {
            List<CommodityUnitDTO> units = CommodityUnitService.getAllCommunityUnits();
            return ResponseEntity.ok(new ApiResponse(true, "Community units retrieved successfully", units));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving community units: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommunityUnitById(@PathVariable Long id) {
        try {
            CommodityUnitDTO unit = CommodityUnitService.getCommunityUnitById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community unit not found with id: " + id));
            return ResponseEntity.ok(new ApiResponse(true, "Community unit found successfully", unit));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCommunityUnit(@Validated @RequestBody CreateCommodityUnitRequest request) {
        try {
            CommodityUnitDTO created = CommodityUnitService.createCommodityUnit(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Community unit created successfully", created));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (org.springframework.http.converter.HttpMessageNotReadableException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Invalid request format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error creating community unit: " + e.getMessage()));
        }
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<?> updateCommunityUnit(
    //         @PathVariable Long id, 
    //         @Validated @RequestBody CreateCommodityUnitRequest request) {
    //     try {
    //         CommodityUnitDTO updated = CommodityUnitService.updateCommodityUnit(id, request);
    //         return ResponseEntity.ok(new ApiResponse(true, "Community unit updated successfully", updated));
    //     } catch (ResourceNotFoundException e) {
    //         return ResponseEntity
    //             .status(HttpStatus.NOT_FOUND)
    //             .body(new ApiResponse(false, e.getMessage()));
    //     }
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommunityUnit(@PathVariable Long id) {
        try {
            CommodityUnitService.deleteCommunityUnit(id);
            return ResponseEntity.ok(new ApiResponse(true, "Community unit deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/ward/{wardId}")
    public ResponseEntity<?> getCommunityUnitsByWard(@PathVariable Long wardId) {
        try {
            List<CommodityUnitDTO> units = CommodityUnitService.getCommunityUnitsByWard(wardId);
            return ResponseEntity.ok(new ApiResponse(true, "Community units retrieved successfully", units));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/county/{countyId}")
    public ResponseEntity<?> getCommunityUnitsByCounty(@PathVariable Long countyId) {
        try {
            List<CommodityUnitDTO> units = CommodityUnitService.getCommunityUnitsByCounty(countyId);
            return ResponseEntity.ok(new ApiResponse(true, "Community units retrieved successfully", units));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}