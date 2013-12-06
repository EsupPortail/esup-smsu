package org.esupportail.smsu.web;
 
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class StartPage implements org.springframework.web.HttpRequestHandler {

    private String serviceURL;

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
	ServletContext context = request.getSession().getServletContext();
	boolean genTestStaticJsonPage = request.getServletPath().equals("/GenTestStaticJsonPage");
	String serviceURL = genTestStaticJsonPage ? ".." : this.serviceURL;
	String baseURL = get_baseURL(request, serviceURL);
	String page = getWebWidgetHtml(context, baseURL, genTestStaticJsonPage);
	if (!request.getServletPath().equals("/WebWidget")) 
	    page = getStartPageHtml(context, page);	
	response.setContentType("text/html; charset=UTF-8");
        response.getWriter().print(page);
    }

    static public String getStartPageHtml(ServletContext context, String webWidget) throws IOException {
	String s = getHtmlTemplate(context, "/WEB-INF/StartPage-template.html");
	return instantiateTemplate(s, "webWidget", webWidget);
    }

    static public String getWebWidgetHtml(ServletContext context, String baseURL, boolean genTestStaticJsonPage) throws IOException {
	String s = getHtmlTemplate(context, "/WEB-INF/WebWidget-template.html");
	return instantiateWebWidgetHtml(s, baseURL, genTestStaticJsonPage);
    }

    static public String instantiateWebWidgetHtml(String template, String baseURL, boolean genTestStaticJsonPage) {
	String s = template;
	s = instantiateTemplate(s, "baseURL", baseURL);
	s = instantiateTemplate(s, "loginURL", genTestStaticJsonPage ? "test/login.jsonp" : "rest/login");
        s = instantiateTemplate(s, "useTestStaticJson", ""+genTestStaticJsonPage);
	return s;
    }

    static public String get_baseURL(HttpServletRequest request, String serviceURL) throws IOException {
	if (StringUtils.isBlank(serviceURL)) {
	    String url = request.getRequestURL().toString();
	    return url.replaceFirst("/(WebWidget|index.html|)$", "");
	} else {
	    return serviceURL;
	}
    }

    static public String instantiateTemplate(String template, String var, String value) {
	return template.replaceAll(Pattern.quote("{{" + var + "}}"), value);
    }

    static public String getHtmlTemplate(ServletContext context, String path) throws IOException {
	return IOUtils.toString(context.getResourceAsStream(path), "UTF-8");
    }

    public void setServiceURL(String serviceURL) {
	this.serviceURL = serviceURL;
    }
}
