package com.tictactoe.domain.service;

import com.tictactoe.domain.model.JwtAuthentication;
import com.tictactoe.web.model.JwtRequest;
import com.tictactoe.web.model.JwtResponse;
import com.tictactoe.web.model.SignUpRequest;

import java.util.UUID;

public interface AuthService {
boolean register(SignUpRequest signUpRequest);
JwtResponse login(JwtRequest jwtRequest);
JwtResponse refreshAccessToken(String refreshToken);
JwtResponse refreshRefreshToken(String refreshtoken);
JwtAuthentication getJwtAuthentication();
}
