package com.livinggoodsbackend.livinggoodsbackend.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.UserOnlineStatus;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOnlineStatusRepository extends JpaRepository<UserOnlineStatus, Long> {

    /**
     * Find all users who are currently online
     */
    List<UserOnlineStatus> findByIsOnlineTrue();

    /**
     * Find all users who are currently offline
     */
    List<UserOnlineStatus> findByIsOnlineFalse();

    /**
     * Find users by their IDs
     */
    List<UserOnlineStatus> findByUserIdIn(List<Long> userIds);

    /**
     * Find users who are marked as online but haven't been seen since the threshold
     */
    List<UserOnlineStatus> findByIsOnlineTrueAndLastSeenBefore(Instant threshold);

    /**
     * Find users last seen before a certain time
     */
    List<UserOnlineStatus> findByLastSeenBefore(LocalDateTime threshold);

    /**
     * Count online users
     */
    long countByIsOnlineTrue();

    /**
     * Count offline users
     */
    long countByIsOnlineFalse();

    /**
     * Find users online within a time range
     */
    List<UserOnlineStatus> findByLastSeenBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Custom query to get online users with user details
     */
    @Query("SELECT uos FROM UserOnlineStatus uos JOIN FETCH uos.user WHERE uos.isOnline = true")
    List<UserOnlineStatus> findOnlineUsersWithDetails();

    /**
     * Custom query to get user status with user details
     */
    @Query("SELECT uos FROM UserOnlineStatus uos JOIN FETCH uos.user WHERE uos.userId = :userId")
    UserOnlineStatus findByUserIdWithDetails(@Param("userId") Long userId);

    /**
     * Update user's online status
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatus uos SET uos.isOnline = :isOnline, uos.lastSeen = :lastSeen WHERE uos.userId = :userId")
    int updateUserStatus(@Param("userId") Long userId,
            @Param("isOnline") Boolean isOnline,
            @Param("lastSeen") LocalDateTime lastSeen);

    /**
     * Bulk update users to offline
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatus uos SET uos.isOnline = false WHERE uos.userId IN :userIds")
    int bulkSetUsersOffline(@Param("userIds") List<Long> userIds);

    /**
     * Delete old status records
     */
    @Modifying
    @Transactional
    int deleteByLastSeenBefore(LocalDateTime cutoff);

    /**
     * Find recently active users (within last X minutes)
     */
    @Query("SELECT uos FROM UserOnlineStatus uos WHERE uos.lastSeen >= :threshold")
    List<UserOnlineStatus> findRecentlyActiveUsers(@Param("threshold") LocalDateTime threshold);

     @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatus u " +
           "SET u.isOnline = false " +
           "WHERE u.isOnline = true " +
           "AND u.lastSeen < :threshold")
    int markInactiveUsersOffline(@Param("threshold") Instant threshold);


}