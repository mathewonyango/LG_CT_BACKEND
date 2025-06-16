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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "community_units")
public class CommodityUnit {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "community_unit_name")
    private String communityUnitName;
    
    @ManyToOne
    @JoinColumn(name = "county_id")
    private County county;
    
    @ManyToOne
    @JoinColumn(name = "sub_county_id")
    private SubCounty subCounty;
    
    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;
    
    @ManyToOne
    @JoinColumn(name = "link_facility_id")
    private Facility linkFacility;
    
    @Column(name = "cha_name")
    private String chaName;
    
    @Column(name = "total_chps")
    private Integer totalChps;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @Column(name = "total_chps_counted")
    private Integer totalCHPsCounted;  // Change from Long to Integer

}
