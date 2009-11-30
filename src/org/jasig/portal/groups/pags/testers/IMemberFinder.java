package org.jasig.portal.groups.pags.testers;

import org.springframework.ldap.support.filter.Filter;


public interface IMemberFinder {
	public Filter getLdapFilter(String attributeName, String attributeValue);
}
