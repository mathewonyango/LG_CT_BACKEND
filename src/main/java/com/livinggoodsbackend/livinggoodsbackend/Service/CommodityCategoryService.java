package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityCategory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityCategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityCategoryDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCategoryRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.*;
import java.util.stream.Collectors;
 import com.livinggoodsbackend.livinggoodsbackend.Service.KafkaProducerService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommodityCategoryService {
    
    @Autowired
    private CommodityCategoryRepository commodityCategoryRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public List<CommodityCategoryDTO> getAllCategories() {
        return commodityCategoryRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<CommodityCategoryDTO> getCategoryById(Long id) {
        return commodityCategoryRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public CommodityCategoryDTO createCategory(CreateCategoryRequest request) {
        if (commodityCategoryRepository.findByName(request.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Category with name " + request.getName() + " already exists");
        }

        CommodityCategory category = new CommodityCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        CommodityCategory saved = commodityCategoryRepository.save(category);
        // kafkaProducerService.sendMessage("commodity-categories", saved.getId().toString(), saved);
        return convertToDTO(saved);
    }
    
    public CommodityCategoryDTO updateCategory(Long id, CreateCategoryRequest request) {
        CommodityCategory category = commodityCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if new name conflicts with existing category
        commodityCategoryRepository.findByName(request.getName())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ResourceAlreadyExistsException("Category with name " + request.getName() + " already exists");
                }
            });
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        CommodityCategory updated = commodityCategoryRepository.save(category);
        // kafkaProducerService.sendMessage("commodity-categories", updated.getId().toString(), updated);
        return convertToDTO(updated);
    }
    
    public void deleteCategory(Long id) {
        CommodityCategory category = commodityCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
            
        try {
            commodityCategoryRepository.delete(category);
            // kafkaProducerService.sendMessage("commodity-categories", id.toString(), null);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("Category is in use and cannot be deleted");
        }
    }

    private CommodityCategoryDTO convertToDTO(CommodityCategory category) {
        CommodityCategoryDTO dto = new CommodityCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
