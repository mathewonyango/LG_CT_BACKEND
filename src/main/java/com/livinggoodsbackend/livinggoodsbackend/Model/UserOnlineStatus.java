package com.livinggoodsbackend.livinggoodsbackend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_online_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOnlineStatus {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "last_seen", nullable = false)
    @UpdateTimestamp
    private Instant lastSeen;

}
