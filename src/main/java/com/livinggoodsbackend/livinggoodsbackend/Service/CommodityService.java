package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

// import com.livinggoodsbackend.livinggoodsbackend.Model.Category;
import com.livinggoodsbackend.livinggoodsbackend.Model.Commodity;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityCategory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityCategoryRepository;
// import com.livinggoodsbackend.livinggoodsbackend.Repository.CategoryRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRepository;


import org.springframework.dao.DataIntegrityViolationException;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceInUseException;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommodityService {
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private CommodityCategoryRepository categoryRepository;

    public List<CommodityDTO> getAllCommodities() {
        return commodityRepository.findAllWithCategories().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public CommodityDTO getCommodityById(Long id) {
        return commodityRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with id: " + id));
    }
    
    public CommodityDTO createCommodity(CreateCommodityRequest request) {
        // Check if commodity with same name exists
        if (commodityRepository.findByName(request.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Commodity with name " + request.getName() + " already exists");
        }

        CommodityCategory category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Commodity commodity = new Commodity();
        commodity.setName(request.getName());
        commodity.setDescription(request.getDescription());
        commodity.setUnitOfMeasure(request.getUnitOfMeasure());
        commodity.setCategory(category);
        
        Commodity saved = commodityRepository.save(commodity);
        return convertToDTO(saved);
    }
    
    public CommodityDTO updateCommodity(Long id, CreateCommodityRequest request) {
        Commodity commodity = commodityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with id: " + id));
        
        // Check if new name conflicts with existing commodity
        commodityRepository.findByName(request.getName())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ResourceAlreadyExistsException("Commodity with name " + request.getName() + " already exists");
                }
            });

        CommodityCategory category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        commodity.setName(request.getName());
        commodity.setDescription(request.getDescription());
        commodity.setUnitOfMeasure(request.getUnitOfMeasure());
        commodity.setCategory(category);
        
        Commodity updated = commodityRepository.save(commodity);
        return convertToDTO(updated);
    }
    
    public void deleteCommodity(Long id) {
        if (!commodityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commodity not found with id: " + id);
        }
        try {
            commodityRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("Cannot delete commodity as it is being used by other records");
        }
    }
    
   public List<CommodityDTO> getCommoditiesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return commodityRepository.findByCategoryId(categoryId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private CommodityDTO convertToDTO(Commodity commodity) {
        CommodityDTO dto = new CommodityDTO();
        dto.setId(commodity.getId());
        dto.setName(commodity.getName());
        dto.setDescription(commodity.getDescription());
        dto.setUnitOfMeasure(commodity.getUnitOfMeasure());
        dto.setCategoryId(commodity.getCategory().getId());
        dto.setCategoryName(commodity.getCategory().getName());
        return dto;
    }
    // public List<CommodityCategory> getAllCategories() {
    //     return categoryRepository.findAll();
    // }

    // public List<Commodity> getAllCommodities() {
    //     return commodityRepository.findAllWithCategories();
    // }
}