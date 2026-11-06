package com.tictactoe.web.mapper;

import com.tictactoe.domain.model.Board;
import com.tictactoe.domain.model.CurrentGame;
import com.tictactoe.web.model.GameWebModel;

public class WebToDomainMapper {
private static final int N = 3;

private WebToDomainMapper() {}

public static CurrentGame toDomain(GameWebModel gameWebModel) {
    if(gameWebModel == null) return null;
    return new CurrentGame(gameWebModel.getId(), new Board(gameWebModel.getField()),
    gameWebModel.getStatus(), gameWebModel.getPlayerXId(), gameWebModel.getPlayerOId(),
    gameWebModel.getGameType(), gameWebModel.getCreatedDate());
}

public static GameWebModel toWeb(CurrentGame game) {
    if(game == null) return null;
    int[][] field = new int[N][N];
    for(int i = 0; i < N; i++)
        for(int j = 0; j < N; j++)
            field[i][j] = game.getBoard().getCell(i, j);
    return new GameWebModel(game.getId(), field, game.getStatus(), game.getPlayerXId(),
                            game.getPlayerOId(), game.getGameType(), game.getCreatedDate());
}
}
