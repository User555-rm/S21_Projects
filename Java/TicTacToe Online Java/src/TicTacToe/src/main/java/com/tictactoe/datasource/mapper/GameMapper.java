package com.tictactoe.datasource.mapper;

import com.tictactoe.datasource.entity.GameEntity;
import com.tictactoe.domain.model.Board;
import com.tictactoe.domain.model.CurrentGame;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {
private final int N = 3;

public CurrentGame toCurrentGame(GameEntity game) {
    if(game == null) return null;
    Board board = new Board();
    int k = 0;
    for(int i = 0; i < N; i++)
        for(int j = 0; j < N; j++) {
            board.setCell(i, j, Character.getNumericValue(game.getBoardState().charAt(k)));
            k++;
        }
return new CurrentGame(game.getId(), board, game.getStatus(), game.getPlayerXId(),
        game.getPlayerOId(), game.getGameType(), game.getCreatedDate());
}

public GameEntity toGameEntity(CurrentGame game) {
    if(game == null) return null;
    String board = "";
    for(int i = 0; i < N; i++)
        for(int j = 0; j < N; j++) {
            board = board + game.getBoard().getCell(i, j);
        }
    return new GameEntity(game.getId(), board, game.getStatus(), game.getPlayerXId(),
            game.getPlayerOId(), game.getGameType(), game.getCreatedDate());
}

}
