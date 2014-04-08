package org.esupportail.smsu.web;
 
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.io.IOUtils;
import org.esupportail.smsu.services.UrlGenerator;

public class StartPage implements org.springframework.web.HttpRequestHandler {

    private UrlGenerator urlGenerator;

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
	ServletContext context = request.getSession().getServletContext();
	boolean isWebWidget = request.getServletPath().equals("/WebWidget");
	boolean genTestStaticJsonPage = request.getServletPath().equals("/GenTestStaticJsonPage");
	String baseURL = genTestStaticJsonPage ? ".." : urlGenerator.baseURL(request);
	String page = getWebWidgetHtml(context, baseURL, isWebWidget, genTestStaticJsonPage);
	if (!isWebWidget) 
	    page = getStartPageHtml(context, page);	
	response.setContentType("text/html; charset=UTF-8");
        response.getWriter().print(page);
    }

    static public String getStartPageHtml(ServletContext context, String webWidget) throws IOException {
	String s = getHtmlTemplate(context, "/WEB-INF/StartPage-template.html");
	return instantiateTemplate(s, "webWidget", webWidget);
    }

    static public String getWebWidgetHtml(ServletContext context, String baseURL, 
    			boolean isWebWidget, boolean genTestStaticJsonPage) throws IOException {
	String s = getHtmlTemplate(context, "/WEB-INF/WebWidget-template.html");
	return instantiateWebWidgetHtml(s, baseURL, isWebWidget, genTestStaticJsonPage);
    }

    static public String instantiateWebWidgetHtml(String template, String baseURL, boolean isWebWidget, boolean genTestStaticJsonPage) {
	String s = template;
	s = instantiateTemplate(s, "baseURL", baseURL);
	s = instantiateTemplate(s, "loginURL", genTestStaticJsonPage ? "test/login.jsonp" : "rest/login");
    s = instantiateTemplate(s, "isWebWidget", ""+isWebWidget);
    s = instantiateTemplate(s, "useTestStaticJson", ""+genTestStaticJsonPage);
	return s;
    }

    static public String instantiateTemplate(String template, String var, String value) {
	return template.replaceAll(Pattern.quote("{{" + var + "}}"), value);
    }

    static public String getHtmlTemplate(ServletContext context, String path) throws IOException {
	return IOUtils.toString(context.getResourceAsStream(path), "UTF-8");
    }

	public void setUrlGenerator(UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

}
