package com.shop.api.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
    super("Необходима авторизация");
  }
    public UnauthorizedException(String message) {
        super(message);
    }
}
