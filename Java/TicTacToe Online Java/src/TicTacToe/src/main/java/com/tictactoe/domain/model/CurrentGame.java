package com.tictactoe.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CurrentGame {
    private final UUID id;
    private final Board board;
    private final GameStatus status;
    private final UUID playerXId;
    private final UUID playerOId;
    private final GameType gameType;
    private final LocalDateTime createdDate;

    //PvE конструктор
    public CurrentGame(UUID playerXId) {
        this.id = UUID.randomUUID();
        this.board = new Board();
        this.status = GameStatus.PLAYER_X_TURN;
        this.playerXId = playerXId;
        this.playerOId = null;
        this.gameType = GameType.VS_AI;
        this.createdDate = LocalDateTime.now();
    }

    //PvP конструктор (создает Игрок Х)
    public CurrentGame(UUID playerXId, GameType gameType) {
        this.id = UUID.randomUUID();
        this.board = new Board();
        this.status = GameStatus.WAITING_PLAYERS;
        this.playerXId = playerXId;
        this.playerOId = null;
        this.gameType = gameType;
        this.createdDate = LocalDateTime.now();
    }

    //PvP конструктор полный
    public CurrentGame(UUID id, Board board, GameStatus status, UUID playerXId,
                       UUID playerOId, GameType gameType, LocalDateTime createdDate) {
        this.id = id;
        this.board = new Board(board);
        this.status = status;
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.gameType = gameType;
        this.createdDate = createdDate;
    }

    public Board getBoard() {
        return new Board(board);
    }
}
