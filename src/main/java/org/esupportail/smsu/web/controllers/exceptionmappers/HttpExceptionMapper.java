package org.esupportail.smsu.web.controllers.exceptionmappers;

import org.esupportail.smsu.web.Helper;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HttpExceptionMapper {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> toResponse(HttpException ex) {
        return Helper.jsonErrorResponse(HttpStatus.BAD_REQUEST, ex.getClass().getSimpleName(), ex.toString());
    }
}