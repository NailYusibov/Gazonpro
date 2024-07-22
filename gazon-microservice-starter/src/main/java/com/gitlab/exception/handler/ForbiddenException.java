package com.gitlab.exception.handler;


import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Forbidden: Access is denied.");
    }

}
