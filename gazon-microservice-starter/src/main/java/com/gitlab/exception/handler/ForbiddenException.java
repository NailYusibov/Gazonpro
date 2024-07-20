package com.gitlab.exception.handler;


import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(HttpStatus httpStatus,String message) {
        super(httpStatus,message);
    }

}
