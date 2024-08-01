package com.gitlab.exception.handler;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleSearchException(BusinessException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getHttpStatus().value(), e.getMessage()), e.getHttpStatus());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ErrorResponseDto handleConstraintViolationException(ConstraintViolationException ex) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ErrorResponseDto handleForbiddenException(ForbiddenException ex) {
        return new ErrorResponseDto(ex.getHttpStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(value = UserNotAuthenticatedException.class)
    public ErrorResponseDto handleUserNotAuthenticatedException(UserNotAuthenticatedException ex) {
        return new ErrorResponseDto(ex.getHttpStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(value = UserDoesNotHaveAccessException.class)
    public ErrorResponseDto handleUserDoesNotHaveAccessException(UserDoesNotHaveAccessException ex) {
        return new ErrorResponseDto(ex.getHttpStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(value = WrongSelectedProductsException.class)
    public ErrorResponseDto handleWrongSelectedProductsException(WrongSelectedProductsException ex) {
        return new ErrorResponseDto(ex.getHttpStatus().value(), ex.getMessage());
    }
}