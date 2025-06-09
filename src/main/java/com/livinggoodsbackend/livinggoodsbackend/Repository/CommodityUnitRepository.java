package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityUnit;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;

@Repository
public interface CommodityUnitRepository extends JpaRepository<CommodityUnit, Long> {
    List<CommodityUnit> findByCountyId(Long countyId);
    List<CommodityUnit> findBySubCountyId(Long subCountyId);
    List<CommodityUnit> findByWardId(Long wardId);
    List<CommodityUnit> findByLinkFacilityId(Long facilityId);
    List<CommodityUnit> findByCreatedBy(User createdBy);
}
