package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.Facility;
import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import com.livinggoodsbackend.livinggoodsbackend.Repository.FacilityRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.WardRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateFacilityRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.FacilityDTO;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceInUseException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FacilityService {
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private WardRepository wardRepository;

    private FacilityDTO convertToDTO(Facility facility) {
        FacilityDTO dto = new FacilityDTO();
        dto.setName(facility.getName());
        // dto.setFacilityCode(facility.getFacilityCode());
        dto.setType(facility.getType());
        
        if (facility.getWard() != null) {
            dto.setWardId(facility.getWard().getId());
        }
        return dto;
    }

    public List<FacilityDTO> getAllFacilities() {
        return facilityRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<FacilityDTO> getFacilityById(Long id) {
        return facilityRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public FacilityDTO createFacility(CreateFacilityRequest request) {
        // Check for duplicate facility code
        

        // Validate ward exists
        Ward ward = wardRepository.findById(request.getWardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found with id: " + request.getWardId()));

        // Create new facility
        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setType(request.getType());
        facility.setWard(ward);

        // Save and convert to DTO
        Facility saved = facilityRepository.save(facility);
        return convertToDTO(saved);
    }
    
    public FacilityDTO updateFacility(Long id, CreateFacilityRequest request) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facility not found with id: " + id));
        
    
        Ward ward = wardRepository.findById(request.getWardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found with id: " + request.getWardId()));

        facility.setName(request.getName());
        // facility.setFacilityCode(request.getFacilityCode());
        facility.setType(request.getType());
        facility.setWard(ward);
        
        return convertToDTO(facilityRepository.save(facility));
    }
    
    public void deleteFacility(Long id) {
        if (!facilityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Facility not found with id: " + id);
        }
        try {
            facilityRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("Facility is in use and cannot be deleted");
        }
    }
    
    public List<FacilityDTO> getFacilitiesByWard(Long wardId) {
        if (!wardRepository.existsById(wardId)) {
            throw new ResourceNotFoundException("Ward not found with id: " + wardId);
        }
        return facilityRepository.findByWardId(wardId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<FacilityDTO> getFacilitiesByType(String type) {
        return facilityRepository.findByType(type).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}