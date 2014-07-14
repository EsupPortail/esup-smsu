package org.esupportail.smsu.web;

import java.io.InputStream;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

public class ServletContextWrapper {
	private ServletContext servletContext;
	private PortletContext portletContext;
	
	public ServletContextWrapper(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContextWrapper(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	public String getRealPath(String path) {
		if (portletContext != null)
			return portletContext.getRealPath(path);
		else
			return servletContext.getRealPath(path);
	}

	public InputStream getResourceAsStream(String path) {
		if (portletContext != null)
			return portletContext.getResourceAsStream(path);
		else
			return servletContext.getResourceAsStream(path);
	}
}
