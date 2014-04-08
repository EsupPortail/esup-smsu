package org.esupportail.smsu.services.ldap.beans;

/**
 * This class represents group of user.
 */
public class UserGroup {	
	public String id;
	public String name;

	public UserGroup(final String id, final String name) {
		this.id = id;
		this.name = name;
	}
}
