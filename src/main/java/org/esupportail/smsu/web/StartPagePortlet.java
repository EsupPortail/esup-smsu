package org.esupportail.smsu.web;

import java.io.IOException;

import javax.portlet.*;

import org.apache.commons.io.IOUtils;
import org.esupportail.commons.context.ApplicationContextHolder;
import org.springframework.context.ApplicationContext;

public class StartPagePortlet extends GenericPortlet {
	
   public void init (PortletConfig portletConfig) throws UnavailableException, PortletException
   {
      super.init( portletConfig );
   }

   public void doView(RenderRequest request, RenderResponse response)
                      throws PortletException, IOException
   {
       String page = getWebWidgetHtml(getPortletContext(), getRequestURI(request));
       response.setContentType("text/html");
       response.getWriter().print(page);
    }

    static public String getRequestURI(PortletRequest request) {
	return (String) request.getAttribute(/* JSR168 PLT.16.3.1 */ "javax.servlet.include.context_path");
    }

    static public String getWebWidgetHtml(PortletContext context, String baseURL) throws IOException {
		// need <bean id="..." class="org.esupportail.commons.context.ApplicationContextHolder" />
		ApplicationContext applicationContext = ApplicationContextHolder.getContext();
		StartPage startPage = (StartPage) applicationContext.getBean("StartPage");

	String s = getHtmlTemplate(context, "/WEB-INF/WebWidget-template.html");
	return startPage.instantiateWebWidgetHtml(s, baseURL, true);
    }

    static public String getHtmlTemplate(PortletContext context, String path) throws IOException {
	return IOUtils.toString(context.getResourceAsStream(path), "UTF-8");
    }

}
