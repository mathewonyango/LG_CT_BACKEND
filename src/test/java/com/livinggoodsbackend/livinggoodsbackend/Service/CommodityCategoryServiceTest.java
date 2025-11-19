package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityCategory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityCategoryRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityCategoryDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCategoryRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommodityCategoryServiceTest {

    @Mock
    private CommodityCategoryRepository commodityCategoryRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private CommodityCategoryService commodityCategoryService;

    private CommodityCategory category;
    private CreateCategoryRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new CommodityCategory();
        category.setId(1L);
        category.setName("Health Supplies");
        category.setDescription("Medical related commodities");

        request = new CreateCategoryRequest();
        request.setName("Health Supplies");
        request.setDescription("Medical related commodities");
    }

    @Test
    void testGetAllCategories() {
        when(commodityCategoryRepository.findAll()).thenReturn(List.of(category));

        List<CommodityCategoryDTO> result = commodityCategoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Health Supplies", result.get(0).getName());
        verify(commodityCategoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryByIdFound() {
        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<CommodityCategoryDTO> result = commodityCategoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Health Supplies", result.get().getName());
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(commodityCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<CommodityCategoryDTO> result = commodityCategoryService.getCategoryById(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateCategorySuccess() {
        when(commodityCategoryRepository.findByName("Health Supplies")).thenReturn(Optional.empty());
        when(commodityCategoryRepository.save(any(CommodityCategory.class))).thenReturn(category);

        CommodityCategoryDTO result = commodityCategoryService.createCategory(request);

        assertEquals("Health Supplies", result.getName());
        verify(commodityCategoryRepository, times(1)).save(any(CommodityCategory.class));
    }

    @Test
    void testCreateCategoryAlreadyExists() {
        when(commodityCategoryRepository.findByName("Health Supplies")).thenReturn(Optional.of(category));

        assertThrows(ResourceAlreadyExistsException.class, () -> commodityCategoryService.createCategory(request));
        verify(commodityCategoryRepository, never()).save(any());
    }

    @Test
    void testUpdateCategorySuccess() {
        CreateCategoryRequest updateRequest = new CreateCategoryRequest();
        updateRequest.setName("New Category");
        updateRequest.setDescription("Updated Desc");

        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(commodityCategoryRepository.findByName("New Category")).thenReturn(Optional.empty());
        when(commodityCategoryRepository.save(any(CommodityCategory.class))).thenReturn(category);

        CommodityCategoryDTO result = commodityCategoryService.updateCategory(1L, updateRequest);

        assertEquals("New Category", result.getName());
        verify(commodityCategoryRepository, times(1)).save(any());
    }

    @Test
    void testUpdateCategoryNotFound() {
        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commodityCategoryService.updateCategory(1L, request));
    }

    @Test
    void testUpdateCategoryNameConflict() {
        CommodityCategory existing = new CommodityCategory();
        existing.setId(2L);
        existing.setName("Health Supplies");

        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(commodityCategoryRepository.findByName("Health Supplies")).thenReturn(Optional.of(existing));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> commodityCategoryService.updateCategory(1L, request));
    }

    @Test
    void testDeleteCategorySuccess() {
        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.of(category));

        commodityCategoryService.deleteCategory(1L);

        verify(commodityCategoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commodityCategoryService.deleteCategory(1L));
    }

    @Test
    void testDeleteCategoryInUse() {
        when(commodityCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doThrow(DataIntegrityViolationException.class).when(commodityCategoryRepository).delete(category);

        assertThrows(ResourceInUseException.class,
                () -> commodityCategoryService.deleteCategory(1L));
    }
}
