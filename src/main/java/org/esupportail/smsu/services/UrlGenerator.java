package org.esupportail.smsu.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class UrlGenerator {

	private final Logger logger = Logger.getLogger(getClass());

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
		return goTo(request, then, null);
	}

	public String goTo(HttpServletRequest request, String then, String sessionId) {
		if (sessionId != null) {
			// add sessionId as a "search" parameter in hash part of url
			then += (then.contains("?") ? "&" : "?") + "sessionId=" + sessionId;
		}
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

    public static String urlencode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("urlencode failed on '" + s + "'");
        }
    }

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = cleanupServerUrl(serverURL);
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
    
}
