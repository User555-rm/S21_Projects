package com.tictactoe.web.model;

import java.time.LocalDateTime;

public class ErrorResponse {
private final String message;
private final String timestamp;
private final int statusCode;
private final String error;

public ErrorResponse(String message, int statusCode, String error) {
    this.message = message;
    this.timestamp = LocalDateTime.now().toString();
    this.statusCode = statusCode;
    this.error = error;
}

public String getMessage() { return message; }
public String getTimestamp() { return timestamp; }
public int getStatusCode() { return statusCode; }
public String getError() { return error; }

}
