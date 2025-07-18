package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.ChaCuMapping;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityUnit;
import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Model.Facility;
import com.livinggoodsbackend.livinggoodsbackend.Model.SubCounty;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRecordRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityUnitRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityUnitDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityUnitRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityUnitDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityUnitRequest;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CountyRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.SubCountyRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.UserRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.WardRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.FacilityRepository;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChaCuMappingRepository;

@Service
public class CommodityUnitService {
    
    @Autowired
    private CommodityUnitRepository communityUnitRepository;
    
    @Autowired
    private CountyRepository countyRepository;
    
    @Autowired
    private SubCountyRepository subCountyRepository;
    
    @Autowired
    private WardRepository wardRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private CommodityRecordRepository commodityRecordRepository;

    private UserRepository userRepository;
    @Autowired
    private ChaCuMappingRepository chaCuMappingRepository;

    private CommodityUnitDTO convertToDTO(CommodityUnit unit) {
        CommodityUnitDTO dto = new CommodityUnitDTO();
        dto.setId(unit.getId());
        dto.setChaName(unit.getChaName());
        dto.setCommunityUnitName(unit.getCommunityUnitName());
        dto.setTotalChps(unit.getTotalChps());
        dto.setCountyName(unit.getCounty() != null ? unit.getCounty().getName() : null);
        dto.setSubCountyId(unit.getSubCounty() != null ? unit.getSubCounty().getId() : null);
        dto.setWardName(unit.getWard() != null ? unit.getWard().getName() : null);
        dto.setLinkFacilityId(unit.getLinkFacility() != null ? unit.getLinkFacility().getId() : null);
        dto.setCreatedBy(unit.getCreatedById());
        dto.setTotalCHPsCounted(unit.getTotalCHPsCounted());
          // Fetch and set stock level
    Integer stockLevel = commodityRecordRepository.getTotalStockByCommunityUnitId(unit.getId());
    dto.setStockLevel(stockLevel);
        dto.setCreatedAt(unit.getCreatedAt());
        return dto;
    }

    // Update service methods to use DTO
    public List<CommodityUnitDTO> getAllCommunityUnits() {
        return communityUnitRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }




    public List<CommodityUnitDTO> getCommunityUnitsByCha(Long chaId) {
    // Step 1: Get CU IDs mapped to the CHA
    List<Long> cuIds = chaCuMappingRepository.findByChaId(chaId).stream()
            .map(ChaCuMapping::getCommunityUnitId)
            .distinct()
            .collect(Collectors.toList());

    if (cuIds.isEmpty()) {
        return Collections.emptyList(); // No CUs for this CHA
    }

    // Step 2: Fetch only the CUs by ID
    return communityUnitRepository.findAllById(cuIds).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    
    public Optional<CommodityUnitDTO> getCommunityUnitById(Long id) {
        return communityUnitRepository.findById(id)
            .map(this::convertToDTO);
    }

    public CommodityUnitDTO createCommodityUnit(CreateCommodityUnitRequest request) {

        County county = countyRepository.findById(request.getCountyId())
            .orElseThrow(() -> new ResourceNotFoundException("County not found"));
            
        SubCounty subCounty = subCountyRepository.findById(request.getSubCountyId())
            .orElseThrow(() -> new ResourceNotFoundException("SubCounty not found"));
            
        Ward ward = wardRepository.findById(request.getWardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found"));

        Facility facility = null;
        if (request.getLinkFacilityId() != null) {
            facility = facilityRepository.findById(request.getLinkFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
        }

        CommodityUnit CommodityUnit = new CommodityUnit();
        CommodityUnit.setCommunityUnitName(request.getCommunityUnitName());
        CommodityUnit.setChaName(request.getChaName());
        CommodityUnit.setTotalChps(request.getTotalChps());
        CommodityUnit.setWard(ward);
        CommodityUnit.setCounty(county);
        CommodityUnit.setSubCounty(subCounty);
        CommodityUnit.setLinkFacility(facility);
        CommodityUnit.setCreatedAt(LocalDateTime.now());
        CommodityUnit.setTotalCHPsCounted(request.getTotalCHPsCounted());
        CommodityUnit.setCreatedById(request.getCreatedBy() != null ? request.getCreatedBy() : null);
        // CommodityUnit.setCreatedBy(userRepository.findById(0L)
        //     .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        CommodityUnit saved = communityUnitRepository.save(CommodityUnit);
        return convertToDTO(saved);
    }
    
    public CommodityUnitDTO updateCommunityUnit(Long id, CreateCommodityUnitRequest request) {
        CommodityUnit communityUnit = communityUnitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Community Unit not found"));

        County county = countyRepository.findById(request.getCountyId())
            .orElseThrow(() -> new ResourceNotFoundException("County not found"));
            
        SubCounty subCounty = subCountyRepository.findById(request.getSubCountyId())
            .orElseThrow(() -> new ResourceNotFoundException("SubCounty not found"));
            
        Ward ward = wardRepository.findById(request.getWardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found"));

        Facility facility = null;
        if (request.getLinkFacilityId() != null) {
            facility = facilityRepository.findById(request.getLinkFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
        }
        
        communityUnit.setCommunityUnitName(request.getCommunityUnitName());
        communityUnit.setChaName(request.getChaName());
        communityUnit.setTotalChps(request.getTotalChps());
        communityUnit.setWard(ward);
        communityUnit.setCounty(county);
        communityUnit.setSubCounty(subCounty);
        communityUnit.setLinkFacility(facility);
        
        CommodityUnit updated = communityUnitRepository.save(communityUnit);
        return convertToDTO(updated);
    }
    
    public void deleteCommunityUnit(Long id) {
        communityUnitRepository.deleteById(id);
    }
    
    public List<CommodityUnitDTO> getCommunityUnitsByCounty(Long countyId) {
        return communityUnitRepository.findByCountyId(countyId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<CommodityUnitDTO> getCommunityUnitsByWard(Long wardId) {
        return communityUnitRepository.findByWardId(wardId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
