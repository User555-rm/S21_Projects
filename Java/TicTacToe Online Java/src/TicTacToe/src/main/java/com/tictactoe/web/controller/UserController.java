package com.tictactoe.web.controller;

import com.tictactoe.domain.model.User;
import com.tictactoe.domain.service.UserService;
import com.tictactoe.web.model.ErrorResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
private final UserService userService;

public UserController(UserService userService) {
    this.userService = userService;
}

@GetMapping("/{id}")
public ResponseEntity<?> findUser(@PathVariable("id") UUID userId) {
User user = userService.getUserById(userId);
if (user == null) {
ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "USER_NOT_FOUND");
return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}

String login = user.getLogin();
Map<String, String> response = new HashMap<>();
response.put("userId", userId.toString());
response.put("login", login);
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
}

}
