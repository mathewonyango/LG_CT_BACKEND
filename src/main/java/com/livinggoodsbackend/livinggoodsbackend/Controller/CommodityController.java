package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.Commodity;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceInUseException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/commodities")
@CrossOrigin(origins = "*")
public class CommodityController {

    @Autowired
    private CommodityService commodityService;

    @GetMapping
    public ResponseEntity<?> getAllCommodities() {
        try {
            List<CommodityDTO> commodities = commodityService.getAllCommodities();
            return ResponseEntity.ok(new ApiResponse(true, "Commodities retrieved successfully", commodities));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving commodities: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommodityById(@PathVariable Long id) {
        try {
            CommodityDTO commodity = commodityService.getCommodityById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Commodity found successfully", commodity));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getCommoditiesByCategory(@PathVariable Long categoryId) {
        try {
            List<CommodityDTO> commodities = commodityService.getCommoditiesByCategory(categoryId);
            return ResponseEntity.ok(new ApiResponse(true, "Commodities retrieved successfully", commodities));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCommodity(@Validated @RequestBody CreateCommodityRequest request) {
        try {
            CommodityDTO created = commodityService.createCommodity(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Commodity created successfully", created));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCommodity(
            @PathVariable Long id, 
            @Validated @RequestBody CreateCommodityRequest request) {
        try {
            CommodityDTO updated = commodityService.updateCommodity(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Commodity updated successfully", updated));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommodity(@PathVariable Long id) {
        try {
            commodityService.deleteCommodity(id);
            return ResponseEntity.ok(new ApiResponse(true, "Commodity deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (ResourceInUseException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}