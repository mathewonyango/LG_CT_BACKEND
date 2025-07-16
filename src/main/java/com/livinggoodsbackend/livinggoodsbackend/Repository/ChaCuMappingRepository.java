package com.livinggoodsbackend.livinggoodsbackend.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.ChaCuMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChaCuMappingRepository extends JpaRepository<ChaCuMapping, Long> {
    List<ChaCuMapping> findByChaId(Long chaId);
    List<ChaCuMapping> findByCommunityUnitId(Long communityUnitId);
}
