package com.shop.api.service;

public interface AuthService {
    void createDefaultAdminIfNotExists(String username, String password);
}
