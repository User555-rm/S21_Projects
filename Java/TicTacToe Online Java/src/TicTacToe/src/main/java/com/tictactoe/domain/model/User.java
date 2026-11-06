package com.tictactoe.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
private UUID id;
private String login;
private String password;
private List<Role> roles = new ArrayList<>(List.of(Role.USER));
}
