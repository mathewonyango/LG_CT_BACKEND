package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


public class StockDtos {
@Data
    public static class OrderRequestDto {
        @NotNull
        private Long commodityId;
        @NotNull
        @Min(0)
        private Integer quantityToOrder;

       
    }
@Data
    public static class ReceiveStockRequestDto {
        @NotNull
        private Long commodityId;
        @NotNull
        @Min(0)
        private Integer quantityReceived;
        private String notes;

    }
    @Data

    public static class DistributeStockRequestDto {
        @NotNull
        private Long communityUnitId;
        @NotNull
        private Long chaId;
        @NotNull
        private Long chpId;
        @NotNull
        private Long commodityId;
        @NotNull
        @Min(0)
        private Integer quantityToDistribute;
        private String notes;
    }
}
