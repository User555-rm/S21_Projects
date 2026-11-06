package com.shop.api.service;

import com.shop.api.dto.ChangePassDto;
import com.shop.api.dto.LoginDto;
import com.shop.api.dto.RegisterDto;

public interface AuthService {
    String register(RegisterDto dto);
    String login(LoginDto dto);
    void changePassword(String email, ChangePassDto dto);
    void resetPassword(String email);
}
