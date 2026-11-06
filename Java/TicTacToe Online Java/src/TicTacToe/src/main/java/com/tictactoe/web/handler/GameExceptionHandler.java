package com.tictactoe.web.handler;

import com.tictactoe.domain.model.GameServiceException;
import com.tictactoe.web.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GameExceptionHandler {

@ExceptionHandler(GameServiceException.class)
public ResponseEntity<ErrorResponse> handleGameService(GameServiceException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorMessage(),
            e.getHttpStatus(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getHttpStatus()));
}
}
