package com.livinggoodsbackend.livinggoodsbackend.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "commodity_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommodityRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "community_unit_id")
    private CommodityUnit communityUnit;
    
    @ManyToOne
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;
    
    private Integer quantityExpired;
    private Integer quantityDamaged;
    private Integer stockOnHand;
    private Integer quantityIssued;
    private Integer excessQuantityReturned;
    private Integer quantityConsumed;
    private Integer closingBalance;
    
    @Column(name = "last_restock_date")
    private LocalDateTime lastRestockDate;
    
    @Column(name = "stock_out_date")
    private LocalDateTime stockOutDate;
    
    @Column(name = "consumption_period")
    private Integer consumptionPeriod;
    
    @Column(name = "earliest_expiry_date")
    private LocalDateTime earliestExpiryDate;
    
    @Column(name = "quantity_to_order")
    private Integer quantityToOrder;
    
    @Column(name = "record_date")
    private LocalDateTime recordDate;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
