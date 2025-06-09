package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityRecord;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityRecordService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityRecordDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRecordRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = "*")
public class CommodityRecordController {
    
    @Autowired
    private CommodityRecordService commodityRecordService;

    @GetMapping
    public ResponseEntity<?> getAllRecords() {
        try {
            List<CommodityRecordDTO> records = commodityRecordService.getAllRecords();
            return ResponseEntity.ok(new ApiResponse(true, "Records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving records: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecordById(@PathVariable Long id) {
        try {
            CommodityRecordDTO record = commodityRecordService.getRecordById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
            return ResponseEntity.ok(new ApiResponse(true, "Record found successfully", record));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createRecord(@Validated @RequestBody CreateCommodityRecordRequest request) {
        try {
            CommodityRecordDTO created = commodityRecordService.createRecord(request);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Record created successfully", created));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommodityRecordDTO> updateRecord(@PathVariable Long id, 
            @RequestBody CommodityRecord recordDetails) {
        try {
            CommodityRecordDTO updatedRecord = commodityRecordService.updateRecord(id, recordDetails);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        try {
            commodityRecordService.deleteRecord(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/community-unit/{communityUnitId}")
    public ResponseEntity<?> getRecordsByCommunityUnit(@PathVariable Long communityUnitId) {
        try {
            List<CommodityRecord> records = commodityRecordService.getRecordsByCommunityUnit(communityUnitId);
            return ResponseEntity.ok(new ApiResponse(true, "Records retrieved successfully", records));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockRecords(@RequestParam Integer threshold) {
        try {
            List<CommodityRecord> records = commodityRecordService.getLowStockRecords(threshold);
            return ResponseEntity.ok(new ApiResponse(true, "Low stock records retrieved successfully", records));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/date-range")
    public List<CommodityRecord> getRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return commodityRecordService.getRecordsByDateRange(start, end);
    }
}