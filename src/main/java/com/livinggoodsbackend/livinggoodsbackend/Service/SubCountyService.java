package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Model.SubCounty;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CountyRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.SubCountyRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.SubCountyDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateSubCountyRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
//
//kafka pproducer
import com.livinggoodsbackend.livinggoodsbackend.Service.KafkaProducerService;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class SubCountyService {
    
    @Autowired
    private SubCountyRepository subCountyRepository;
    
    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public List<SubCountyDTO> getAllSubCounties() {

        List <SubCountyDTO> subCounties = subCountyRepository.findAll()
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

        return subCounties;
    }
    
    public Optional<SubCountyDTO> getSubCountyById(Long id) {
        return subCountyRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public List<SubCountyDTO> getSubCountiesByCounty(Long countyId) {
        return subCountyRepository.findByCountyId(countyId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public SubCountyDTO createSubCounty(CreateSubCountyRequest request) {
        // Check if name already exists in the same county
        if (subCountyRepository.findByNameAndCountyId(request.getName(), request.getCountyId()).isPresent()) {
            throw new IllegalArgumentException("Sub-county with this name already exists in the specified county");
        }

        // Find county
        County county = countyRepository.findById(request.getCountyId())
            .orElseThrow(() -> new ResourceNotFoundException("County not found with id: " + request.getCountyId()));

        // Create new sub-county
        SubCounty subCounty = new SubCounty();
        subCounty.setName(request.getName());
        subCounty.setCounty(county);

        // Save and convert to DTO
        SubCounty savedSubCounty = subCountyRepository.save(subCounty);
        // kafkaProducerService.sendMessage("sub-county", savedSubCounty.getId().toString(), savedSubCounty);
        return convertToDTO(savedSubCounty);
    }
    
    public SubCountyDTO updateSubCounty(Long id, CreateSubCountyRequest request) {
        SubCounty subCounty = subCountyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sub-county not found"));
            
        County county = countyRepository.findById(request.getCountyId())
            .orElseThrow(() -> new ResourceNotFoundException("County not found"));
            
        subCounty.setName(request.getName());
        subCounty.setCounty(county);
        
        SubCounty updated = subCountyRepository.save(subCounty);
        // kafkaProducerService.sendMessage("sub-county", updated.getId().toString(), updated);
        return convertToDTO(updated);
    }
    
    public void deleteSubCounty(Long id) {
        if (!subCountyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sub-county not found");
        }
        subCountyRepository.deleteById(id);
    }
    
    private SubCountyDTO convertToDTO(SubCounty subCounty) {
        SubCountyDTO dto = new SubCountyDTO();
        dto.setId(subCounty.getId());
        dto.setName(subCounty.getName());
        dto.setCountyId(subCounty.getCounty().getId());
        dto.setCountyName(subCounty.getCounty().getName());
        return dto;
    }
}