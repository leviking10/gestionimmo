package com.sodeca.gestionimmo.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {

    private final HttpStatus status;

    public NotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

    public HttpStatus getStatus() {
        return status;
    }
}