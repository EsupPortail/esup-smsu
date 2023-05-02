package org.esupportail.smsu.web;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.esupportail.smsu.services.UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@WebServlet( //
//		name = "StartPage", //
//		description = "StartPage", //
//		urlPatterns = { "", "/index.html", "/WebWidget.js", "/WebWidget", "/GenTestStaticJsonPage" } //
//)
@RequestMapping({ "", "/index.html", "/WebWidget.js", "/WebWidget", "/GenTestStaticJsonPage" })
public class StartPage /*extends HttpServlet*/ {
	private static final long serialVersionUID = -1793427355812892051L;

//	public static final String[] URIs = StartPage.class.getDeclaredAnnotation(WebServlet.class).urlPatterns();
	
	@Autowired
	private UrlGenerator urlGenerator;
	
	@Autowired
	private ServerSideDirectives serverSideDirectives;
	
	@Value("${wsgroups.url}")
	private String wsgroupsURL;

	@Value("#{'${authentication}' == 'shibboleth'}")
	private boolean jsonpDisabled = false;
	
	@Value("${recipient.phoneNumberPattern}")
	private String phoneNumberPattern;
	
//	@Override
	@GetMapping
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

	public Map<String, Object> createEnv(String baseURL, boolean isWebWidget, boolean genTestStaticJsonPage)
			throws IOException {
		Map<String, Object> env = new TreeMap<>();
		env.put("baseURL", baseURL);
		env.put("wsgroupsURL", wsgroupsURL);
		env.put("isWebWidget", isWebWidget);
		env.put("jsonpDisabled", jsonpDisabled);
		env.put("useTestStaticJson", genTestStaticJsonPage);
		env.put("phoneNumberPattern", phoneNumberPattern);
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
}
