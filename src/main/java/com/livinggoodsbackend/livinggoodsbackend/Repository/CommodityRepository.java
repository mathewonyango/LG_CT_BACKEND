package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.Commodity;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Long> {
    List<Commodity> findByCategoryId(Long categoryId);
    Optional<Commodity> findByName(String name);
    List<Commodity> findByUnitOfMeasure(String unitOfMeasure);

    @Query("SELECT c FROM Commodity c LEFT JOIN FETCH c.category")
    List<Commodity> findAllWithCategories();
}

