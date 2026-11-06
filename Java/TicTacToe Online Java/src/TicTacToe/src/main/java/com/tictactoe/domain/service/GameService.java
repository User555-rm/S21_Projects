package com.tictactoe.domain.service;

import com.tictactoe.domain.model.Board;
import com.tictactoe.domain.model.CurrentGame;
import com.tictactoe.domain.model.GameStatus;
import com.tictactoe.domain.model.GameType;
import com.tictactoe.web.model.GameWebModel;
import com.tictactoe.web.model.LeaderboardEntry;

import java.util.List;
import java.util.UUID;

public interface GameService {
    CurrentGame getNextTurnByMinimax(CurrentGame game);
    boolean checkBoardValidation(CurrentGame game, Board prevBoard);
    GameStatus checkGameOver(Board board);
    CurrentGame createGame(UUID userXId, GameType gameType);
    CurrentGame joinGame(UUID gameId, UUID userOId);
    CurrentGame makeTurn(UUID gameId, UUID userId, Board board);
    List<GameWebModel> getAvailableGames();
    CurrentGame getGameById(UUID gameId);
    List<GameWebModel> getFinishedGamesByUserId(UUID userId);
    List<LeaderboardEntry> getTopPlayers(int topCount);
}