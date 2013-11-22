package org.esupportail.smsu.exceptions.ldap;

/**
 * Used when a modification of an attribute fails in LDAP
 * @author PRQD8824
 *
 */
public class LdapWriteException extends Exception {
	
	private static final long serialVersionUID = -7371808692875247536L;

	public LdapWriteException(final String message) {
		super(message);
	}
	
}
