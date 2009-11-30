/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.groups.pags.testers;

/**
 * Abstract class tests a possibly multi-valued attribute against
 * a test value.  
 * <p>
 * @author Dan Ellentuck
 * @version $Revision: 34757 $
 */

public abstract class StringTester extends BaseAttributeTester {

public StringTester(String attribute, String test) {
    super(attribute, test);
}

}
