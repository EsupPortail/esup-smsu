package org.esupportail.smsu.web.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.services.UrlGenerator;
import org.esupportail.smsu.web.Helper;

@Controller
@RequestMapping(value = "/login")
public class LoginController {
	
    @Autowired private DomainService domainService;
    @Autowired private UrlGenerator urlGenerator;
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get(HttpServletRequest request) throws IOException {
    	boolean ourCookiesRejected = ourCookiesRejected(request);

	String sessionId = ourCookiesRejected ? request.getSession().getId() : null;
    
	String then = request.getParameter("then");
	if (then != null) {
		//then = URLDecoder.decode(then, "UTF-8");
		String url = urlGenerator.goTo(request, then, sessionId);
		return new ResponseEntity<>(Helper.headers("Location", url), HttpStatus.TEMPORARY_REDIRECT);		
	}

	User user = domainService.getUser(request.getRemoteUser());
	if (ourCookiesRejected) {
		user.sessionId = request.getSession().getId();
	}
	String jsUser = new ObjectMapper().writeValueAsString(user);
	String content, type;
	if (request.getParameter("postMessage") != null) {
		type = "text/html";
		content = "Login success, please wait...\n<script>\n (window.opener ? (window.opener.postMessage ? window.opener : window.opener.document) : window.parent).postMessage('loggedUser=' + JSON.stringify(" + jsUser + "), '*');\n</script>";
	} else if (request.getParameter("callback") != null) {
		type = "application/x-javascript";
		content = request.getParameter("callback") + "(" + jsUser + ")";
	} else {
		type = "application/json";
		content = jsUser;
	}
        return new ResponseEntity<>(content, Helper.headers("Content-Type", type), HttpStatus.OK);
    }

	// call this function on successful login
    // if we managed to get here and there is no cookie, it means they have been rejected
    private boolean ourCookiesRejected(HttpServletRequest request) {
    	for (Cookie cookie : request.getCookies()) {
    		if (cookie.getName().equals("JSESSIONID"))
    			// cool, our previous Set-Cookie was accepted
    			return false;
    		if (cookie.getName().startsWith("_shibsession_"))
    			// Shibboleth SP Set-Cookie was accepted
    			return false;
    	}
    	return true;
     }

}