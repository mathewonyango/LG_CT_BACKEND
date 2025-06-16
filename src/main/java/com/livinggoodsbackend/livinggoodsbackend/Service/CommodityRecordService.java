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
import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChangeType;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRecordRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityStockHistoryRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityUnitRepository;
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
    
    public List<CommodityRecordDTO> getAllRecords() {
        return commodityRecordRepository.findAllWithLocations().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<CommodityRecordDTO> getRecordById(Long id) {
        return commodityRecordRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public CommodityRecordDTO createRecord(CreateCommodityRecordRequest request) {
        CommodityUnit communityUnit = communityUnitRepository.findById(request.getCommunityUnitId())
            .orElseThrow(() -> new ResourceNotFoundException("Community unit not found"));
            
        Commodity commodity = commodityRepository.findById(request.getCommodityId())
            .orElseThrow(() -> new ResourceNotFoundException("Commodity not found"));

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
        record.setEarliestExpiryDate(request.getEarliestExpiryDate()); // Add this line
        record.setQuantityToOrder(request.getQuantityToOrder());        // Add this line
        record.setCreatedAt(LocalDateTime.now());
        record.setRecordDate(LocalDateTime.now());
        record.setLastRestockDate(request.getLastRestockDate());
        record.setStockOutDate(request.getStockOutDate());
        // record.setCreatedBy(request.getCreatedBy()); // Assuming you have a way to set the user who created the record
        // record.setUpdatedAt(LocalDateTime.now());

        CommodityRecord saved = commodityRecordRepository.save(record);
        
        // Create stock history entry
        createStockHistoryEntry(saved, ChangeType.ADJUSTMENT);
        
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
        history.setRecordedBy(record.getCreatedBy());
        
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
        dto.setCommunityUnitName(record.getCommunityUnit().getCommunityUnitName()); // Changed from getChaName()
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
        dto.setEarliestExpiryDate(record.getEarliestExpiryDate());  // Add this line
        dto.setQuantityToOrder(record.getQuantityToOrder());        // Add this line
        dto.setRecordDate(record.getRecordDate());
        dto.setCreatedByUsername(record.getCreatedBy() != null ? record.getCreatedBy().getUsername() : null);
        
        // Add location hierarchy information
        CommodityUnit communityUnit = record.getCommunityUnit();
        if (communityUnit != null) {
            Facility facility = communityUnit.getLinkFacility();
            if (facility != null) {
                dto.setFacilityId(facility.getId());    // Add facility ID
                dto.setFacilityName(facility.getName()); // Add facility name
                
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
        }
        
        return dto;
    }
}
