package com.livinggoodsbackend.livinggoodsbackend.Repository;


import com.livinggoodsbackend.livinggoodsbackend.Model.ChpCuMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChpCuMappingRepository extends JpaRepository<ChpCuMapping, Long> {
    List<ChpCuMapping> findByChpId(Long chpId);
    List<ChpCuMapping> findByCommunityUnitId(Long communityUnitId);
    List<ChpCuMapping> findByCommunityUnitIdIn(List<Long> cuIds);

}
