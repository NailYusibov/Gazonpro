package com.gitlab.exception.handler;

import org.springframework.http.HttpStatus;

public class WrongSelectedProductsException extends BusinessException {

    public WrongSelectedProductsException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
