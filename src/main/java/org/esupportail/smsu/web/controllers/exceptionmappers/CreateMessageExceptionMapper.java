package org.esupportail.smsu.web.controllers.exceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.Helper;


@Provider
public class CreateMessageExceptionMapper implements
        ExceptionMapper<CreateMessageException> {
    @Override
    public Response toResponse(CreateMessageException ex) {
        return Helper.jsonErrorResponse(400, ex.toString());
    }
}