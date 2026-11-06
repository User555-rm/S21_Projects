package com.tictactoe.domain.service;

import com.tictactoe.domain.model.JwtAuthentication;
import com.tictactoe.domain.model.Role;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
public JwtAuthentication buildJwtAuthentication(Claims claims) {
JwtAuthentication auth = new JwtAuthentication();
auth.setUserId(UUID.fromString(claims.getSubject()));
/*
Почему нельзя просто сделать так:
List<Role> roles = claims.get("roles", List.class);
Ответ:
Проблема: дженерики стираются. `claims.get("roles", List.class)` вернёт
`List<String>` (строки "USER"), а не `List<Role>`.
Когда `JwtAuthentication.getAuthorities()` попытается вернуть этот список
как `Collection<? extends GrantedAuthority>`, Java ожидает объекты `Role`,
а там строки — будет `ClassCastException` при первом обращении к авторизации.
 */
List<String> roleStrings = claims.get("roles", List.class);
List<Role> roles = roleStrings.stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toList());
auth.setRoles(roles);
auth.setAuthenticated(true);
return auth;
}


}
