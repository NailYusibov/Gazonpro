package com.gitlab.exception.handler;

import org.springframework.http.HttpStatus;

public class EmptySearchStringException extends BusinessException {

    private static final String MESSAGE = "Empty search string";

    public EmptySearchStringException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
