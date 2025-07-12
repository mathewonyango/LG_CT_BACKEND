package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
// import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.Commodity;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityRecord;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityStockHistory;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityUnit;
import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Model.Facility;
import com.livinggoodsbackend.livinggoodsbackend.Model.SubCounty;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChangeType;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRecordRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityStockHistoryRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityUnitRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRecordRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityRecordDTO;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;


@Service

public class CommodityRecordService {
    
    @Autowired
    private CommodityRecordRepository commodityRecordRepository;
    
    @Autowired
    private CommodityUnitRepository communityUnitRepository;
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private CommodityStockHistoryRepository stockHistoryRepository;
    @Autowired
    private UserRepository userRepository;

    
   public List<CommodityRecordDTO> getAllRecords() {
    return commodityRecordRepository.findAllWithLocations().stream()
        .map(record -> {
            CommodityRecordDTO dto = convertToDTO(record);

            // Safely get createdById from community unit
            Integer creatorId = record.getCommunityUnit() != null ? record.getCommunityUnit().getCreatedById() : null;
            dto.setCreatedBy(creatorId);

            // Set the username from the user table using createdById
            if (creatorId != null) {
                userRepository.findById(Long.valueOf(creatorId)).ifPresent(user ->
                    dto.setCreatedByUsername(user.getUsername())
                );
            }

            return dto;
        })
        .collect(Collectors.toList());
}
    
    public Optional<CommodityRecordDTO> getRecordById(Long id) {
        return commodityRecordRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public CommodityRecordDTO createRecord(CreateCommodityRecordRequest request) {
    // Fetch Community Unit
    CommodityUnit communityUnit = communityUnitRepository.findById(request.getCommunityUnitId())
        .orElseThrow(() -> new ResourceNotFoundException("Community Unit not found"));

    // Fetch Commodity
    Commodity commodity = commodityRepository.findById(request.getCommodityId())
        .orElseThrow(() -> new ResourceNotFoundException("Commodity not found"));

    // Fetch CHP (User)
    User chp = userRepository.findById(request.getChpId())
        .orElseThrow(() -> new ResourceNotFoundException("CHP not found"));

    // Create and populate CommodityRecord entity
    CommodityRecord record = new CommodityRecord();
    record.setCommunityUnit(communityUnit);
    record.setCommodity(commodity);
    record.setQuantityExpired(request.getQuantityExpired());
    record.setQuantityDamaged(request.getQuantityDamaged());
    record.setStockOnHand(request.getStockOnHand());
    record.setQuantityIssued(request.getQuantityIssued());
    record.setExcessQuantityReturned(request.getExcessQuantityReturned());
    record.setQuantityConsumed(request.getQuantityConsumed());
    record.setClosingBalance(request.getClosingBalance());
    record.setConsumptionPeriod(request.getConsumptionPeriod());
    record.setEarliestExpiryDate(request.getEarliestExpiryDate());
    record.setQuantityToOrder(request.getQuantityToOrder());
    record.setStockOutDate(request.getStockOutDate());
    record.setLastRestockDate(request.getLastRestockDate());
    record.setChp(chp);
    record.setCreatedAt(LocalDateTime.now());
    record.setRecordDate(LocalDateTime.now());

    // Save the record
    CommodityRecord saved = commodityRecordRepository.save(record);

    // Log stock change
    createStockHistoryEntry(saved, ChangeType.ADJUSTMENT);

    // Convert to DTO and return
    return convertToDTO(saved);
}

    
    public CommodityRecordDTO updateRecord(Long id, CommodityRecord recordDetails) {
        CommodityRecord record = commodityRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Record not found"));
        
        Integer oldBalance = record.getClosingBalance();
        
        record.setQuantityExpired(recordDetails.getQuantityExpired());
        record.setQuantityDamaged(recordDetails.getQuantityDamaged());
        record.setStockOnHand(recordDetails.getStockOnHand());
        record.setQuantityIssued(recordDetails.getQuantityIssued());
        record.setExcessQuantityReturned(recordDetails.getExcessQuantityReturned());
        record.setQuantityConsumed(recordDetails.getQuantityConsumed());
        record.setClosingBalance(recordDetails.getClosingBalance());
        
        // Create stock history for the change
        if (!Objects.equals(oldBalance, recordDetails.getClosingBalance())) {
            createStockHistoryEntry(record, ChangeType.ADJUSTMENT);
        }
        
        return convertToDTO(record);
    }
    
    private void createStockHistoryEntry(CommodityRecord record, ChangeType changeType) {
        CommodityStockHistory history = new CommodityStockHistory();
        history.setCommunityUnit(record.getCommunityUnit());
        history.setCommodity(record.getCommodity());
        history.setNewBalance(record.getClosingBalance());
        // history.setChangeType(record.changeType);
        history.setRecordDate(LocalDateTime.now());
        // history.setRecordedBy(record.getCr);
        
        stockHistoryRepository.save(history);
    }
    
    public void deleteRecord(Long id) {
        commodityRecordRepository.deleteById(id);
    }
    
    public List<CommodityRecord> getRecordsByCommunityUnit(Long communityUnitId) {
        return commodityRecordRepository.findByCommunityUnitId(communityUnitId);
    }
    
    public List<CommodityRecord> getLowStockRecords(Integer threshold) {
        return commodityRecordRepository.findByStockOnHandLessThan(threshold);
    }
    
    public List<CommodityRecord> getRecordsByDateRange(LocalDateTime start, LocalDateTime end) {
        return commodityRecordRepository.findByRecordDateBetween(start, end);
    }
    
   

private CommodityRecordDTO convertToDTO(CommodityRecord record) {
    CommodityRecordDTO dto = new CommodityRecordDTO();
    dto.setId(record.getId());
    dto.setCommunityUnitId(record.getCommunityUnit().getId());
    dto.setCommunityUnitName(record.getCommunityUnit().getCommunityUnitName());
    dto.setCommodityId(record.getCommodity().getId());
    dto.setCommodityName(record.getCommodity().getName());
    dto.setQuantityExpired(record.getQuantityExpired());
    dto.setQuantityDamaged(record.getQuantityDamaged());
    dto.setStockOnHand(record.getStockOnHand());
    dto.setQuantityIssued(record.getQuantityIssued());
    dto.setExcessQuantityReturned(record.getExcessQuantityReturned());
    dto.setQuantityConsumed(record.getQuantityConsumed());
    dto.setClosingBalance(record.getClosingBalance());
    dto.setLastRestockDate(record.getLastRestockDate());
    dto.setStockOutDate(record.getStockOutDate());
    dto.setConsumptionPeriod(record.getConsumptionPeriod());
    dto.setEarliestExpiryDate(record.getEarliestExpiryDate());
    dto.setQuantityToOrder(record.getQuantityToOrder());
    dto.setRecordDate(record.getRecordDate());
    dto.setChp(record.getChp());

    // ✅ Fetch createdByUsername using created_by_id from communityUnit
    CommodityUnit cu = record.getCommunityUnit();
    if (cu != null && cu.getCreatedById() != null) {
        userRepository.findById(Long.valueOf(cu.getCreatedById())).ifPresent(user -> {
            dto.setCreatedByUsername(user.getUsername());
        });
    }

    // ✅ Add location hierarchy info
    Facility facility = cu.getLinkFacility();
    if (facility != null) {
        dto.setFacilityId(facility.getId());
        dto.setFacilityName(facility.getName());

        Ward ward = facility.getWard();
        if (ward != null) {
            dto.setWardId(ward.getId());
            dto.setWardName(ward.getName());

            SubCounty subCounty = ward.getSubCounty();
            if (subCounty != null) {
                dto.setSubCountyId(subCounty.getId());
                dto.setSubCountyName(subCounty.getName());

                County county = subCounty.getCounty();
                if (county != null) {
                    dto.setCountyId(county.getId());
                    dto.setCountyName(county.getName());
                }
            }
        }
    }

    return dto;
}

}
