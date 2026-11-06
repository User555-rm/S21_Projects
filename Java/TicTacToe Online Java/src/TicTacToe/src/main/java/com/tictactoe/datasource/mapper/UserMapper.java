package com.tictactoe.datasource.mapper;

import com.tictactoe.datasource.entity.UserEntity;
import com.tictactoe.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

public UserEntity toUserEntity(User user) {
return new UserEntity(user.getId(), user.getLogin(),
                      user.getPassword(), user.getRoles());
}

public User toUser(UserEntity user) {
return new User(user.getId(), user.getLogin(),
                user.getPassword(), user.getRoles());
}

}
