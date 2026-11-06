package com.tictactoe.domain.service;

import com.tictactoe.domain.model.User;
import java.util.UUID;

public interface UserService {
boolean registerUser(String login, String password);
UUID authenticate(String login, String password);
User getUserById(UUID id);
}