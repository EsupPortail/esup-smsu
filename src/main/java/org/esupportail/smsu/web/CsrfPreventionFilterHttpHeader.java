/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.smsu.web;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

/**
 * Provides basic CSRF protection for a web application. 
 * The filter assumes that accesses from client have a HTTP header X-CSRF-TOKEN with value returned with previous 403 request.
 * This means the first request will always fail, and it is the responsability of the client to resend request with the token.
 *
 * NB : we do not go the "standard" angular way of setting a cookie since the application is a web-widget,
 *      which implies the cookie will not be accessible in javascript:
 *      the cookie would be set for web-widget app foo.com 
 *      whereas the web-widget app will be included in app bar.com which won't have access to foo.com cookies
 *
 * NB2: it could be much simpler to use jersey's very simple CsrfProtectionFilter 
 *           &lt;param-name&gt;com.sun.jersey.spi.container.ContainerRequestFilters&lt;/param-name&gt;
 *           &lt;param-value&gt;com.sun.jersey.api.container.filter.CsrfProtectionFilter&lt;/param-value&gt;
 *      which checks "X-Requested-By" HTTP header for POST/PUT/DELETE
 *      but it's not clear wether it is enough
 *      such HTTP header can only be set using XHR... except when there are bugs
 *      http://lists.webappsec.org/pipermail/websecurity_lists.webappsec.org/2011-February/007533.html
 *      (CSRF: Flash + 307 redirect = Game Over)
 *      issue fixed long time ago in firefox: 3.5.17, 3.6.14 (http://www.mozilla.org/security/announce/2011/mfsa2011-10.html)
 *      but what about safari?
 *      Ruby on Rails and Django have given up the X-Requested-By technique...
 *
 * inspired from org.apache.catalina.filters.CsrfPreventionFilter
 */
public class CsrfPreventionFilterHttpHeader implements Filter {

    private final Logger logger = new LoggerImpl(getClass());
    private Random randomSource = new SecureRandom();

    private int denyStatus = HttpServletResponse.SC_FORBIDDEN;
    private String httpHeaderName = "X-CSRF-TOKEN";
    private String sessionAttrName = "org.esupportail.smsu.web.CSRF_TOKEN";
    private static final Set<String> METHODS_TO_IGNORE;

    static {
        Set<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet(mti);
    }

    public void destroy() {}
    public void init(FilterConfig config) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest &&
                response instanceof HttpServletResponse) {
            if (!check((HttpServletRequest) request, (HttpServletResponse) response))
            	return;
        }
        chain.doFilter(request, response);
    }
    
	private boolean check(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		if (METHODS_TO_IGNORE.contains(req.getMethod())) return true;

		HttpSession session = req.getSession(false);
		String existingToken = (session == null) ? null : (String) session.getAttribute(sessionAttrName);
		String previousToken = req.getHeader(httpHeaderName);

	    boolean deny = existingToken == null || previousToken == null ||
	    		!existingToken.equals(previousToken);
		    
		if (!deny) return true;

		String denyReason = 
				existingToken == null && previousToken == null ? "no token in HTTP header, no token in session" :
		    	existingToken == null ? "no token in session" :
		    	previousToken == null ? "no token in HTTP header" :
		    		"expected token and token in cookie are different";

	  	if (session == null) {
	  		session = req.getSession(true);
    	}
	  	String newToken = generateToken();
	  	session.setAttribute(sessionAttrName, newToken);

	  	logger.warn(denyReason);
	  	res.setStatus(denyStatus);
	  	res.setContentType("application/json");
	  	res.getOutputStream().println(
  				"{ \"error\": \"Invalid CRSF prevention token\"" +
	  			", \"reason\": \"" + denyReason + "\"" +
				", \"token\": \"" + newToken + "\" }");
	  	return false;
	}

    /**
     * Generate a token for authenticating subsequent
     * requests. This will also add the token to the session. The token
     * generation is a simplified version of ManagerBase.generateSessionId().
     *
     */
    protected String generateToken() {
        byte random[] = new byte[16];

        // Render the result as a String of hexadecimal digits
        StringBuilder buffer = new StringBuilder();

        randomSource.nextBytes(random);

        for (int j = 0; j < random.length; j++) {
            byte b1 = (byte) ((random[j] & 0xf0) >> 4);
            byte b2 = (byte) (random[j] & 0x0f);
            if (b1 < 10) {
                buffer.append((char) ('0' + b1));
            } else {
                buffer.append((char) ('A' + (b1 - 10)));
            }
            if (b2 < 10) {
                buffer.append((char) ('0' + b2));
            } else {
                buffer.append((char) ('A' + (b2 - 10)));
            }
        }

        return buffer.toString();
    }
}
