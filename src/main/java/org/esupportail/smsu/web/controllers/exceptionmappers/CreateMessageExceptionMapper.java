package org.esupportail.smsu.web.controllers.exceptionmappers;

import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CreateMessageExceptionMapper {
    @ExceptionHandler(CreateMessageException.class)
    public ResponseEntity<?> toResponse(Exception ex) {
        return Helper.jsonErrorResponse(HttpStatus.BAD_REQUEST, ex.toString());
    }
}