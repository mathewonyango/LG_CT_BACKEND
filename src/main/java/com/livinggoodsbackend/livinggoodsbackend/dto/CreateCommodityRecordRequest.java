package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateCommodityRecordRequest {
    @NotNull(message = "Community unit ID is required")
    private Long communityUnitId;
    
    @NotNull(message = "Commodity ID is required")
    private Long commodityId;
    
    @Min(value = 0, message = "Quantity expired cannot be negative")
    private Integer quantityExpired = 0;
    
    @Min(value = 0, message = "Quantity damaged cannot be negative")
    private Integer quantityDamaged = 0;
    
    @NotNull(message = "Stock on hand is required")
    @Min(value = 0, message = "Stock on hand cannot be negative")
    private Integer stockOnHand;
    
    @Min(value = 0, message = "Quantity issued cannot be negative")
    private Integer quantityIssued = 0;
    
    @Min(value = 0, message = "Excess quantity returned cannot be negative")
    private Integer excessQuantityReturned = 0;
    
    @Min(value = 0, message = "Quantity consumed cannot be negative")
    private Integer quantityConsumed = 0;
    
    @NotNull(message = "Closing balance is required")
    @Min(value = 0, message = "Closing balance cannot be negative")
    private Integer closingBalance;
    
    @Min(value = 1, message = "Consumption period must be at least 1")
    private Integer consumptionPeriod;
}