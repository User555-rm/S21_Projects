package com.tictactoe.domain.service;

import com.tictactoe.datasource.entity.RefreshTokenEntity;
import com.tictactoe.datasource.repository.RefreshTokenRepository;
import com.tictactoe.domain.model.GameServiceException;
import com.tictactoe.domain.model.JwtAuthentication;
import com.tictactoe.domain.model.User;
import com.tictactoe.web.model.JwtRequest;
import com.tictactoe.web.model.JwtResponse;
import com.tictactoe.web.model.SignUpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
private final UserService userService;
private final JwtProvider jwtProvider;
private final RefreshTokenRepository repository;

public AuthServiceImpl(UserService userService, JwtProvider jwtProvider,
                       RefreshTokenRepository repository) {
    this.userService = userService;
    this.jwtProvider = jwtProvider;
    this.repository = repository;
}

@Override
public boolean register(SignUpRequest signUpRequest) {
String login = signUpRequest.getLogin();
String password = signUpRequest.getPassword();
if(login == null || password == null) return false;
return userService.registerUser(login, password);
}

/*Здесь refreshToken не меняется, только accessToken. Это удобно,
если у пользователя истёк accessToken, но refreshToken ещё жив — он
не хочет каждый раз получать новый refreshToken. */
@Override
public JwtResponse login(JwtRequest jwtRequest) {
LocalDateTime time = LocalDateTime.now().plus(Duration.ofMillis(jwtProvider.getRefreshTokenTime()));
UUID userId = userService.authenticate(jwtRequest.getLogin(), jwtRequest.getPassword());
if(userId == null) return null;
User user = userService.getUserById(userId);
String accessToken = jwtProvider.generateAccessToken(user);
String refreshToken = jwtProvider.generateRefreshToken(user);
RefreshTokenEntity tokenEntity = new RefreshTokenEntity(UUID.randomUUID(), userId,
                    refreshToken, time);
repository.save(tokenEntity);
return new JwtResponse(accessToken, refreshToken);
}

@Override
public JwtResponse refreshAccessToken(String refreshToken) {
RefreshTokenEntity tokenEntity = repository.findByToken(refreshToken).orElse(null);
if(tokenEntity == null) {
    throw new GameServiceException(401, "AUTHORIZATION_FAILED");
} else if(tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
    repository.delete(tokenEntity);
    throw new GameServiceException(401, "AUTHORIZATION_FAILED");
}

UUID userId = tokenEntity.getUserId();
User user = userService.getUserById(userId);
String accessToken = jwtProvider.generateAccessToken(user);
return new JwtResponse(accessToken, refreshToken);
}

/* Если refreshToken украли, после того как владелец использовал его для ротации,
токен в БД удаляется — вор не сможет его переиспользовать. Это безопаснее,
чем оставлять старый refreshToken жить. */
@Override
public JwtResponse refreshRefreshToken(String refreshToken) {
RefreshTokenEntity tokenEntity = repository.findByToken(refreshToken).orElse(null);
if(tokenEntity == null) {
    throw new GameServiceException(401, "AUTHORIZATION_FAILED");
} else if(tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
    repository.delete(tokenEntity);
    throw new GameServiceException(401, "AUTHORIZATION_FAILED");
    }

UUID userId = tokenEntity.getUserId();
User user = userService.getUserById(userId);
repository.delete(tokenEntity);
String newAccessToken = jwtProvider.generateAccessToken(user);
String newRefreshToken = jwtProvider.generateRefreshToken(user);
repository.save(new RefreshTokenEntity(UUID.randomUUID(), userId, newRefreshToken,
        LocalDateTime.now().plus(Duration.ofMillis(jwtProvider.getRefreshTokenTime()))));
return new JwtResponse(newAccessToken, newRefreshToken);
}

/* Логика работы метода:
1. Получить Authentication из SecurityContextHolder.getContext().getAuthentication()
2. Если authentication == null -> вернуть null
3. Если authentication не является JwtAuthentication -> вернуть null
4. Если является -> привести к JwtAuthentication и вернуть. */
@Override
public JwtAuthentication getJwtAuthentication() {
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
if(auth == null) return null;
if(auth instanceof JwtAuthentication) return (JwtAuthentication) auth;
else return null;
}

}
