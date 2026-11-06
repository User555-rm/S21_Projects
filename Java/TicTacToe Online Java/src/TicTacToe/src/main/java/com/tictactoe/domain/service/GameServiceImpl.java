package com.tictactoe.domain.service;

import com.tictactoe.datasource.entity.GameEntity;
import com.tictactoe.datasource.mapper.GameMapper;
import com.tictactoe.datasource.repository.GameRepository;
import com.tictactoe.domain.model.*;
import com.tictactoe.web.mapper.WebToDomainMapper;
import com.tictactoe.web.model.GameWebModel;
import com.tictactoe.web.model.LeaderboardEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tictactoe.domain.model.GameStatus.*;

@Service
public class GameServiceImpl implements GameService {
    public static final int N = 3;
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final UserService userService;

    public GameServiceImpl(GameRepository gameRepository, GameMapper gameMapper,
                           UserService userService) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.userService = userService;
    }

    @Override
    public CurrentGame createGame(UUID userXId, GameType gameType) {
        CurrentGame game;
    if(gameType == GameType.VS_AI) {
        game = new CurrentGame(userXId); //создаем PvE
    } else {
        game = new CurrentGame(userXId, gameType); //создаем PvP
    }
    GameEntity savedGame = gameMapper.toGameEntity(game);
    gameRepository.save(savedGame);

    savedGame = gameRepository.findById(game.getId()).orElse(null);
    if(savedGame == null) throw new GameServiceException(404, "GAME_NOT_FOUND");
    game = gameMapper.toCurrentGame(savedGame);
    return game;
    }

    @Override
    public CurrentGame joinGame(UUID gameId, UUID userOId) {
    CurrentGame game = gameMapper.toCurrentGame(gameRepository.findById(gameId).orElse(null));
    if(game == null) throw new GameServiceException(404, "GAME_NOT_FOUND");
    if(game.getStatus() == WAITING_PLAYERS) {
        CurrentGame newGame = new CurrentGame(game.getId(), game.getBoard(), PLAYER_X_TURN, game.getPlayerXId(),
                userOId, game.getGameType(), game.getCreatedDate());
        GameEntity savedNewGame = gameMapper.toGameEntity(newGame);
        gameRepository.save(savedNewGame);
        return newGame;
    } else {
        return null;
    }
    }

    @Override
    public CurrentGame makeTurn(UUID gameId, UUID userId, Board board) {

    CurrentGame game = getGameById(gameId);
    if(game == null)
        throw  new GameServiceException(404, "GAME_NOT_FOUND");

    GameStatus status = game.getStatus();
    if(status != PLAYER_X_TURN && status != PLAYER_O_TURN)
        throw new GameServiceException(400, "GAME_FINISHED");

    if(status == PLAYER_X_TURN && !userId.equals(game.getPlayerXId()))
        throw new GameServiceException(403, "NOT_YOUR_TURN");

    if(status == PLAYER_O_TURN && !userId.equals(game.getPlayerOId()))
        throw new GameServiceException(403, "NOT_YOUR_TURN");

    if(false == checkBoardValidation(game, board))
        throw new GameServiceException(400, "INVALID_MOVE");

    GameStatus currentTurn = game.getStatus();

    status = checkGameOver(board);
    game = new CurrentGame(game.getId(), board, status, game.getPlayerXId(),
                           game.getPlayerOId(), game.getGameType(), game.getCreatedDate());

    if(status == PLAYER_X_WIN || status == PLAYER_O_WIN || status == DRAW) {
         GameEntity savedGame = gameMapper.toGameEntity(game);
         gameRepository.save(savedGame);
         return game;
    }

    if(game.getGameType() == GameType.VS_AI) {
        game = getNextTurnByMinimax(game);
        GameEntity savedGame = gameMapper.toGameEntity(game);
        gameRepository.save(savedGame);
        return game;
    } else {
        GameStatus nextStatus = (currentTurn == PLAYER_X_TURN) ? PLAYER_O_TURN : PLAYER_X_TURN;
        game = new CurrentGame(game.getId(), game.getBoard(), nextStatus, game.getPlayerXId(),
                               game.getPlayerOId(), game.getGameType(), game.getCreatedDate());
        GameEntity savedGame = gameMapper.toGameEntity(game);
        gameRepository.save(savedGame);
        return game;
    }

    }

    @Override
    public List<GameWebModel> getAvailableGames() {
    List<GameEntity> games = gameRepository.findByStatus(WAITING_PLAYERS);
    List<GameWebModel> webModels = new ArrayList<>();
    for(GameEntity g: games) {
        CurrentGame game = gameMapper.toCurrentGame(g);
        if(game != null) {
            webModels.add(WebToDomainMapper.toWeb(game));
        }
    }
    return webModels;
    }

    @Override
    public CurrentGame getGameById(UUID gameId) {
    GameEntity savedGame = gameRepository.findById(gameId).orElse(null);
    return gameMapper.toCurrentGame(savedGame);
    }

    @Override
    public CurrentGame getNextTurnByMinimax(CurrentGame game) {
        GameStatus status = checkGameOver(game.getBoard());
        if(status == PLAYER_X_WIN || status == PLAYER_O_WIN || status == DRAW) return game;

        int bestScore = Integer.MIN_VALUE;
        int[] bestTurn = new int[]{-1, -1};

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(game.getBoard().getCell(i, j) == 0) {

                    Board copyBoard = game.getBoard().copy();
                    copyBoard.setCell(i, j, 2);
                    int score = minimax(copyBoard, 1);
                    if(score > bestScore) {
                        bestScore = score;
                        bestTurn[0] = i;
                        bestTurn[1] = j;
                    }
                }
            }
        }

        if (bestTurn[0] == -1) return game;

        Board newBoard = game.getBoard().copy();
        newBoard.setCell(bestTurn[0], bestTurn[1], 2);
        status = checkGameOver(newBoard);
        return new CurrentGame(game.getId(), newBoard, status, game.getPlayerXId(),
                game.getPlayerOId(), game.getGameType(), game.getCreatedDate());
    }

    private int minimax(Board board, int currentPlayer) {
        GameStatus status = checkGameOver(board);
        if(status == PLAYER_O_WIN) return 10;
        if(status == PLAYER_X_WIN) return -10;
        if(status == DRAW) return 0;

        if(currentPlayer == 2) {
            int bestScore = Integer.MIN_VALUE;
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(board.getCell(i, j) == 0) {
                        Board copyBoard = board.copy();
                        copyBoard.setCell(i, j, 2);
                        int score = minimax(copyBoard, 1);
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(board.getCell(i, j) == 0) {
                        Board copyBoard = board.copy();
                        copyBoard.setCell(i, j, 1);
                        int score = minimax(copyBoard, 2);
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }



    @Override
    public boolean checkBoardValidation(CurrentGame game, Board board) {
        if(game == null || board == null) return false;

        Board prevBoard = game.getBoard();

        int diff_count = 0;
        int[] diff_coord = new int[2];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(board.getCell(i, j) != prevBoard.getCell(i, j)) {
                    diff_count++;
                    if(diff_count == 1) {
                        diff_coord[0] = i;
                        diff_coord[1] = j;
                    }
                }
            }
        }

        if(diff_count == 0) {
            return false;
        }
        if(diff_count > 1) {
            return false;
        }

        if(prevBoard.getCell(diff_coord[0], diff_coord[1]) != 0) {
            return false;
        }

        GameStatus status = game.getStatus();
        if(status == PLAYER_X_TURN && board.getCell(diff_coord[0], diff_coord[1]) != 1) {
            return false;
        }

        if(status == PLAYER_O_TURN && board.getCell(diff_coord[0], diff_coord[1]) != 2) {
            return false;
        }

        return true;
    }

    @Override
    public GameStatus checkGameOver(Board board) {

        for (int i = 0; i < N; i++) {
        if (board.getCell(i, 0) == 1
        && board.getCell(i, 1) == 1
        && board.getCell(i, 2) == 1
        ) return PLAYER_X_WIN;

        if (board.getCell(0, i) == 1
        && board.getCell(1, i) == 1
        && board.getCell(2, i) == 1
        ) return PLAYER_X_WIN;
        }

        if (board.getCell(0, 0) == 1
        && board.getCell(1, 1) == 1
        && board.getCell(2, 2) == 1
        ) return PLAYER_X_WIN;

        if (board.getCell(2, 0) == 1
        && board.getCell(1, 1) == 1
        && board.getCell(0, 2) == 1
        ) return PLAYER_X_WIN;


        for (int i = 0; i < N; i++) {
        if (board.getCell(i, 0) == 2
            && board.getCell(i, 1) == 2
            && board.getCell(i, 2) == 2
            ) return PLAYER_O_WIN;

            if (board.getCell(0, i) == 2
            && board.getCell(1, i) == 2
            && board.getCell(2, i) == 2
            ) return PLAYER_O_WIN;
        }

        if (board.getCell(0, 0) == 2
        && board.getCell(1, 1) == 2
        && board.getCell(2, 2) == 2
        ) return PLAYER_O_WIN;

        if (board.getCell(2, 0) == 2
        && board.getCell(1, 1) == 2
        && board.getCell(0, 2) == 2
        ) return PLAYER_O_WIN;

        for(int i = 0; i < N; i++)
        for(int j = 0; j < N; j++)
        if(board.getCell(i, j) == 0)
        return PLAYER_X_TURN;

        return DRAW;
    }

    @Override
    public List<GameWebModel> getFinishedGamesByUserId(UUID userId) {
    if(userId == null) return null;
    List<GameEntity> gameEntities = gameRepository.findFinishedGamesByUserId(userId);
    if(gameEntities == null) return null;
    List<GameWebModel> gameWebModels = new ArrayList<>();
    for(GameEntity gameEntity: gameEntities) {
    CurrentGame game = gameMapper.toCurrentGame(gameEntity);
    GameWebModel gameWebModel = WebToDomainMapper.toWeb(game);
    gameWebModels.add(gameWebModel);
    }
    return gameWebModels;
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(int topCount) {
    if(topCount <= 0) return null;
    List<Object[]> topList = gameRepository.findTopPlayers(topCount);
    if(topList == null) return null;
    List<LeaderboardEntry> result = new ArrayList<>();
    for(Object[] arr: topList) {
        User user = userService.getUserById((UUID) arr[0]);
        LeaderboardEntry entry = new LeaderboardEntry((UUID) arr[0],
                                user.getLogin(), (double) arr[1]);
        result.add(entry);
    }
    return result;
    }
}
