package com.tictactoe.domain.service;

import com.tictactoe.datasource.entity.UserEntity;
import com.tictactoe.datasource.mapper.UserMapper;
import com.tictactoe.datasource.repository.UserRepository;
import com.tictactoe.domain.model.Role;
import com.tictactoe.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
private final UserRepository userRepository;
private final UserMapper userMapper;

public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
}

@Override
public boolean registerUser(String login, String password) {
if(login == null || password == null) return false;
UserEntity existingUser = userRepository.findByLogin(login).orElse(null);
if(existingUser != null) return false;
UUID userId = UUID.randomUUID();
User user = new User(userId, login, password, new ArrayList<>(List.of(Role.USER)));
UserEntity newUser = userMapper.toUserEntity(user);
userRepository.save(newUser);
return true;
}

@Override
public UUID authenticate(String login, String password){
if(login == null || password == null) return null;
UserEntity existingUser = userRepository.findByLogin(login).orElse(null);
if(existingUser == null) return null;
String existPassword = existingUser.getPassword();
if(!password.equals(existPassword)) return null;
UUID userId = existingUser.getId();
return userId;
}

@Override
public User getUserById(UUID id){
if(id == null) return null;
UserEntity existingUser = userRepository.findById(id).orElse(null);
if(existingUser == null) return null;
User user = userMapper.toUser(existingUser);
return user;
}
}
