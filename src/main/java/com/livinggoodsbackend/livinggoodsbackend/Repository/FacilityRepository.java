package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.Facility;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByWardId(Long wardId);
    Optional<Facility> findByFacilityCode(String facilityCode);
    Optional<Facility> findByNameAndWardId(String name, Long wardId);
    List<Facility> findByType(String type);
}

