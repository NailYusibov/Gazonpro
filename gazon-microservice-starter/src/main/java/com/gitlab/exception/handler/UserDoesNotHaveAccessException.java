package com.gitlab.exception.handler;

import org.springframework.http.HttpStatus;

public class UserDoesNotHaveAccessException extends BusinessException {
    public UserDoesNotHaveAccessException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}