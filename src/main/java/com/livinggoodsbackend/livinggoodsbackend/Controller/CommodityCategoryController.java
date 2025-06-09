package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityCategory;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityCategoryService;

import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityCategoryDTO;  
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCategoryRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceInUseException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CommodityCategoryController {

    @Autowired
    private CommodityCategoryService commodityCategoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CommodityCategoryDTO> categories = commodityCategoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse(true, "Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving categories: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            CommodityCategoryDTO category = commodityCategoryService.getCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
            return ResponseEntity.ok(new ApiResponse(true, "Category found successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Validated @RequestBody CreateCategoryRequest request) {
        try {
            CommodityCategoryDTO created = commodityCategoryService.createCategory(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Category created successfully", created));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id, 
            @Validated @RequestBody CreateCategoryRequest request) {
        try {
            CommodityCategoryDTO updated = commodityCategoryService.updateCategory(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Category updated successfully", updated));
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
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            commodityCategoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse(true, "Category deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (ResourceInUseException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, "Cannot delete category as it is being used by commodities"));
        }
    }
}