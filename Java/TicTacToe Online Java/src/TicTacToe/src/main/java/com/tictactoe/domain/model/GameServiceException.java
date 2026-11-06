package com.tictactoe.domain.model;

import lombok.Getter;

@Getter
public class GameServiceException extends RuntimeException {
private final int httpStatus;
private final String errorMessage;

public GameServiceException(int httpStatus, String errorMessage) {
    super(errorMessage); //сообщение = код ошибки
    this.httpStatus = httpStatus;
    this.errorMessage = errorMessage;
    }
}
