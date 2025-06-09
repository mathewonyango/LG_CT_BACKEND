package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityCategory;

@Repository
public interface CommodityCategoryRepository extends JpaRepository<CommodityCategory, Long> {
    Optional<CommodityCategory> findByName(String name);
}