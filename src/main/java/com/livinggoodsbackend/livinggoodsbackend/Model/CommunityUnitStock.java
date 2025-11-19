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
@Table(
    name = "community_unit_stock",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_community_unit_commodity",
            columnNames = {"community_unit_id", "commodity_id"}
        )
    }
)
public class CommunityUnitStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_unit_id", nullable = false)
    private Long communityUnitId;

    @Column(name = "commodity_id", nullable = false)
    private Long commodityId;

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived = 0;

    @Column(name = "quantity_to_order", nullable = false)
    private Integer quantityToOrder = 0;

    @Column(name = "stock_on_hand", nullable = false)
    private Integer stockOnHand = 0;

    @Column(name = "closing_balance", nullable = false)
    private Integer closingBalance = 0;

    @Column(name = "last_restock_date")
    private LocalDateTime lastRestockDate;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate = LocalDateTime.now();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "notes", length = 500)
    private String notes;
}