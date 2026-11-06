package com.tictactoe.web.model;

import com.tictactoe.domain.model.GameStatus;
import com.tictactoe.domain.model.GameType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GameWebModel {
    private static final int N = 3;
    private UUID id;
    private int[][] field;
    private GameStatus status;
    private UUID playerXId;
    private UUID playerOId;
    private GameType gameType;
    private LocalDateTime createdDate;

    public GameWebModel(UUID id, int[][] field, GameStatus status, UUID playerXId,
                        UUID playerOId, GameType gameType, LocalDateTime createdDate) {
        this.id = id;
        this.field = new int[N][N];
        for (int i = 0; i < N; i++) {
            this.field[i] = Arrays.copyOf(field[i], N);
        }
        this.status = status;
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.gameType = gameType;
        this.createdDate = createdDate;
    }

    public int[][] getField() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            copy[i] = Arrays.copyOf(field[i], N);
        }
        return copy;
    }

    public void setField(int[][] field) {
        this.field = new int[N][N];
        for (int i = 0; i < N; i++) {
            this.field[i] = Arrays.copyOf(field[i], N);
        }
    }

}
