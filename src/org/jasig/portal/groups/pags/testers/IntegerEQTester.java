/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.groups.pags.testers;

import org.springframework.ldap.support.filter.EqualsFilter;
import org.springframework.ldap.support.filter.Filter;


/**
 * Tests if any of the possibly multiple values of the attribute are EQ
 * (equal to) the test value.  
 * <p>
 * @author Dan Ellentuck
 * @version $Revision: 34757 $
 */

public class IntegerEQTester extends IntegerTester {

public IntegerEQTester(String attribute, String test) {
    super(attribute, test); 
}
public boolean test(int attributeValue) {
    return attributeValue == testInteger;
}
public Filter getLdapFilter(String attributeName, String attributeValue) {
	return new EqualsFilter(attributeName, attributeValue);
}
}
