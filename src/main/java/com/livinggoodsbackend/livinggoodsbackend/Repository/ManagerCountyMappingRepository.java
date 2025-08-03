package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.livinggoodsbackend.livinggoodsbackend.Model.ChaChpMapping;
import com.livinggoodsbackend.livinggoodsbackend.Model.ManagerCountyMapping;

public interface ManagerCountyMappingRepository extends JpaRepository<ManagerCountyMapping, Long> {
    // Custom query methods can be added here if needed
    // For example, to find mappings by managerId or countyId
    List<ManagerCountyMapping> findByManagerId(Long managerId);
    List<ManagerCountyMapping> findByCountyId(Long countyId);
}
