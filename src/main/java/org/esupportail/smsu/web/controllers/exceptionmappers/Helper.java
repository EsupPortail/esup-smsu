package org.esupportail.smsu.web.controllers.exceptionmappers;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class Helper {

	public static Response jsonErrorResponse(int status, String error) {
    	return jsonErrorResponse(status, error, null);
    }
	
    public static Response jsonErrorResponse(int status, String error, String message) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("error", error);
        if (message != null) node.put("message", message);
        return Response.status(status).entity(node).type("application/json").build();
    }
}
