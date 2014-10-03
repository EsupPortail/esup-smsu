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
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.SecurityManager;
import org.esupportail.smsu.services.UrlGenerator;
import org.jasig.cas.client.util.CommonUtils;


public final class AuthAndRoleAndMiscFilter implements Filter {
	
    @Autowired private UrlGenerator urlGenerator;
    @Autowired private SecurityManager securityManager;

    private String authentication = "cas";
    private boolean shibUseHeaders = false;
    private String shibbolethSessionInitiatorUrl;

    private String sessionAttributeName = "MY_REMOTE_USER";
    private String rightAllowingImpersonate = "FCTN_GESTION_ROLES_AFFECT";

    private final Logger logger = Logger.getLogger(getClass());

    public void destroy() {}
    public void init(FilterConfig config) {}

    /**
     * Wraps the HttpServletRequest in a wrapper class that delegates <code>request.isUserInRole</code> 
     */
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String user = getRemoteUser(request);
	if (user == null) {
	    handleNotAuthenticated(request, response);
	    return;
	}

        Set<String> rights = securityManager.loadUserRightsByUsername(user);
        if (request.getHeader("X-Impersonate-User") != null) {
	    if (!rights.contains(rightAllowingImpersonate))
	        throw new RuntimeException("Impersonate not allowed");

        	user = request.getHeader("X-Impersonate-User");
        	rights = securityManager.loadUserRightsByUsername(user);
        }
        
        // ugly hack for Internet Explorer. It would need to be done somewhere else...
        response.setHeader("Cache-Control", "no-cache");
        
		filterChain.doFilter(new MyHttpServletRequestWrapper(request, user, rights), response);
    }

	private String getRemoteUser(HttpServletRequest request) {
		if (authentication.equals("cas")) {
        	// the job is done by org.jasig.cas.client.util.HttpServletRequestWrapperFilter
        	return request.getRemoteUser();
		} else {			
			HttpSession session = request.getSession();
			String user = (String) session.getAttribute(sessionAttributeName);
        	if (user == null && allowShibbolethSPLogin(request)) {
        		user = loginShibboleth(request);
        	}
        	return user;
        }		
	}
	
	private String loginShibboleth(HttpServletRequest request) {
		HttpSession session = request.getSession();
		// we rely on shibboleth SP to do the authentication
		// use either use normal stuff (AJP) or HTTP header (if really needed...)
		String user = request.getRemoteUser();
		if (user == null) {
			user = request.getHeader("REMOTE_USER");
			if (StringUtils.isBlank(user)) user = null; // why is this needed? is shibboleth SP with lazy sessions doing this? 
			if (user != null && !shibUseHeaders)
				throw new RuntimeException("Received HTTP header REMOTE_USER. You must be using \"ShibUserHeaders On\" in apache configuration. In that case set shibboleth.shibUserHeaders=true");
		}
		if (user != null) session.setAttribute(sessionAttributeName, user);
		return user;
	}
    
	private void handleNotAuthenticated(HttpServletRequest request,	HttpServletResponse response) throws IOException {
		if (allowShibbolethSPLogin(request)) {
			shibbolethSessionInitiate(request, response);
		} else {
			unauthorized(response);
		}
	}

	private void shibbolethSessionInitiate(HttpServletRequest request,	HttpServletResponse response) throws IOException {
		String serverURL = urlGenerator.getServerURL();

		// re-use code from CAS to reconstruct current URL
		// (nb: not using encode=true since it adds jsessionid in url which mess up things. we do not need it since we will have _shibsession_)
		String target = CommonUtils.constructServiceUrl(request, response, null, serverURL, "_shibsession_", false);
		
		String url = shibbolethSessionInitiatorUrl + "?target=" + UrlGenerator.urlencode(target);

		String idpId = request.getParameter("idpId");
		if (idpId != null) url += "&providerId=" + idpId;

		response.sendRedirect(concatUrlAndLocation(serverURL, url));
	}
	
	private String concatUrlAndLocation(String serverURL, String url) throws MalformedURLException {
		return new URL(new URL(serverURL), url).toString();
	}

    private void unauthorized(HttpServletResponse response) throws IOException {
	response.setHeader("WWW-Authenticate", "CAS");
	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean allowShibbolethSPLogin(HttpServletRequest request) {
		return authentication.equals("shibboleth") &&
				request.getPathInfo().equals("/login") &&
				(request.getParameter("postMessage") != null ||
				 request.getParameter("then") != null);
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
    
    public void setAuthentication(String authentication) {
 		this.authentication = authentication;
 	}
 	public void setShibUseHeaders(boolean shibUseHeaders) {
 		this.shibUseHeaders = shibUseHeaders;
 	}
	public void setShibbolethSessionInitiatorUrl(String shibbolethSessionInitiatorUrl) {
		this.shibbolethSessionInitiatorUrl = shibbolethSessionInitiatorUrl;
	}

}
