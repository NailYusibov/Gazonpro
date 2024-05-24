package com.gitlab.exception.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BusinessException(HttpStatus httpStatus) {
        super("Http status:" + httpStatus.value());
        this.httpStatus = httpStatus;
    }
}
