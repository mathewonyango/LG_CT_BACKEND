package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.Commodity;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityCategory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityCategoryRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceAlreadyExistsException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceInUseException;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommodityServiceTest {

    @Mock
    private CommodityRepository commodityRepository;
    @Mock
    private CommodityCategoryRepository categoryRepository;

    @InjectMocks
    private CommodityService commodityService;

    private CommodityCategory category;
    private Commodity commodity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new CommodityCategory();
        category.setId(1L);
        category.setName("Medicine");

        commodity = new Commodity();
        commodity.setId(100L);
        commodity.setName("Paracetamol");
        commodity.setDescription("Pain relief");
        commodity.setUnitOfMeasure("tablets");
        commodity.setCategory(category);
    }

    // ✅ getAllCommodities
    @Test
    void testGetAllCommodities_Success() {
        when(commodityRepository.findAllWithCategories()).thenReturn(List.of(commodity));

        List<CommodityDTO> result = commodityService.getAllCommodities();

        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).getName());
        assertEquals("Medicine", result.get(0).getCategoryName());
    }

    // ✅ getCommodityById found
    @Test
    void testGetCommodityById_Found() {
        when(commodityRepository.findById(100L)).thenReturn(Optional.of(commodity));

        CommodityDTO result = commodityService.getCommodityById(100L);

        assertEquals("Paracetamol", result.getName());
        assertEquals(1L, result.getCategoryId());
    }

    // ✅ getCommodityById not found
    @Test
    void testGetCommodityById_NotFound() {
        when(commodityRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commodityService.getCommodityById(200L));
    }

    // ✅ createCommodity success
    @Test
    void testCreateCommodity_Success() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("Ibuprofen");
        req.setDescription("Pain killer");
        req.setUnitOfMeasure("capsules");
        req.setCategoryId(1L);

        when(commodityRepository.findByName("Ibuprofen")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(commodityRepository.save(any())).thenReturn(commodity);

        CommodityDTO result = commodityService.createCommodity(req);

        assertNotNull(result);
        assertEquals("Paracetamol", result.getName()); // returned from saved mock
        verify(commodityRepository).save(any());
    }

    // ✅ createCommodity when name already exists
    @Test
    void testCreateCommodity_AlreadyExists() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("Paracetamol");

        when(commodityRepository.findByName("Paracetamol"))
                .thenReturn(Optional.of(commodity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> commodityService.createCommodity(req));
    }

    // ✅ createCommodity when category not found
    @Test
    void testCreateCommodity_CategoryNotFound() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("NewDrug");
        req.setCategoryId(999L);

        when(commodityRepository.findByName("NewDrug")).thenReturn(Optional.empty());
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commodityService.createCommodity(req));
    }

    // ✅ updateCommodity success
    @Test
    void testUpdateCommodity_Success() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("Paracetamol");
        req.setDescription("Updated desc");
        req.setUnitOfMeasure("bottle");
        req.setCategoryId(1L);

        when(commodityRepository.findById(100L)).thenReturn(Optional.of(commodity));
        when(commodityRepository.findByName("Paracetamol")).thenReturn(Optional.of(commodity));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(commodityRepository.save(any())).thenReturn(commodity);

        CommodityDTO result = commodityService.updateCommodity(100L, req);

        assertEquals("Paracetamol", result.getName());
        verify(commodityRepository).save(any());
    }

    // ✅ updateCommodity when commodity not found
    @Test
    void testUpdateCommodity_NotFound() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("Drug");

        when(commodityRepository.findById(500L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commodityService.updateCommodity(500L, req));
    }

    // ✅ updateCommodity when duplicate name exists
    @Test
    void testUpdateCommodity_DuplicateNameExists() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("AnotherDrug");
        req.setCategoryId(1L);

        Commodity other = new Commodity();
        other.setId(999L);
        other.setName("AnotherDrug");
        other.setCategory(category);

        when(commodityRepository.findById(100L)).thenReturn(Optional.of(commodity));
        when(commodityRepository.findByName("AnotherDrug")).thenReturn(Optional.of(other));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> commodityService.updateCommodity(100L, req));
    }

    // ✅ updateCommodity when category not found
    @Test
    void testUpdateCommodity_CategoryNotFound() {
        CreateCommodityRequest req = new CreateCommodityRequest();
        req.setName("Paracetamol");
        req.setCategoryId(5L);

        when(commodityRepository.findById(100L)).thenReturn(Optional.of(commodity));
        when(commodityRepository.findByName("Paracetamol")).thenReturn(Optional.of(commodity));
        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commodityService.updateCommodity(100L, req));
    }

    // ✅ deleteCommodity success
    @Test
    void testDeleteCommodity_Success() {
        when(commodityRepository.existsById(100L)).thenReturn(true);

        commodityService.deleteCommodity(100L);

        verify(commodityRepository).deleteById(100L);
    }

    // ✅ deleteCommodity not found
    @Test
    void testDeleteCommodity_NotFound() {
        when(commodityRepository.existsById(100L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commodityService.deleteCommodity(100L));
    }

    // ✅ deleteCommodity in use (DataIntegrityViolation)
    @Test
    void testDeleteCommodity_InUse() {
        when(commodityRepository.existsById(100L)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class)
                .when(commodityRepository).deleteById(100L);

        assertThrows(ResourceInUseException.class,
                () -> commodityService.deleteCommodity(100L));
    }

    // ✅ getCommoditiesByCategory success
    @Test
    void testGetCommoditiesByCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(commodityRepository.findByCategoryId(1L)).thenReturn(List.of(commodity));

        List<CommodityDTO> result = commodityService.getCommoditiesByCategory(1L);

        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).getName());
    }

    // ✅ getCommoditiesByCategory category not found
    @Test
    void testGetCommoditiesByCategory_CategoryNotFound() {
        when(categoryRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commodityService.getCommoditiesByCategory(999L));
    }
}
