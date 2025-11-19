package com.livinggoodsbackend.livinggoodsbackend.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.livinggoodsbackend.livinggoodsbackend.Model.Commodity;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityRecord;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityUnit;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityUnitRepository;

@Component
public class DtoConverter {

    @Autowired
    private CommodityRepository commodityRepository;

    @Autowired
    private CommodityUnitRepository communityUnitRepository;

    public CommodityRecordDTO toCommodityRecordDTO(CommodityRecord entity) {
        if (entity == null) {
            return null;
        }

        CommodityRecordDTO dto = new CommodityRecordDTO();
        dto.setId(entity.getId());
        dto.setCommunityUnitId(entity.getCommunityUnit().getId());
        dto.setCommodityId(entity.getCommodity().getId());
        dto.setQuantityExpired(entity.getQuantityExpired());
        dto.setQuantityDamaged(entity.getQuantityDamaged());
        dto.setStockOnHand(entity.getStockOnHand());
        dto.setQuantityIssued(entity.getQuantityIssued());
        dto.setExcessQuantityReturned(entity.getExcessQuantityReturned());
        dto.setQuantityConsumed(entity.getQuantityConsumed());
        dto.setClosingBalance(entity.getClosingBalance());
        dto.setLastRestockDate(entity.getLastRestockDate());
        dto.setStockOutDate(entity.getStockOutDate());
        dto.setConsumptionPeriod(entity.getConsumptionPeriod());
        dto.setRecordDate(entity.getRecordDate());
        dto.setEarliestExpiryDate(entity.getEarliestExpiryDate());
        dto.setQuantityToOrder(entity.getQuantityToOrder());

        // Fetch related data
        Commodity commodity = commodityRepository.findById(entity.getCommodity().getId()).orElse(null);
        if (commodity != null) {
            dto.setCommodityName(commodity.getName());
        }

        CommodityUnit communityUnit = communityUnitRepository.findById(entity.getCommunityUnit().getId()).orElse(null);
        if (communityUnit != null) {
            dto.setCommunityUnitName(communityUnit.getCommunityUnitName());
            // Assuming CommunityUnit has fields for county, subCounty, ward, facility
            dto.setCountyId(communityUnit.getCounty().getId());
            dto.setSubCountyId(communityUnit.getSubCounty().getId());
            dto.setWardId(communityUnit.getWard().getId());
            dto.setFacilityId(communityUnit.getLinkFacility().getId());
            dto.setCountyName(communityUnit.getCounty().getName());
            dto.setSubCountyName(communityUnit.getSubCounty().getName());
            dto.setWardName(communityUnit.getWard().getName());
            dto.setFacilityName(communityUnit.getLinkFacility().getName());
        }

        // createdByUsername would need a user lookup; assuming it's stored elsewhere or null for now
        dto.setCreatedByUsername(null); // Replace with actual logic if available

        return dto;
    }
}