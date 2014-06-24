package org.esupportail.smsu.web;

/*
 * Copyright (c) 2009-2009 Mort Bay Consulting Pty. Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

/* code below is a simplified version of org.eclipse.jetty.servlets.CrossOriginFilter.
 * the original version is great but only allow FilterConfig hence no spring beans :-(
 * so here we have a setter for allowedOrigins
 *
 * NB: another solution would have been to use servlet3: servletContext.addFilter(...).setInitParameter("allowedMethods", ....)
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrossOriginFilter implements Filter
{
    private List<String> allowedOrigins = new ArrayList<String>();

    // preflight cache duration in the browser
    private String maxAge = "600"; // 600 seconds = 10 minutes
    private String allowedMethods = "GET,POST,PUT,DELETE";
    private String allowedHeaders = "X-CSRF-TOKEN,Content-Type,Accept,Origin";

    public void destroy() {}
    public void init(FilterConfig config) {}

    public void setAllowedOrigins(String origins) throws ServletException {
        allowedOrigins.clear();
	allowedOrigins.addAll(Arrays.asList(origins.split(",")));
    }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        handle((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String origin = request.getHeader("Origin");
        if (origin != null && allowedOrigins.contains(origin)) {
	    response.setHeader("Access-Control-Allow-Origin", origin);
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    if (isPreflightRequest(request)) {
		response.setHeader("Access-Control-Max-Age", maxAge);
		response.setHeader("Access-Control-Allow-Methods", allowedMethods);
		response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
		return;
	    }
        }
        chain.doFilter(request, response);
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return 
	    "OPTIONS".equalsIgnoreCase(request.getMethod()) &&
	    request.getHeader("Access-Control-Request-Method") != null;
    }

}
