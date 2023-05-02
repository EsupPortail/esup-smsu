package org.esupportail.smsu.web.controllers.exceptionmappers;

import org.esupportail.smsu.web.Helper;
import org.esupportail.smsu.web.controllers.ServicesSmsuController;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = ServicesSmsuController.class)
public class HttpExceptionMapper {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> toResponse(HttpException ex) {
        return Helper.jsonErrorResponse(HttpStatus.BAD_REQUEST, ex.getClass().getSimpleName(), ex.toString());
    }
}