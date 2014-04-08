package org.esupportail.smsu.web.controllers;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.services.UrlGenerator;

@Path("/login")
public class LoginController {
	
    @Autowired private DomainService domainService;
    @Autowired private UrlGenerator urlGenerator;
    
    @GET
    public Response get(@Context HttpServletRequest request) throws IOException {

	String then = request.getParameter("then");
	if (then != null) {
		//then = URLDecoder.decode(then, "UTF-8");
		String url = urlGenerator.goTo(request, then);
		return Response.temporaryRedirect(URI.create(url)).build();		
	}

	User user = domainService.getUser(request.getRemoteUser());
	String jsUser = new ObjectMapper().writeValueAsString(user);
	String callback = request.getParameter("callback");
	String type = callback == null ? "text/html" : "application/x-javascript";
	String js = 
	    callback == null ?
	    "Login success, please wait...\n<script>\n (window.opener.postMessage ? window.opener : window.opener.document).postMessage('loggedUser=' + JSON.stringify(" + jsUser + "), '*');\n</script>" :
	    callback + "(" + jsUser + ")";
	
        return Response.status(Response.Status.OK).type(type).entity(js).build();
    }
 
}