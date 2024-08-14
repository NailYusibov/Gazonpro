package com.gitlab.exception.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class TechnicalException extends RuntimeException {

    private final HttpStatus httpStatus;

    public TechnicalException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
