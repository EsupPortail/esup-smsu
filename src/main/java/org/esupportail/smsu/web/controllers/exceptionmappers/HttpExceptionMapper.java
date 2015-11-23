package org.esupportail.smsu.web.controllers.exceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.esupportail.smsu.web.Helper;
import org.esupportail.smsuapi.utils.HttpException;

@Provider
public class HttpExceptionMapper implements
        ExceptionMapper<HttpException> {
    @Override
    public Response toResponse(HttpException ex) {
        return Helper.jsonErrorResponse(400, ex.getClass().getSimpleName(), ex.toString());
    }
}