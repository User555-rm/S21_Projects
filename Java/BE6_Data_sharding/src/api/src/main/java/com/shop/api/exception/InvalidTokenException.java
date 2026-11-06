package com.shop.api.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() { super("Invalid Token"); }
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
