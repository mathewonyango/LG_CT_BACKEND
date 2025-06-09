package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityStockHistory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChangeType;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityStockHistoryRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommodityStockHistoryService {
    
    @Autowired
    private CommodityStockHistoryRepository stockHistoryRepository;
    
    public List<CommodityStockHistory> getAllHistory() {
        return stockHistoryRepository.findAll();
    }
    
    public Optional<CommodityStockHistory> getHistoryById(Long id) {
        return stockHistoryRepository.findById(id);
    }
    
    public List<CommodityStockHistory> getHistoryByCommunityUnit(Long communityUnitId) {
        return stockHistoryRepository.findByCommunityUnitId(communityUnitId);
    }
    
    public List<CommodityStockHistory> getHistoryByCommodity(Long commodityId) {
        return stockHistoryRepository.findByCommodityId(commodityId);
    }
    
    // public List<CommodityStockHistory> getHistoryByChangeType(ChangeType changeType) {
    //     return stockHistoryRepository.findByChangeType(changeType);
    // }
    
    public List<CommodityStockHistory> getHistoryByDateRange(LocalDateTime start, LocalDateTime end) {
        return stockHistoryRepository.findByRecordDateBetween(start, end);
    }
}