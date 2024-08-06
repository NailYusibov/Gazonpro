package com.gitlab.exception.handler;

import org.springframework.http.HttpStatus;

public class UserNotAuthenticatedException extends BusinessException {

    public UserNotAuthenticatedException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
