package com.tictactoe.domain.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
USER;

@Override
public String getAuthority() {
    return "ROLE_" + this.name();
}
}
