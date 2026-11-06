package com.tictactoe.datasource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenEntity {
@Id
private UUID id;
@Column(nullable = false)
private UUID userId;
@Column(nullable = false, unique = true)
private String token;
@Column(nullable = false)
private LocalDateTime expiresAt;
}
