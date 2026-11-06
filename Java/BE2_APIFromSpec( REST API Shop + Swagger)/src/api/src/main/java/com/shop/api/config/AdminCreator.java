package com.shop.api.config;

import com.shop.api.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminCreator implements CommandLineRunner {
    private final AuthService authService;
    private final String username;
    private final String password;

    public AdminCreator(AuthService authService, @Value("${app.admin.username}")
    String username, @Value("${app.admin.password}") String password) {
        this.authService = authService;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        authService.createDefaultAdminIfNotExists(username, password);
    }
}
