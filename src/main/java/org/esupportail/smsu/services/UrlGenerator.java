package org.esupportail.smsu.services;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

public class UrlGenerator {

	private final Logger logger = new LoggerImpl(getClass());

	private String serviceURL;
	
	public String baseURL(HttpServletRequest request) {
		try {
			return get_baseURL(request, serviceURL);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}   

	public String goTo(HttpServletRequest request, String then) {
		return baseURL(request) + "/#" + then;
	}

	static public String get_baseURL(HttpServletRequest request, String serviceURL) throws IOException {
	if (StringUtils.isBlank(serviceURL)) {
	    String url = request.getRequestURL().toString();
	    return url.replaceFirst("(/|/WebWidget|/index.html|/rest/.*)$", "");
	} else {
	    return serviceURL;
	}
    }

    public void setServiceURL(String serviceURL) {
	this.serviceURL = serviceURL;
    }
    
}
