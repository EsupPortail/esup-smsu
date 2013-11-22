/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.groups.pags.testers;

import org.jasig.portal.groups.pags.testers.StringTester;
import org.springframework.ldap.support.filter.EqualsFilter;
import org.springframework.ldap.support.filter.Filter;

/**
 * Tests whether or not the user has <em>some</em>
 * value for a particular attribute.
 * This tester ignores the test-value field.
 * If the attribute has any value, then it returns true.
 * @author Nick Blair, nblair@wisc.edu
 * @version $Revision: 34851 $
 */
public class ValueExistsTester extends StringTester {

    public ValueExistsTester(String attribute, String test) {
        super(attribute, test);
    }

    public boolean test(String att) {
        boolean result = false;
        if (att != null && !att.equals("")) {
            result = true;
        }
        return result;
    }

	public Filter getLdapFilter(final String attributeName, final String attributeValue) {
		return new EqualsFilter(attributeName, "*");
	}
}
