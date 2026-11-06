package com.tictactoe.web.controller;

import com.tictactoe.domain.model.GameServiceException;
import com.tictactoe.domain.model.JwtAuthentication;
import com.tictactoe.domain.model.User;
import com.tictactoe.domain.service.AuthService;
import com.tictactoe.domain.service.UserService;
import com.tictactoe.web.model.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
private final AuthService authService;
private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
        this.userService = userService;
    }

@PostMapping("/register")
public ResponseEntity<?> registerNewUser(@RequestBody SignUpRequest signUpRequest) {
boolean result = authService.register(signUpRequest);
if(result == true) {
    Map<String, String> registerResponse = new HashMap<>();
    registerResponse.put("login", signUpRequest.getLogin().toString());
    registerResponse.put("USER", "CREATED");
    return new ResponseEntity<>(registerResponse, HttpStatusCode.valueOf(201));
} else {
    ErrorResponse errorResponse = new ErrorResponse("TRY_ANOTHER_LOGIN", 409, "LOGIN_ALREADY_EXISTS");
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(409));
}
}

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody JwtRequest request) {
    JwtResponse response = authService.login(request);
    if(response == null) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_AUTHORIZED", 401, "WRONG_LOGIN_OR_PASSWORD");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(401));
    }
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
}

@PostMapping("/refresh-access")
public ResponseEntity<?> refreshAccess(@RequestBody RefreshJwtRequest request) {
try {
JwtResponse response = authService.refreshAccessToken(request.getRefreshToken());
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
} catch (GameServiceException e) {
    ErrorResponse errorResponse = new ErrorResponse("NOT_AUTHORIZED", 401, "AUTHORIZATION_FAILED");
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(401));
    }
}

@PostMapping("/refresh-refresh")
public ResponseEntity<?> refreshRefresh(@RequestBody RefreshJwtRequest request) {
try {
JwtResponse response = authService.refreshRefreshToken(request.getRefreshToken());
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
} catch (GameServiceException e) {
    ErrorResponse errorResponse = new ErrorResponse("NOT_AUTHORIZED", 401, "AUTHORIZATION_FAILED");
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(401));
    }
}

@GetMapping("/info")
public ResponseEntity<?> getUserInfo() {
JwtAuthentication auth = authService.getJwtAuthentication();
if(auth == null) {
    ErrorResponse errorResponse = new ErrorResponse("NOT_AUTHORIZED", 401, "AUTHENTIFICATION_FAILED");
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(401));
    }
UUID userId = auth.getUserId();
User user = userService.getUserById(userId);
if(user == null) {
    ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", 404, "USER_NOT_FOUND");
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }
Map<String, String> response = new HashMap<>();
response.put("userId", userId.toString());
response.put("login", user.getLogin());
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
}

}
