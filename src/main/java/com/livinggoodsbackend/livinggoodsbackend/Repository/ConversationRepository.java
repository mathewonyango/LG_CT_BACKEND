package com.livinggoodsbackend.livinggoodsbackend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.Conversation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    
    @Query("SELECT DISTINCT c FROM Conversation c " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId AND p.isActive = true " +
           "ORDER BY c.updatedAt DESC")
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Conversation c " +
           "JOIN c.participants p1 " +
           "JOIN c.participants p2 " +
           "WHERE p1.user.id = :userId1 AND p2.user.id = :userId2 " +
           "AND c.isGroup = false AND p1.isActive = true AND p2.isActive = true")
    Optional<Conversation> findPrivateConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);


    @Query("SELECT DISTINCT c FROM Conversation c " +
       "JOIN c.messages m " +
       "WHERE :userId MEMBER OF c.participants " +
       "ORDER BY c.updatedAt DESC")
List<Conversation> findByUserIdWithMessages(@Param("userId") Long userId);

}
