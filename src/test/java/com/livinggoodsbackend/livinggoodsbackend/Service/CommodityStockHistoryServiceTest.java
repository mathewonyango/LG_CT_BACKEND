package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityStockHistory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityStockHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommodityStockHistoryServiceTest {

    @Mock
    private CommodityStockHistoryRepository stockHistoryRepository;

    @InjectMocks
    private CommodityStockHistoryService stockHistoryService;

    private CommodityStockHistory record1;
    private CommodityStockHistory record2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        record1 = new CommodityStockHistory();
        record2 = new CommodityStockHistory();

        // Flexible setup: works whether entity uses IDs or relationships
        try {
            record1.getClass().getMethod("setCommodityId", Long.class).invoke(record1, 1L);
            record1.getClass().getMethod("setCommunityUnitId", Long.class).invoke(record1, 100L);
            record2.getClass().getMethod("setCommodityId", Long.class).invoke(record2, 2L);
            record2.getClass().getMethod("setCommunityUnitId", Long.class).invoke(record2, 200L);
        } catch (Exception e) {
            // Entity uses object relationships, ignore these setters
        }

        record1.setRecordDate(LocalDateTime.now().minusDays(2));
        record2.setRecordDate(LocalDateTime.now());
    }

    @Test
    void getAllHistory_ShouldReturnAllRecords() {
        when(stockHistoryRepository.findAll()).thenReturn(Arrays.asList(record1, record2));

        List<CommodityStockHistory> result = stockHistoryService.getAllHistory();

        assertEquals(2, result.size());
        verify(stockHistoryRepository, times(1)).findAll();
    }

    @Test
    void getHistoryById_ShouldReturnCorrectRecord() {
        when(stockHistoryRepository.findById(1L)).thenReturn(Optional.of(record1));

        Optional<CommodityStockHistory> result = stockHistoryService.getHistoryById(1L);

        assertTrue(result.isPresent());
        verify(stockHistoryRepository, times(1)).findById(1L);
    }

    @Test
    void getHistoryByCommunityUnit_ShouldReturnFilteredList() {
        when(stockHistoryRepository.findByCommunityUnitId(100L)).thenReturn(List.of(record1));

        List<CommodityStockHistory> result = stockHistoryService.getHistoryByCommunityUnit(100L);

        assertEquals(1, result.size());
        verify(stockHistoryRepository, times(1)).findByCommunityUnitId(100L);
    }

    @Test
    void getHistoryByCommodity_ShouldReturnFilteredList() {
        when(stockHistoryRepository.findByCommodityId(1L)).thenReturn(List.of(record1));

        List<CommodityStockHistory> result = stockHistoryService.getHistoryByCommodity(1L);

        assertEquals(1, result.size());
        verify(stockHistoryRepository, times(1)).findByCommodityId(1L);
    }

    @Test
    void getHistoryByDateRange_ShouldReturnRecordsWithinRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();

        when(stockHistoryRepository.findByRecordDateBetween(start, end))
                .thenReturn(Arrays.asList(record1, record2));

        List<CommodityStockHistory> result = stockHistoryService.getHistoryByDateRange(start, end);

        assertEquals(2, result.size());
        verify(stockHistoryRepository, times(1)).findByRecordDateBetween(start, end);
    }
}
