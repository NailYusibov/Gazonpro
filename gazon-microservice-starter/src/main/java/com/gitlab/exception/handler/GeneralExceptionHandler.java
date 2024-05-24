package com.gitlab.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}