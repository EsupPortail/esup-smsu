package org.esupportail.smsu.exceptions;

/**
 * @author xphp8691
 *
 */
public class BackOfficeUnrichableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3367454850554977211L;

	/**
	 * 
	 */
	public BackOfficeUnrichableException() {
		super();
	
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BackOfficeUnrichableException(final String message, final Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 */
	public BackOfficeUnrichableException(final String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public BackOfficeUnrichableException(final Throwable cause) {
		super(cause);

	}

}
