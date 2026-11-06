package com.tictactoe.datasource.repository;

import com.tictactoe.datasource.entity.GameEntity;
import com.tictactoe.domain.model.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {

    List<GameEntity> findByStatus(GameStatus status);

    @Query("SELECT g FROM GameEntity g WHERE " +
    "(g.playerXId = :userId OR g.playerOId = :userId) AND " +
    "(g.status = 'PLAYER_X_WIN' OR g.status = 'PLAYER_O_WIN' OR g.status = 'DRAW') AND " +
    "g.gameType <> 'VS_AI' " +
    "ORDER BY g.createdDate DESC")
    List<GameEntity> findFinishedGamesByUserId(@Param("userId") UUID userId);

    @Query(value = """
    SELECT user_id, SUM(wins)::float / SUM(total) AS win_ratio FROM (
        SELECT g.player_x_id AS user_id,
               COUNT(*) FILTER (WHERE g.status = 'PLAYER_X_WIN') AS wins,
               COUNT(*) AS total
        FROM games g
        WHERE g.game_type <> 'VS_AI'
          AND (g.status IN ('PLAYER_X_WIN', 'PLAYER_O_WIN', 'DRAW'))
        GROUP BY g.player_x_id
        UNION ALL
        SELECT g.player_o_id AS user_id,
               COUNT(*) FILTER (WHERE g.status = 'PLAYER_O_WIN') AS wins,
               COUNT(*) AS total
        FROM games g
        WHERE g.game_type <> 'VS_AI'
          AND (g.status IN ('PLAYER_X_WIN', 'PLAYER_O_WIN', 'DRAW'))
          AND g.player_o_id IS NOT NULL
        GROUP BY g.player_o_id
    ) stats
    GROUP BY user_id
    ORDER BY win_ratio DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Object[]> findTopPlayers(@Param("limit") int limit);

}