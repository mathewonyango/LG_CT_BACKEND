package com.livinggoodsbackend.livinggoodsbackend.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facilities")
public class Facility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(name = "facility_code")
    private String facilityCode;
    
    private String type;
    
    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToMany
    @JoinTable(
        name = "facility_wards",
        joinColumns = @JoinColumn(name = "facility_id"),
        inverseJoinColumns = @JoinColumn(name = "ward_id")
    )
    private List<Ward> wards = new ArrayList<>();
}