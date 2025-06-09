package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.County;

@Repository
public interface CountyRepository extends JpaRepository<County, Long> {
    Optional<County> findByName(String name);
    Optional<County> findByCode(String code);
}