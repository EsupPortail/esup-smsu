package org.esupportail.smsu.web;
 
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.esupportail.smsu.services.UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class StartPage implements org.springframework.web.HttpRequestHandler {

    @Autowired private UrlGenerator urlGenerator;
    @Autowired private ServerSideDirectives serverSideDirectives;
    private String wsgroupsURL;
    
    private boolean jsonpDisabled = false;

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
	ServletContextWrapper context = new ServletContextWrapper(request.getSession().getServletContext());
	boolean isWebWidget = request.getServletPath().startsWith("/WebWidget");
	boolean genTestStaticJsonPage = request.getServletPath().equals("/GenTestStaticJsonPage");
	String baseURL = genTestStaticJsonPage ? ".." : urlGenerator.baseURL(request);
	Map<String, Object> env = createEnv(baseURL, isWebWidget, genTestStaticJsonPage);

	String template = getHtmlTemplate(context, "/WEB-INF/WebWidget-template.html");
	String page = instantiateTemplate(context, env, template);
	if (!isWebWidget) 
	    page = getStartPageHtml(context, page);	
	String type = "text/html; charset=UTF-8";
	if (request.getServletPath().endsWith(".js")) {
	    type = "application/x-javascript";
	    page = "document.write(" + new ObjectMapper().writeValueAsString(page) + ");";
	}

	response.setContentType(type);
        response.getWriter().print(page);
    }

	public String instantiateTemplate(ServletContextWrapper context, Map<String, Object> env, String template) {
		return serverSideDirectives.instantiate(template, env, context);
	}

    private String getStartPageHtml(ServletContextWrapper context, String webWidget) throws IOException {
    	String s = getHtmlTemplate(context, "/WEB-INF/StartPage-template.html");
    	return serverSideDirectives.instantiate_vars(s, singletonMap("webWidget", (Object) webWidget));
    }

	public Map<String, Object> createEnv(String baseURL, boolean isWebWidget, boolean genTestStaticJsonPage) throws IOException {
		Map<String, Object> env = new TreeMap<>();
    	env.put("baseURL", baseURL);
    	env.put("wsgroupsURL", wsgroupsURL);
    	env.put("isWebWidget", isWebWidget);
    	env.put("jsonpDisabled", jsonpDisabled);
    	env.put("useTestStaticJson", genTestStaticJsonPage);
    	env.put("globals", new ObjectMapper().writeValueAsString(env));
		return env;
	}

	private <A, B> Map<A, B> singletonMap(A key, B value) {
		Map<A, B> r = new TreeMap<>();
		r.put(key, value);
		return r;
	}

    static public String getHtmlTemplate(ServletContextWrapper context, String path) throws IOException {
	return IOUtils.toString(context.getResourceAsStream(path), "UTF-8");
    }

	public void setWsgroupsURL(String wsgroupsURL) {
		this.wsgroupsURL = wsgroupsURL;
	}

	public void setJsonpDisabled(boolean jsonpDisabled) {
		this.jsonpDisabled = jsonpDisabled;
	}

}
