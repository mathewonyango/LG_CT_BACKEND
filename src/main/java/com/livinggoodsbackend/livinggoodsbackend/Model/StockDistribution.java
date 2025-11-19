package com.livinggoodsbackend.livinggoodsbackend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_distribution")
public class StockDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_unit_id", nullable = false)
    private Long communityUnitId;

    @Column(name = "cha_id", nullable = false)
    private Long chaId;

    @Column(name = "chp_id", nullable = false)
    private Long chpId;

    @Column(name = "commodity_id", nullable = false)
    private Long commodityId;

    @Column(name = "quantity_ordered", nullable = false)
    private Integer quantityOrdered;

    @Column(name = "quantity_distributed", nullable = false)
    private Integer quantityDistributed = 0;

    @Column(name = "distribution_date", nullable = false)
    private LocalDateTime distributionDate = LocalDateTime.now();

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
