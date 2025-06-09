package com.livinggoodsbackend.livinggoodsbackend.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commodity_stock_history")
public class CommodityStockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "community_unit_id")
    private CommodityUnit communityUnit;
    
    @ManyToOne
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;
    
    @Column(name = "previous_balance")
    private Integer previousBalance;
    
    @Column(name = "new_balance")
    private Integer newBalance;
    
    @Column(name = "quantity_changed")
    private Integer quantityChanged;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    private ChangeType changeType;
    
    @Column(name = "record_date")
    private LocalDateTime recordDate;
    
    @ManyToOne
    @JoinColumn(name = "recorded_by")
    private User recordedBy;
    
    private String notes;

    enum ChangeType {
    RESTOCK, ISSUE, RETURN, ADJUSTMENT, EXPIRED, DAMAGED
}
}
