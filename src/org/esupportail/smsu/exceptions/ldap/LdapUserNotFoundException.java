package org.esupportail.smsu.exceptions.ldap;

/**
 * Used when an user is not found in the ldap.
 * @author PRQD8824
 *
 */
public class LdapUserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4244684563927159925L;

	/**
	 * Default constructor.
	 */
	public LdapUserNotFoundException() {
		super();
	}
	
	/**
	 * Constructor with message.
	 * @param message
	 */
	public LdapUserNotFoundException(final String message) {
		super(message);
	}
	
	/**
	 * Constructor with message and cause.
	 * @param message
	 * @param t
	 */
	public LdapUserNotFoundException(final String message, final Throwable t) {
		super(message, t);
	}
		
	
}
