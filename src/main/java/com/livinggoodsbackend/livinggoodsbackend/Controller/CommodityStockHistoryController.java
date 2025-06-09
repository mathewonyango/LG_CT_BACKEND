package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityStockHistory;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ChangeType;
import com.livinggoodsbackend.livinggoodsbackend.Service.CommodityStockHistoryService;

@RestController
@RequestMapping("/api/stock-history")
@CrossOrigin(origins = "*")
public class CommodityStockHistoryController {

    @Autowired
    private CommodityStockHistoryService stockHistoryService;

    @GetMapping
    public List<CommodityStockHistory> getAllHistory() {
        return stockHistoryService.getAllHistory();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommodityStockHistory> getHistoryById(@PathVariable Long id) {
        Optional<CommodityStockHistory> history = stockHistoryService.getHistoryById(id);
        return history.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/community-unit/{communityUnitId}")
    public List<CommodityStockHistory> getHistoryByCommunityUnit(@PathVariable Long communityUnitId) {
        return stockHistoryService.getHistoryByCommunityUnit(communityUnitId);
    }

    @GetMapping("/commodity/{commodityId}")
    public List<CommodityStockHistory> getHistoryByCommodity(@PathVariable Long commodityId) {
        return stockHistoryService.getHistoryByCommodity(commodityId);
    }

    // @GetMapping("/change-type/{changeType}")
    // public List<CommodityStockHistory> getHistoryByChangeType(
    //         @PathVariable ChangeType changeType) {
    //     return stockHistoryService.getHistoryByChangeType(changeType);
    // }

    @GetMapping("/date-range")
    public List<CommodityStockHistory> getHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return stockHistoryService.getHistoryByDateRange(start, end);
    }
}