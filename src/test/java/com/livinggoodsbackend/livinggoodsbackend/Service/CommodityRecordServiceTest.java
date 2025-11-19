package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.*;
import com.livinggoodsbackend.livinggoodsbackend.Repository.*;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityRecordDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.CreateCommodityRecordRequest;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommodityRecordServiceTest {

    @Mock
    private CommodityRecordRepository commodityRecordRepository;
    @Mock
    private CommodityUnitRepository communityUnitRepository;
    @Mock
    private CommodityRepository commodityRepository;
    @Mock
    private CommodityStockHistoryRepository stockHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChaCuMappingRepository chaCuMappingRepository;
    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private CommodityRecordService commodityRecordService;

    private CommodityRecord record;
    private Commodity commodity;
    private CommodityUnit cu;
    private User chp;
    private Ward ward;
    private SubCounty subCounty;
    private County county;
    private Facility facility;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        county = new County();
        county.setId(10L);
        county.setName("Nairobi");

        subCounty = new SubCounty();
        subCounty.setId(11L);
        subCounty.setName("Westlands");
        subCounty.setCounty(county);

        ward = new Ward();
        ward.setId(12L);
        ward.setName("Kangemi");
        ward.setSubCounty(subCounty);

        facility = new Facility();
        facility.setId(13L);
        facility.setName("Kangemi Health Center");
        facility.setWard(ward);

        cu = new CommodityUnit();
        cu.setId(1L);
        cu.setCommunityUnitName("CU 1");
        cu.setCreatedById(5);
        cu.setLinkFacility(facility);
        cu.setWard(ward);

        chp = new User();
        chp.setId(2L);
        chp.setUsername("CHP_John");

        commodity = new Commodity();
        commodity.setId(3L);
        commodity.setName("Paracetamol");

        record = new CommodityRecord();
        record.setId(100L);
        record.setCommunityUnit(cu);
        record.setCommodity(commodity);
        record.setChp(chp);
        record.setClosingBalance(10);
        record.setRecordDate(LocalDateTime.now());
    }

    @Test
    void testGetAllRecords_Success() {
        when(commodityRecordRepository.findAllWithLocations()).thenReturn(List.of(record));
        when(userRepository.findById(5L)).thenReturn(Optional.of(chp));

        List<CommodityRecordDTO> result = commodityRecordService.getAllRecords();

        assertEquals(1, result.size());
        assertEquals("CU 1", result.get(0).getCommunityUnitName());
        verify(commodityRecordRepository, times(1)).findAllWithLocations();
    }

    @Test
    void testGetRecordById_Found() {
        when(commodityRecordRepository.findById(100L)).thenReturn(Optional.of(record));

        Optional<CommodityRecordDTO> result = commodityRecordService.getRecordById(100L);

        assertTrue(result.isPresent());
        assertEquals("Paracetamol", result.get().getCommodityName());
    }

    @Test
    void testGetRecordById_NotFound() {
        when(commodityRecordRepository.findById(200L)).thenReturn(Optional.empty());

        Optional<CommodityRecordDTO> result = commodityRecordService.getRecordById(200L);
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateRecord_Success() {
        CreateCommodityRecordRequest req = new CreateCommodityRecordRequest();
        req.setCommunityUnitId(1L);
        req.setCommodityId(3L);
        req.setChpId(2L);
        req.setClosingBalance(50);

        when(communityUnitRepository.findById(1L)).thenReturn(Optional.of(cu));
        when(commodityRepository.findById(3L)).thenReturn(Optional.of(commodity));
        when(userRepository.findById(2L)).thenReturn(Optional.of(chp));
        when(commodityRecordRepository.save(any())).thenReturn(record);

        CommodityRecordDTO result = commodityRecordService.createRecord(req);

        assertEquals("Paracetamol", result.getCommodityName());
        verify(commodityRecordRepository, times(1)).save(any());
        verify(stockHistoryRepository, times(1)).save(any());
    }

    @Test
    void testCreateRecord_CommunityUnitNotFound() {
        CreateCommodityRecordRequest req = new CreateCommodityRecordRequest();
        req.setCommunityUnitId(999L);
        when(communityUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commodityRecordService.createRecord(req));
    }

    @Test
    void testCreateRecord_CommodityNotFound() {
        CreateCommodityRecordRequest req = new CreateCommodityRecordRequest();
        req.setCommunityUnitId(1L);
        req.setCommodityId(999L);

        when(communityUnitRepository.findById(1L)).thenReturn(Optional.of(cu));
        when(commodityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commodityRecordService.createRecord(req));
    }

    @Test
    void testCreateRecord_ChpNotFound() {
        CreateCommodityRecordRequest req = new CreateCommodityRecordRequest();
        req.setCommunityUnitId(1L);
        req.setCommodityId(3L);
        req.setChpId(888L);

        when(communityUnitRepository.findById(1L)).thenReturn(Optional.of(cu));
        when(commodityRepository.findById(3L)).thenReturn(Optional.of(commodity));
        when(userRepository.findById(888L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commodityRecordService.createRecord(req));
    }

    @Test
    void testUpdateRecord_Success() {
        CommodityRecord updatedRecord = new CommodityRecord();
        updatedRecord.setClosingBalance(50);
        updatedRecord.setQuantityIssued(2);
        updatedRecord.setQuantityDamaged(1);
        updatedRecord.setQuantityExpired(0);

        when(commodityRecordRepository.findById(100L)).thenReturn(Optional.of(record));

        CommodityRecordDTO result = commodityRecordService.updateRecord(100L, updatedRecord);

        assertEquals("Paracetamol", result.getCommodityName());
        verify(stockHistoryRepository, times(1)).save(any());
    }

    @Test
    void testUpdateRecord_NotFound() {
        when(commodityRecordRepository.findById(999L)).thenReturn(Optional.empty());
        CommodityRecord r = new CommodityRecord();

        assertThrows(RuntimeException.class, () -> commodityRecordService.updateRecord(999L, r));
    }

    @Test
    void testDeleteRecord_Success() {
        commodityRecordService.deleteRecord(100L);
        verify(commodityRecordRepository, times(1)).deleteById(100L);
    }

    @Test
    void testGetRecordsByCommunityUnit() {
        when(commodityRecordRepository.findByCommunityUnitId(1L)).thenReturn(List.of(record));
        List<CommodityRecord> result = commodityRecordService.getRecordsByCommunityUnit(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetLowStockRecords() {
        when(commodityRecordRepository.findByStockOnHandLessThan(5)).thenReturn(List.of(record));
        List<CommodityRecord> result = commodityRecordService.getLowStockRecords(5);
        assertEquals(1, result.size());
    }

    @Test
    void testGetRecordsByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        when(commodityRecordRepository.findByRecordDateBetween(start, end)).thenReturn(List.of(record));

        List<CommodityRecord> result = commodityRecordService.getRecordsByDateRange(start, end);
        assertEquals(1, result.size());
    }

    @Test
    void testIsCommodityExists_True() {
        when(commodityRepository.findById(3L)).thenReturn(Optional.of(commodity));
        assertTrue(commodityRecordService.isCommodityExists(3L));
    }

    @Test
    void testIsCommodityExists_False() {
        when(commodityRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(commodityRecordService.isCommodityExists(99L));
    }
}
