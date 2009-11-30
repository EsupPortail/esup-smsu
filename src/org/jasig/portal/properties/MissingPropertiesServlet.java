/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A servlet that reports missing properties.
 * Exposes as XML the list of properties the PropertiesManager
 * has been asked about but for which it has no values.
 * @author andrew.petro@yale.edu
 * @version $Revision: 35696 $ $Date: 2005-04-30 13:09:41 -0700 (Sat, 30 Apr 2005) $
 * @since uPortal 2.4
 * @deprecated No longer necessary since CMissingProperties was introduced in uPortal 2.5.
 */
public class MissingPropertiesServlet extends HttpServlet {

    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

 
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            out.println("<?xml version=\"1.0\" ?>");
            out.println("<missingProperties>");

            Set missingProperties = PropertiesManager.getMissingProperties();

            for (Iterator iter = missingProperties.iterator(); iter.hasNext(); ){
                out.println("<property>");
                out.println(iter.next());
                out.println("</property>");
            }

            out.println("</missingProperties>");
    }
}