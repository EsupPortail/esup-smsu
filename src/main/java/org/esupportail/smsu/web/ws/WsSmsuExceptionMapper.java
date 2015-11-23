package org.esupportail.smsu.web.ws;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.esupportail.smsu.exceptions.SmsuForbiddenException;
import org.esupportail.smsu.web.Helper;


@Provider
public class WsSmsuExceptionMapper implements
        ExceptionMapper<Exception> {

	private final Logger logger = Logger.getLogger(WsSmsuExceptionMapper.class);
	
    @Override
    public Response toResponse(Exception ex) {
    	
    	logger.warn("catching exception " + ex.toString(), ex);
    	int statusCode = 400;
    	
    	if(ex instanceof SmsuForbiddenException) {
    		statusCode = HttpServletResponse.SC_FORBIDDEN;
    	}
    	
    	return Helper.jsonErrorResponse(statusCode, ex.toString());
    }
}