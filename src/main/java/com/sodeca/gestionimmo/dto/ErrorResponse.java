package com.sodeca.gestionimmo.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String message;
    private final int statusCode;
    private final String errorCode;
    private final String timestamp;

    public ErrorResponse(String message, int statusCode, String errorCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getTimestamp() {
        return timestamp;
    }
}