package org.esupportail.smsu.web.controllers.exceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.esupportail.smsu.web.controllers.InvalidParameterException;

@Provider
public class InvalidParameterExceptionMapper implements
        ExceptionMapper<InvalidParameterException> {
    @Override
    public Response toResponse(InvalidParameterException ex) {
        return Helper.jsonErrorResponse(400, ex.getMessage());
    }
}