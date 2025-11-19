package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityRecord;
import com.livinggoodsbackend.livinggoodsbackend.Model.CommunityUnitStock;
import com.livinggoodsbackend.livinggoodsbackend.Model.StockDistribution;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRecordRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommodityUnitRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.CommunityUnitStockRepository;
import com.livinggoodsbackend.livinggoodsbackend.Repository.StockDistributionRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityRecordDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.DtoConverter;
import com.livinggoodsbackend.livinggoodsbackend.dto.StockDtos.DistributeStockRequestDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.StockDtos.OrderRequestDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.StockDtos.ReceiveStockRequestDto;

import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;

@Service
public class StockService {

    private final CommunityUnitStockRepository communityUnitStockRepository;
    private final StockDistributionRepository distributionRepo;
    private final CommodityRecordRepository commodityRecordsRepo;
    private final DtoConverter dtoConverter;
    private final CommodityRepository commodityRepository;
    private final CommodityUnitRepository commodityUnitRepository;

    /************* ✨ Windsurf Command ⭐ *************/
    /**
    
     */
    /******* 9c55edbf-0534-4065-9fd6-205aaa09c703 *******/

    @Autowired
    public StockService(CommunityUnitStockRepository communityUnitStockRepository,
            StockDistributionRepository distributionRepo,
            CommodityRecordRepository commodityRecordsRepo,
            DtoConverter dtoConverter,
            CommodityRepository commodityRepository,
            CommodityUnitRepository commodityUnitRepository) {
        this.communityUnitStockRepository = communityUnitStockRepository;
        this.distributionRepo = distributionRepo;
        this.commodityRecordsRepo = commodityRecordsRepo;
        this.dtoConverter = dtoConverter;
        this.commodityRepository = commodityRepository;
        this.commodityUnitRepository = commodityUnitRepository;
    }

    @Transactional
    public Integer aggregateChpOrders(Long cuId, Long commodityId) {
        return communityUnitStockRepository.sumChpQuantityToOrder(cuId, commodityId);
    }

    @Transactional
    public CommunityUnitStock updateOrder(Long cuId, OrderRequestDto request) {
        CommunityUnitStock stock = communityUnitStockRepository.findLatestByCommunityUnitIdAndCommodityId(cuId,
                request.getCommodityId());
        if (stock == null) {
            stock = new CommunityUnitStock();
            stock.setCommunityUnitId(cuId);
            stock.setCommodityId(request.getCommodityId());
            stock.setCreatedAt(LocalDateTime.now());
        }
        stock.setQuantityToOrder(request.getQuantityToOrder());
        stock.setUpdatedAt(LocalDateTime.now());
        return communityUnitStockRepository.save(stock);
    }

    @Transactional
    public CommunityUnitStock receiveStock(Long cuId, ReceiveStockRequestDto request) {
        CommunityUnitStock stock = communityUnitStockRepository.findLatestByCommunityUnitIdAndCommodityId(cuId,
                request.getCommodityId());
        if (stock == null) {
            stock = new CommunityUnitStock();
            stock.setCommunityUnitId(cuId);
            stock.setCommodityId(request.getCommodityId());
            stock.setCreatedAt(LocalDateTime.now());
        }
        stock.setQuantityReceived(stock.getQuantityReceived() + request.getQuantityReceived());
        stock.setNotes(request.getNotes());
        stock.setUpdatedAt(LocalDateTime.now());
        return communityUnitStockRepository.save(stock);
    }

    @Transactional
    public StockDistribution distributeStock(DistributeStockRequestDto request) {
        CommunityUnitStock cuStock = communityUnitStockRepository.findLatestByCommunityUnitIdAndCommodityId(
                request.getCommunityUnitId(), request.getCommodityId());
        if (cuStock == null) {
            throw new IllegalStateException("No stock record found for community unit " +
                    commodityUnitRepository.findById(request.getCommunityUnitId()).get().getCommunityUnitName()
                    + " and commodity " + commodityRepository.findById(request.getCommodityId()).get().getName());
        }
        if (cuStock.getStockOnHand() < request.getQuantityToDistribute()) {
            throw new IllegalStateException("Insufficient stock for  " +
                    commodityRepository.findById(request.getCommodityId()).get().getName() + ". Available: "
                    + cuStock.getStockOnHand() +
                    ", Requested: " + request.getQuantityToDistribute());
        }

        Integer totalOrdered = commodityRecordsRepo.sumQuantityToOrderForCurrentMonth(
                request.getChpId(), request.getCommodityId());

        if (totalOrdered == null || totalOrdered < request.getQuantityToDistribute()) {
            throw new IllegalStateException("Distribution exceeds CHP Qnty to Order" +
                    " for " + commodityRepository.findById(request.getCommodityId()).get().getName() +
                    ", Max Qnty that can be Ordered: " + (getQntyToOrderForChp(request.getChpId(), request.getCommodityId())) +
                    ", Amount Requested: " + request.getQuantityToDistribute() + ", Requested: " + request.getQuantityToDistribute() +
                    ", Available: " + cuStock.getStockOnHand());
                    
        }

        // Fetch current month records
        List<CommodityRecord> monthlyRecords = commodityRecordsRepo.findAllForCurrentMonth(
                request.getChpId(), request.getCommodityId());

        // Check "certification" → means there is at least one record in current month
        boolean certified = !monthlyRecords.isEmpty();

        if (certified) {
            int remainingToReduce = request.getQuantityToDistribute();

            for (CommodityRecord record : monthlyRecords) {
                if (remainingToReduce <= 0)
                    break;

                int available = record.getQuantityToOrder();

                if (available > 0) {
                    int deduction = Math.min(available, remainingToReduce);
                    // record.setQuantityToOrder(available - deduction);
                    // record.setQuantityIssued(record.getQuantityIssued() + request.getQuantityToDistribute());
                    // record.setQuantityToOrder(record.getQuantityToOrder() - request.getQuantityToDistribute());
                    remainingToReduce -= deduction;
                    commodityRecordsRepo.save(record);
                }
            }
        }

        StockDistribution distribution = new StockDistribution();
        distribution.setCommunityUnitId(request.getCommunityUnitId());
        distribution.setChaId(request.getChaId());
        distribution.setChpId(request.getChpId());
        distribution.setCommodityId(request.getCommodityId());
        distribution.setQuantityOrdered(monthlyRecords.stream().mapToInt(CommodityRecord::getQuantityToOrder).sum());
        distribution.setQuantityDistributed(request.getQuantityToDistribute());
        distribution.setNotes(request.getNotes());
        distribution.setDistributionDate(LocalDateTime.now());
        distribution.setCreatedAt(LocalDateTime.now());

        return distributionRepo.save(distribution);
    }



    public Integer getQntyToOrderForChp(Long chpId, Long commodityId) {
        return commodityRecordsRepo.sumQuantityToOrderForCurrentMonth(chpId, commodityId);
    }

    @Transactional
    public List<CommunityUnitStock> getCuStockSummary(Long cuId) {
        return communityUnitStockRepository.findByCommunityUnitId(cuId);
    }

    // @Transactional(readOnly = true)
    public List<StockDistribution> getChpDistributionHistory(
            Long chpId,
            Long commodityId,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        return distributionRepo.findByChpId(chpId, commodityId, fromDate, toDate);
    }

}
