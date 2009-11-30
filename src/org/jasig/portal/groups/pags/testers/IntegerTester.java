/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.groups.pags.testers;

/**
 * Abstract class tests the possibly multiple values of an 
 * <code>IPerson</code> integer attribute. 
 * <p>
 * @author Dan Ellentuck
 * @version $Revision: 34757 $
 */

public abstract class IntegerTester extends BaseAttributeTester {
    protected int testInteger = Integer.MIN_VALUE;

public IntegerTester(String attribute, String test) {
    super(attribute, test); 
    testInteger = Integer.parseInt(test);
}
public int getTestInteger() {
    return testInteger;
}

}