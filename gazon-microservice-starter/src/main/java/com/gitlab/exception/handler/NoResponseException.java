package com.gitlab.exception.handler;

import org.springframework.http.HttpStatus;

public class NoResponseException extends TechnicalException {

    public NoResponseException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
