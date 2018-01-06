package org.esupportail.smsu.web.ws;

import org.apache.log4j.Logger;

import org.esupportail.smsu.exceptions.SmsuForbiddenException;
import org.esupportail.smsu.web.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class WsSmsuExceptionMapper {

	private final Logger logger = Logger.getLogger(WsSmsuExceptionMapper.class);
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> toResponse(Exception ex) {
    	
    	logger.warn("catching exception " + ex.toString(), ex);
    	HttpStatus statusCode = HttpStatus.BAD_REQUEST;
    	
    	if(ex instanceof SmsuForbiddenException) {
    		statusCode = HttpStatus.FORBIDDEN;
    	}
    	
    	return Helper.jsonErrorResponse(statusCode, ex.toString());
    }
}