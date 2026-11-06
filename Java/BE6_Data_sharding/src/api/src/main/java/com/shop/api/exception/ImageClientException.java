package com.shop.api.exception;

public class ImageClientException extends RuntimeException {
    public ImageClientException() { super("Ошибка вызова image-service"); }
    public ImageClientException(String message) { super(message); }
    public ImageClientException(String message, Throwable cause) { super(message, cause); }
}
