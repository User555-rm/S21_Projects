package com.tictactoe.datasource.entity;

import com.tictactoe.domain.model.GameStatus;
import com.tictactoe.domain.model.GameType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "games")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameEntity {

    @Id
    private UUID id;
    @Column
    private String boardState;
    @Column
    @Enumerated(EnumType.STRING)
    private GameStatus status;
    @Column(name = "player_x_id")
    private UUID playerXId;
    @Column(name = "player_o_id")
    private UUID playerOId;
    @Column
    @Enumerated(EnumType.STRING)
    private GameType gameType;
    @Column
    private LocalDateTime createdDate;

}
