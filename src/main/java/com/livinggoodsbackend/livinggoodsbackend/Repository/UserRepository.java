package com.livinggoodsbackend.livinggoodsbackend.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.dto.CommodityUnitDTO;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    @Query("SELECT u FROM User u " +
           "WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND u.id != :excludeUserId")
    List<User> searchUsers(@Param("query") String query, @Param("excludeUserId") Long excludeUserId);
    
    @Query("SELECT u FROM User u " +
           "JOIN u.onlineStatus os " +
           "WHERE os.isOnline = true")
    List<User> findOnlineUsers();

    //    List<User> users = userRepository.findByRoleNot(Role.CHP);

       List<User> findByRoleNot(Role role);

}

