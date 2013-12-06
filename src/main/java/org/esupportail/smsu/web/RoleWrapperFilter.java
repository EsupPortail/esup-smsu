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


public final class RoleWrapperFilter implements Filter {
	
    @Autowired private SecurityManager securityManager;

    private final Logger logger = new LoggerImpl(getClass());

    public void destroy() {}
    public void init(FilterConfig config) {}

    /**
     * Wraps the HttpServletRequest in a wrapper class that delegates <code>request.isUserInRole</code> 
     */
    public void doFilter(final ServletRequest servletRequest, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
	if (request.getRemoteUser() == null) {
	    unauthorized((HttpServletResponse) response);
	    return;
	}

        filterChain.doFilter(new MyHttpServletRequestWrapper(request, retrieveRoles(request)), 
			     response);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
	response.setHeader("WWW-Authenticate", "CAS");
	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    protected Set<String> retrieveRoles(final HttpServletRequest request) {
        String user = request.getRemoteUser();
        return securityManager.loadUserRightsByUsername(user);
    }

    final class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final Set<String> roles;

        MyHttpServletRequestWrapper(final HttpServletRequest request, Set<String> roles) {
            super(request);
            this.roles = roles;
        }

        public boolean isUserInRole(final String role) {
	    if (roles.contains(role)) {
		    logger.debug("user has role " + role);
		    return true;
            }
	    String user = getRemoteUser();
	    logger.warn("user " + user + " has not role " + role);
	    for (String r : roles) logger.warn("it has role " + r);
	    return false;
        }
    }
}
