package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.SubCounty;
import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import com.livinggoodsbackend.livinggoodsbackend.Repository.WardRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.SubCountyRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.WardDTO;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WardService {
    
    @Autowired
    private WardRepository wardRepository;
    
    @Autowired
    private SubCountyRepository subCountyRepository;
    
    public List<WardDTO> getAllWards() {
        return wardRepository.findAll().stream()
            .filter(ward -> ward != null)  // Filter out null wards if any
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<WardDTO> getWardById(Long id) {
        return wardRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public List<WardDTO> getWardsBySubCounty(Long subCountyId) {
        return wardRepository.findBySubCountyId(subCountyId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public WardDTO createWard(WardDTO wardDTO) {
        SubCounty subCounty = subCountyRepository.findById(wardDTO.getSubCountyId())
            .orElseThrow(() -> new RuntimeException("SubCounty not found"));
            
        Ward ward = new Ward();
        // ward.setId(null); // Ensure new ID is generated
        ward.setName(wardDTO.getName());
        ward.setSubCounty(subCounty);
        
        Ward savedWard = wardRepository.save(ward);
        return convertToDTO(savedWard);
    }
    
    public WardDTO updateWard(Long id, WardDTO wardDTO) {
        Ward ward = wardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ward not found"));
            
        ward.setName(wardDTO.getName());
        if (wardDTO.getSubCountyId() != null) {
            SubCounty subCounty = subCountyRepository.findById(wardDTO.getSubCountyId())
                .orElseThrow(() -> new RuntimeException("SubCounty not found"));
            ward.setSubCounty(subCounty);
        }
        
        Ward updatedWard = wardRepository.save(ward);
        return convertToDTO(updatedWard);
    }
    
    public void deleteWard(Long id) {
        wardRepository.deleteById(id);
    }
    
    private WardDTO convertToDTO(Ward ward) {
        WardDTO dto = new WardDTO();
        dto.setId(ward.getId());
        dto.setName(ward.getName());
        // Safely handle null SubCounty
        if (ward.getSubCounty() != null) {
            dto.setSubCountyId(ward.getSubCounty().getId());
        }
        return dto;
    }
}
