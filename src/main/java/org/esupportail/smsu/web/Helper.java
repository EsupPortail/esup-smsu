package org.esupportail.smsu.web;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Helper {

	public static ResponseEntity<?> jsonErrorResponse(HttpStatus status, String error) {
    	return jsonErrorResponse(status, error, null);
    }
	
    public static ResponseEntity<?> jsonErrorResponse(HttpStatus status, String error, String message) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("error", error);
        if (message != null) node.put("message", message);
        return new ResponseEntity<ObjectNode>(node, headers("Content-Type", "application/json;charset=UTF-8"), status);
    }

    public static MultiValueMap<String, String> headers(String k, String v) {
        MultiValueMap<String, String> r = new LinkedMultiValueMap<>();
        r.add(k, v);    
        return r;
    }
}
