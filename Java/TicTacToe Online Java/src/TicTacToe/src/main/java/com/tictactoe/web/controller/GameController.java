package com.tictactoe.web.controller;


import com.tictactoe.domain.model.Board;
import com.tictactoe.domain.model.CurrentGame;
import com.tictactoe.domain.model.GameType;
import com.tictactoe.domain.service.GameService;
import com.tictactoe.domain.service.UserService;
import com.tictactoe.web.mapper.WebToDomainMapper;
import com.tictactoe.web.model.ErrorResponse;
import com.tictactoe.web.model.GameWebModel;
import com.tictactoe.web.model.LeaderboardEntry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

private final UserService userService;
private final GameService gameService;

public GameController (UserService userService, GameService gameService) {
    this.userService = userService;
    this.gameService = gameService;
}

@PostMapping("/create")
public ResponseEntity<?> createGame(HttpServletRequest request,
                                               @RequestParam GameType type) {
UUID userId = (UUID) request.getAttribute("userId");
if (userId == null) {
ErrorResponse error = new ErrorResponse("UNAUTHORIZED", 401, "USER_NOT_FOUND");
return new ResponseEntity<>(error, HttpStatusCode.valueOf(401));
}
CurrentGame game = gameService.createGame(userId, type);
GameWebModel response = WebToDomainMapper.toWeb(game);
return new ResponseEntity<>(response, HttpStatusCode.valueOf(201));
}


@GetMapping("/available")
public ResponseEntity<List<GameWebModel>> showAvailableGames() {
 List<GameWebModel> games = gameService.getAvailableGames();
 return new ResponseEntity<>(games, HttpStatusCode.valueOf(200));
}


@PostMapping("/{id}/join")
public ResponseEntity<?> join(HttpServletRequest request, @PathVariable("id")UUID gameId) {
UUID userId = (UUID) request.getAttribute("userId");
if (userId == null) {
ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "USER_NOT_FOUND");
return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}
CurrentGame game = gameService.joinGame(gameId, userId);
if (game == null) {
ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "GAME_NOT_FOUND");
return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}
GameWebModel response = WebToDomainMapper.toWeb(game);
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
}


@GetMapping("/{id}")
public ResponseEntity<?> findGame(@PathVariable("id")UUID gameId) {
CurrentGame game = gameService.getGameById(gameId);
if (game == null) {
ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "GAME_NOT_FOUND");
return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}
GameWebModel response = WebToDomainMapper.toWeb(game);
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
}


@PostMapping("/{id}")
public ResponseEntity<?> makeMove(HttpServletRequest request, @PathVariable("id")UUID gameId,
                                  @RequestBody GameWebModel prevGame) {
UUID userId = (UUID)request.getAttribute("userId");
if (userId == null) {
ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "USER_NOT_FOUND");
return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}
Board board = new Board(prevGame.getField());
CurrentGame game = gameService.makeTurn(gameId, userId, board);

GameWebModel response = WebToDomainMapper.toWeb(game);
return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
}


@GetMapping("/history")
public ResponseEntity<?> getHistory(HttpServletRequest request) {
UUID userId = (UUID)request.getAttribute("userId");
if(userId == null) {
    ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "NOT_FOUND");
    return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}
List<GameWebModel> games = gameService.getFinishedGamesByUserId(userId);
return new ResponseEntity<>(games, HttpStatusCode.valueOf(200));
}

@GetMapping("/top")
public ResponseEntity<?> getTop(@RequestParam("n") int topCount) {
List<LeaderboardEntry> top = gameService.getTopPlayers(topCount);
if(top == null) {
    ErrorResponse error = new ErrorResponse("NOT_FOUND", 404, "NOT_FOUND");
    return new ResponseEntity<>(error, HttpStatusCode.valueOf(404));
}
return new ResponseEntity<>(top, HttpStatusCode.valueOf(200));
}

}