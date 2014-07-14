package org.esupportail.smsu.services;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

public class UrlGenerator {

	private final Logger logger = new LoggerImpl(getClass());

	private String serverURL;
	private String contextPath;
	
	public String baseURL(HttpServletRequest request) {
		try {
			return get_baseURL(request, serverURL, contextPath);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}   

	public String goTo(HttpServletRequest request, String then) {
		return baseURL(request) + "/#" + then;
	}

	static public String get_baseURL(HttpServletRequest request, String serverURL, String contextPath) throws IOException {
		if (StringUtils.isBlank(contextPath)) {
			contextPath = request.getContextPath();
		}
		contextPath = contextPath.replaceFirst("/$", "");
		return serverURL + contextPath;	
    }

	// since java-cas-client breaks its serverName when the port is not explicit,
	// we end up here with urls with explicit ports even when unneeded.
	// alas angularjs $sceDelegateProvider.resourceUrlWhitelist does not like this it seems,
	// so a nice cleanup here will help
	// (also cleanup trailing slash that would break web.xml <url-pattern> in case of double "/")
	private String cleanupServerUrl(String serverURL) {
		serverURL = serverURL.replaceFirst("/$", "");
		if (serverURL.startsWith("http://"))
			return serverURL.replaceFirst(":80/?$", "");
		else if (serverURL.startsWith("https://"))
			return serverURL.replaceFirst(":443/?$", "");
		else
			return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = cleanupServerUrl(serverURL);
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
    
}
