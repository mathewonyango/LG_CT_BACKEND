package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long > {
    List<Ward> findBySubCountyId(Long subCountyId);
    Optional<Ward> findByNameAndSubCountyId(String name, Long subCountyId);
}
