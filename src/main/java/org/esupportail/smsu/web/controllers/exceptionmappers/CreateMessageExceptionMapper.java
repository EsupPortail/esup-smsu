package org.esupportail.smsu.web.controllers.exceptionmappers;

import org.apache.log4j.Logger;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.Helper;
import org.esupportail.smsu.web.controllers.ServicesSmsuController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = ServicesSmsuController.class)
public class CreateMessageExceptionMapper {
	
    private final Logger logger = Logger.getLogger(getClass());
    
    @ExceptionHandler(CreateMessageException.class)
    public ResponseEntity<?> toResponse(CreateMessageException ex) {
    	logger.error("Handle Exception in CreateMessageExceptionMapper : " + ex.getMessage(), ex);
        return Helper.jsonErrorResponse(HttpStatus.BAD_REQUEST, ex.toString());
    }
}