package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.SubCounty;

@Repository
public interface SubCountyRepository extends JpaRepository<SubCounty, Long> {
    List<SubCounty> findByCountyId(Long countyId);
    Optional<SubCounty> findByNameAndCountyId(String name, Long countyId);
}
