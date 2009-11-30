/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.car;

import org.xml.sax.ContentHandler;

/**
 * Holds a Path object for which it watches and answers true when that
 * path is seen. Also holds onto the handler for the sub-tree below
 * that path.
 * 
 * @author mboyd
 * @version $Revision: 35612 $ $Date: 2005-04-18 13:57:17 -0700 (Mon, 18 Apr 2005) $
 **/
class PathRouter
{
    Path pathLookedFor = null;
    ContentHandler handler = null;

    PathRouter(Path p, ContentHandler handler)
    {
        this.pathLookedFor = p;
        this.handler = handler;
    }

    ContentHandler handler()
    {
        return handler;
    }

    boolean looksFor(Path aPath)
    {
        return pathLookedFor.equals(aPath);

    }
}
