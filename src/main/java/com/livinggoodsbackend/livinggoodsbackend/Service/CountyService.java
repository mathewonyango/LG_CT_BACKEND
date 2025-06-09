package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CountyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import com.livinggoodsbackend.livinggoodsbackend.dto.CountyDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCountyRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.ApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CountyService {
    
    @Autowired
    private CountyRepository countyRepository;

    public List<CountyDTO> getAllCounties() {
        return countyRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<CountyDTO> getCountyById(Long id) {
        return countyRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public CountyDTO createCounty(CreateCountyRequest request) {
        // Check if county with same code exists
        if (countyRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("County with code " + request.getCode() + " already exists");
        }

        County county = new County();
        county.setName(request.getName());
        county.setCode(request.getCode());
        
        County saved = countyRepository.save(county);
        return convertToDTO(saved);
    }
    
    public CountyDTO updateCounty(Long id, CreateCountyRequest request) {
        County county = countyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("County not found with id: " + id));
        
        // Check if new code conflicts with existing county
        countyRepository.findByCode(request.getCode())
            .ifPresent(existingCounty -> {
                if (!existingCounty.getId().equals(id)) {
                    throw new IllegalArgumentException("County with code " + request.getCode() + " already exists");
                }
            });
        
        county.setName(request.getName());
        county.setCode(request.getCode());
        
        County updated = countyRepository.save(county);
        return convertToDTO(updated);
    }
    
    public void deleteCounty(Long id) {
        if (!countyRepository.existsById(id)) {
            throw new ResourceNotFoundException("County not found with id: " + id);
        }
        countyRepository.deleteById(id);
    }

    private CountyDTO convertToDTO(County county) {
        CountyDTO dto = new CountyDTO();
        dto.setId(county.getId());
        dto.setName(county.getName());
        dto.setCode(county.getCode());
        return dto;
    }
}