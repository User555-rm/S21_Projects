package com.tictactoe.datasource.repository;

import com.tictactoe.datasource.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, UUID> {
Optional<RefreshTokenEntity> findByToken(String token);
void deleteByUserId(UUID userId);
}
