package com.livinggoodsbackend.livinggoodsbackend.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityUnitDTO;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<CommodityUnitDTO> findById(Integer createdBy);
    List<User> findByRole(Role role);
    Optional<User> findById(Long id);

}

