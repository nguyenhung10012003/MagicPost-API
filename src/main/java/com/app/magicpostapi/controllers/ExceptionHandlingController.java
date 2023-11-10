package com.app.magicpostapi.controllers;

import com.app.magicpostapi.models.ErrorObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.InvalidParameterException;

@RestControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        ex.printStackTrace(System.out);
        return new ResponseEntity<>(new ErrorObject(
                HttpStatus.CONFLICT,
                "Data integrity violation"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        e.printStackTrace(System.out);
        return new ResponseEntity<>(new ErrorObject(
                HttpStatus.NOT_FOUND,
                e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidParameterException.class)
    ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException e, WebRequest request) {
        e.printStackTrace(System.out);
        return new ResponseEntity<>(new ErrorObject(
                HttpStatus.UNPROCESSABLE_ENTITY,
                e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
