package com.livinggoodsbackend.livinggoodsbackend.exception;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}