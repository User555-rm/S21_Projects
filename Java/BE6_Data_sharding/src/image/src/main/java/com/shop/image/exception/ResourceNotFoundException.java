package com.shop.image.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Not Found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
