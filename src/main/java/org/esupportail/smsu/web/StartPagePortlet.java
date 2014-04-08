package org.esupportail.smsu.web;

import java.io.IOException;
import javax.portlet.*;

import org.apache.commons.io.IOUtils;

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
	String s = getHtmlTemplate(context, "/WEB-INF/WebWidget-template.html");
	return StartPage.instantiateWebWidgetHtml(s, baseURL, true, false);
    }

    static public String getHtmlTemplate(PortletContext context, String path) throws IOException {
	return IOUtils.toString(context.getResourceAsStream(path), "UTF-8");
    }

}
