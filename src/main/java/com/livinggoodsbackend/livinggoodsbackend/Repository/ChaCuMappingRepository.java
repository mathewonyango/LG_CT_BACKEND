package com.livinggoodsbackend.livinggoodsbackend.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.ChaCuMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChaCuMappingRepository extends JpaRepository<ChaCuMapping, Long> {
    List<ChaCuMapping> findByChaId(Long chaId);
    List<ChaCuMapping> findByCommunityUnitId(Long communityUnitId);
    List<ChaCuMapping> findAllByCommunityUnitId(Long communityUnitId);

    // Optional<ChaCuMapping> findByCommunityUnitId(Long communityUnitId); // ✅ one-to-one


}
