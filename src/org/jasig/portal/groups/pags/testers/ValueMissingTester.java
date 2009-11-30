/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.groups.pags.testers;

import org.jasig.portal.groups.pags.testers.BaseAttributeTester;
import org.springframework.ldap.support.filter.EqualsFilter;
import org.springframework.ldap.support.filter.Filter;
import org.springframework.ldap.support.filter.NotFilter;

/**
 * Tests whether the attribute is null or none of the
 * values of the attribute equal the specified attribute value.
 * @author Eric Dalquist, edalquist@unicon.net
 * @version $Revision: 34851 $
 */
public class ValueMissingTester extends BaseAttributeTester {

    public ValueMissingTester(String attribute, String test) {
        super(attribute, test);
    }

	public Filter getLdapFilter(final String attributeName, final String attributeValue) {
		return new NotFilter(new EqualsFilter(attributeName, "*"));
	}
}
