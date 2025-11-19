package com.livinggoodsbackend.livinggoodsbackend.enums;

public enum DeliveryStatus {
    SENT,        // Just sent by sender
    DELIVERED,   // Inserted in DB, available to recipients
    READ         // Explicitly marked as read by user(s)
}
