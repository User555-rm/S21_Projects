package com.shop.api.service;

public interface JwtService {
    String generateToken(String email);
    String extractEmail(String token);
}
