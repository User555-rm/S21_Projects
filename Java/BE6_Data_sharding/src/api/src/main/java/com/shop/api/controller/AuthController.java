package com.shop.api.controller;

import com.shop.api.dto.ChangePassDto;
import com.shop.api.dto.LoginDto;
import com.shop.api.dto.RegisterDto;
import com.shop.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto dto) {
        return new ResponseEntity<>(authService.register(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Вход для зарегистрированных пользователей")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto) {
        return new ResponseEntity<>(authService.login(dto), HttpStatus.OK);
    }

    @Operation(summary = "Смена пароля. Необходимо ввести новый пароль.")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePassDto dto,
                                               @AuthenticationPrincipal
                                               String email) {
        authService.changePassword(email, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Сброс пароля. Новый пароль будет автоматически сгенерирован " +
            "приложением и выведен в консоль")
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody String email) {
        authService.resetPassword(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Вход через Google (OAuth 2.0)",
            description = "Браузерный OAuth2-поток. Открываем в браузере, НЕ через Try it out: " +
                    "после входа браузер вернётся на страницу с JWT (в адресе ?token=JWT).")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Редирект на Google OAuth"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/google")
    public ResponseEntity<Void> googleLogin() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }
}
