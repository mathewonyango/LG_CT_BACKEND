package com.livinggoodsbackend.livinggoodsbackend.dto;

import java.time.LocalDateTime;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;

import lombok.Data;

@Data
public class CommodityRecordDTO {
    private Long id;
    private Long communityUnitId;
    private String communityUnitName;
    private Long commodityId;
    private String commodityName;
    private Integer quantityExpired;
    private Integer quantityDamaged;
    private Integer stockOnHand;
    private Integer quantityIssued;
    private Integer excessQuantityReturned;
    private Integer quantityConsumed;
    private Integer closingBalance;
    private LocalDateTime lastRestockDate;
    private LocalDateTime stockOutDate;
    private Integer consumptionPeriod;
    private LocalDateTime recordDate;
    private String createdByUsername;
    private String countyName;
    private String subCountyName;
    private String wardName;
    private Long countyId;
    private Long subCountyId;
    private Long wardId;
    private Long facilityId;
    private String facilityName;
    private LocalDateTime earliestExpiryDate;
    private Integer quantityToOrder;
    // private Integer totalCHPsCounted; // Total CHPs counted for the record
    private Integer createdBy; // ID of the user creating the record
    private User chp; // <-- this Java field name is important


}