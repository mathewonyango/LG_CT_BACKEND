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

import com.livinggoodsbackend.livinggoodsbackend.Service.KafkaProducerService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private FacilityDTO convertToDTO(Facility facility) {
        FacilityDTO dto = new FacilityDTO();
        dto.setName(facility.getName());
        dto.setType(facility.getType());
        // dto.setFacilityCode(facility.getFacilityCode());
        // Set ward IDs as a list
        if (facility.getWards() != null) {
            dto.setWardIds(
                facility.getWards().stream()
                    .map(Ward::getId)
                    .collect(Collectors.toList())
            );
        } 
        return dto;
    }

    public List<FacilityDTO> getAllFacilities() {
        List <FacilityDTO> facilities = facilityRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

            for(FacilityDTO facility : facilities) {
                // kafkaProducerService.sendMessage("facilities", facility.getFacilityCode(),facility);
            }

            return facilities;
    }

    public Optional<FacilityDTO> getFacilityById(Long id) {
        return facilityRepository.findById(id)
            .map(this::convertToDTO);
    }

    public FacilityDTO createFacility(CreateFacilityRequest request) {
        // Fetch wards by IDs
        List<Ward> wards = wardRepository.findAllById(request.getWardIds());
        if (wards.size() != request.getWardIds().size()) {
            throw new ResourceNotFoundException("One or more wards not found for the provided IDs");
        }

        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setType(request.getType());
        // facility.setFacilityCode(request.getFacilityCode());
        facility.setWards(wards);

        Facility saved = facilityRepository.save(facility);

        // kafkaProducerService.sendMessage("facilities", saved.getId().toString(), saved);
        return convertToDTO(saved);
    }

    public FacilityDTO updateFacility(Long id, CreateFacilityRequest request) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facility not found with id: " + id));

        List<Ward> wards = wardRepository.findAllById(request.getWardIds());
        if (wards.size() != request.getWardIds().size()) {
            throw new ResourceNotFoundException("One or more wards not found for the provided IDs");
        }

        facility.setName(request.getName());
        facility.setType(request.getType());
        // facility.setFacilityCode(request.getFacilityCode());
        facility.setWards(wards);

        // kafkaProducerService.sendMessage("facilities", facility.getId().toString(), facility);

        return convertToDTO(facilityRepository.save(facility));
    }

    public void deleteFacility(Long id) {
        if (!facilityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Facility not found with id: " + id);
        }
        try {
            facilityRepository.deleteById(id);
            // kafkaProducerService.sendMessage("facilities", id.toString(), null);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("Facility is in use and cannot be deleted");
        }
    }

    public List<FacilityDTO> getFacilitiesByWard(Long wardId) {
        if (!wardRepository.existsById(wardId)) {
            throw new ResourceNotFoundException("Ward not found with id: " + wardId);
        }
        return facilityRepository.findAll().stream()
            .filter(facility -> facility.getWards() != null && facility.getWards().stream().anyMatch(w -> w.getId().equals(wardId)))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<FacilityDTO> getFacilitiesByType(String type) {
        return facilityRepository.findByType(type).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}