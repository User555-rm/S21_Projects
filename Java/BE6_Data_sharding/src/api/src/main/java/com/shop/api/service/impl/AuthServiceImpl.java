package com.shop.api.service.impl;

import com.shop.api.dto.ChangePassDto;
import com.shop.api.dto.LoginDto;
import com.shop.api.dto.RegisterDto;
import com.shop.api.exception.BadRequestException;
import com.shop.api.exception.UnauthorizedException;
import com.shop.api.model.User;
import com.shop.api.repository.UserRepository;
import com.shop.api.service.AuthService;
import com.shop.api.service.JwtService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {
    final UserRepository userRepository;
    final JwtService jwtService;
    final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String register(RegisterDto dto) {
        if(userRepository.existsByEmail(dto.getEmail()))
            throw new BadRequestException("Пользователь с таким email уже существует");
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return jwtService.generateToken(user.getEmail());
    }

    @Override
    public String login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Неверный email или пароль"));
        if(user.getPasswordHash() == null)
            throw new UnauthorizedException("Авторизуйтесь через учетную запись Google");
        if(!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash()))
            throw new UnauthorizedException("Неверный email или пароль");
        return jwtService.generateToken(user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePassDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Указанный email не найден"));
        if(user.getPasswordHash() == null)
            throw new UnauthorizedException("Авторизуйтесь через учетную запись Google");
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Указанный email не найден"));
        if(user.getPasswordHash() == null)
            throw new BadRequestException("Авторизуйтесь через учетную запись Google");
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(idx));
        }
        String newPassword = new String(sb);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        System.out.println(newPassword);
    }
}
