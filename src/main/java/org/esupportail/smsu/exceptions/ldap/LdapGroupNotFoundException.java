package org.esupportail.smsu.exceptions.ldap;

/**
 * 
 * @author PRQD8824
 *
 */
public class LdapGroupNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3428620017629502506L;

	/**
	 * Default constructor.
	 */
	public LdapGroupNotFoundException() {
		super();
	}
	
	/**
	 * Constructor with message.
	 * @param message
	 */
	public LdapGroupNotFoundException(final String message) {
		super(message);
	}
	
	/**
	 * Constructor with message and cause.
	 * @param message
	 * @param t
	 */
	public LdapGroupNotFoundException(final String message, final Throwable t) {
		super(message, t);
	}
}