package org.esupportail.smsu.web;

import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.SecurityManager;


public final class AuthAndRoleAndMiscFilter implements Filter {
	
    @Autowired private SecurityManager securityManager;

    private final Logger logger = new LoggerImpl(getClass());

    public void destroy() {}
    public void init(FilterConfig config) {}

    /**
     * Wraps the HttpServletRequest in a wrapper class that delegates <code>request.isUserInRole</code> 
     */
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String user = request.getRemoteUser();
	if (user == null) {
	    unauthorized(response);
	    return;
	}

        Set<String> rights = securityManager.loadUserRightsByUsername(user);
        if (request.getHeader("X-Impersonate-User") != null) {
        	user = request.getHeader("X-Impersonate-User");
        	rights = securityManager.loadUserRightsByUsername(user);
        }
        
        // ugly hack for Internet Explorer. It would need to be done somewhere else...
        response.setHeader("Cache-Control", "no-cache");
        
		filterChain.doFilter(new MyHttpServletRequestWrapper(request, user, rights), response);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
	response.setHeader("WWW-Authenticate", "CAS");
	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    final class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final Set<String> roles;
        private String user;

        MyHttpServletRequestWrapper(final HttpServletRequest request, String user, Set<String> roles) {
            super(request);
            this.user = user;
            this.roles = roles;
        }

        public String getRemoteUser() {
        	return user;
        }
        
        public boolean isUserInRole(final String role) {
	    if (roles.contains(role)) {
		    logger.debug("user has role " + role);
		    return true;
            }
	    logger.warn("user " + user + " has not role " + role);
	    for (String r : roles) logger.warn("it has role " + r);
	    return false;
        }
    }
}
