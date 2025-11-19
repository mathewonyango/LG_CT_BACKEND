package com.livinggoodsbackend.livinggoodsbackend.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommunityUnitStock;
import com.livinggoodsbackend.livinggoodsbackend.Model.StockDistribution;
import com.livinggoodsbackend.livinggoodsbackend.Service.StockService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.StockDtos.DistributeStockRequestDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.StockDtos.OrderRequestDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.StockDtos.ReceiveStockRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    // Aggregate CHP orders (read-only, relies on trigger)
    @GetMapping("/community-units/{cuId}/chp-orders/summary")
public ResponseEntity<ApiResponse> getChpOrderSummary(
        @PathVariable Long cuId,
        @RequestParam(required = false) Long commodityId) {
    try {
        Integer orders = stockService.aggregateChpOrders(cuId, commodityId);

        if (orders == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No CHP order summary found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "CHP order summary retrieved successfully", orders)
        );
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Failed to retrieve CHP order summary", null));
    }
}


    // CHA updates order quantity (manual override)
    @PutMapping("/community-units/{cuId}/order")
    public ResponseEntity<CommunityUnitStock> updateOrder(@PathVariable Long cuId,
            @Valid @RequestBody OrderRequestDto request) {
        return ResponseEntity.ok(stockService.updateOrder(cuId, request));
    }

    // CHA logs received stock
    @PostMapping("/community-units/{cuId}/receive")
    public ResponseEntity<CommunityUnitStock> receiveStock(@PathVariable Long cuId,
            @Valid @RequestBody ReceiveStockRequestDto request) {
        return ResponseEntity.ok(stockService.receiveStock(cuId, request));
    }

    // CHA distributes stock to CHP
    @PostMapping("/distribute")
    public ResponseEntity<ApiResponse> distributeStock(
            @Valid @RequestBody DistributeStockRequestDto request) {
        try {
            StockDistribution distribution = stockService.distributeStock(request);
            return ResponseEntity.ok(new ApiResponse(true, "Stock distributed successfully", distribution));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, ex.getMessage()));
        }
    }

    // Get CU stock summary
    @GetMapping("/community-units/{cuId}/stock/summary")
    public ResponseEntity<List<CommunityUnitStock>> getCuStockSummary(@PathVariable Long cuId) {
        return ResponseEntity.ok(stockService.getCuStockSummary(cuId));
    }

 @GetMapping("/chp/{chpId}/distributions")
public ResponseEntity<List<StockDistribution>> getChpDistributionHistory(
        @PathVariable Long chpId,
        @RequestParam(required = false) Long commodityId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

    // Convert LocalDate → LocalDateTime
    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

    return ResponseEntity.ok(
            stockService.getChpDistributionHistory(chpId, commodityId, fromDateTime, toDateTime)
    );
} 

@GetMapping("/chp/{chpId}/quantity-to-order")
public Integer getQntyToOrderForChp(@PathVariable Long chpId,
                                     @RequestParam Long commodityId) {
    return stockService.getQntyToOrderForChp(chpId, commodityId);
}
}
