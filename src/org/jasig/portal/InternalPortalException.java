/* Copyright 2001, 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal;

/**
 * A serious internal portal exception.
 * @author Peter Kharchenko
 * @version $Revision: 34872 $ $Date: 2004-10-30 12:58:19 -0700 (Sat, 30 Oct 2004) $
 */
public class InternalPortalException extends Throwable {

    /**
     * Instantiate an InternalPortalException wrapper around the given
     * Throwable.
     * @param cause - a Throwable to be wrapped
     */
    public InternalPortalException(Throwable cause) {
        super(cause);
    }

    /**
     * Delegates to getCause(). 
     * @return underlying cause
     * @deprecated use Throwable.getCause() instead
     */
    public Throwable getException() {
        return getCause();
    }

}
