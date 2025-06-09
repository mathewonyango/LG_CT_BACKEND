package com.livinggoodsbackend.livinggoodsbackend.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wards", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "sub_county_id"})
})
public class Ward {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "sub_county_id")
    private SubCounty subCounty;
    
    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL)
    private List<Facility> facilities = new ArrayList<>();
    
}