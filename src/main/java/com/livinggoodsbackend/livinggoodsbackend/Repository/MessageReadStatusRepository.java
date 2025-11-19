package com.livinggoodsbackend.livinggoodsbackend.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.MessageReadStatus;
import com.livinggoodsbackend.livinggoodsbackend.enums.DeliveryStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
    
    boolean existsByMessageIdAndUserId(String messageId, Long userId);
    
    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.message.id IN :messageIds")
    List<MessageReadStatus> findByMessageIds(@Param("messageIds") List<String> messageIds);
    Optional<MessageReadStatus> findByMessageIdAndUserId(String messageId, Long userId);
    List<MessageReadStatus> findByUserIdAndStatus(Long userId, DeliveryStatus status);


    


    
}
