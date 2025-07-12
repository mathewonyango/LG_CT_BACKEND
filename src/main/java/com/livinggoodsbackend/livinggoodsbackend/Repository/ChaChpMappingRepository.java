package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.livinggoodsbackend.livinggoodsbackend.Model.ChaChpMapping;

public interface ChaChpMappingRepository extends JpaRepository<ChaChpMapping, Long> {
    // Custom query methods can be added here if needed
    // For example, to find mappings by chaId or chpId
    List<ChaChpMapping> findByChaId(Long chaId);
    List<ChaChpMapping> findByChpId(Long chpId);
    List<ChaChpMapping> findByCommunityUnitId(Long communityUnitId);

}
