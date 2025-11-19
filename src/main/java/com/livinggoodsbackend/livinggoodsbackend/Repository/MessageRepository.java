package com.livinggoodsbackend.livinggoodsbackend.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.Message;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    
    Page<Message> findByConversationIdAndIsDeletedFalseOrderByCreatedAtDesc(String conversationId, Pageable pageable);
    
    @Query("SELECT m FROM Message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversationMessages(@Param("conversationId") String conversationId, Pageable pageable);
    
    @Query("SELECT m FROM Message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC " +
           "LIMIT 1")
    Optional<Message> findLastMessageByConversationId(@Param("conversationId") String conversationId);
    
    @Query("SELECT COUNT(m) FROM Message m " +
           "LEFT JOIN MessageReadStatus mrs ON m.id = mrs.message.id AND mrs.user.id = :userId " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id != :userId " +
           "AND mrs.id IS NULL " +
           "AND m.isDeleted = false")
    Long countUnreadMessagesForUser(@Param("conversationId") String conversationId, @Param("userId") Long userId);
    
}