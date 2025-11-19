package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;

@Data
public class BalanceResponseDto {
    private Long id;
    private Long commodityId;
    private Long communityUnitId;
    private Integer remainingBalance;
    private Integer usedQty;
    private Integer discrepancy;
    private String commodityName;
    private String communityUnitName;

    private String transactionType;
    private String notes; //pro
    private Integer quantity;

}
