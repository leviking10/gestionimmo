package com.sodeca.gestionimmo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Exception personnalisée pour gérer les erreurs métier.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final LocalDateTime timestamp;

    public BusinessException(String message) {
        this(message, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, HttpStatus status) {
        this(message, "BUSINESS_ERROR", status);
    }

    public BusinessException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public BusinessException(String message, String errorCode, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("BusinessException{message='%s', status=%s, errorCode='%s', timestamp=%s}",
                getMessage(), status, errorCode, timestamp);
    }
}
