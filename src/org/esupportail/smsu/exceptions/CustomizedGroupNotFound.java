package org.esupportail.smsu.exceptions;

/**
 * Used when the system is enable to define a customized group.
 * @author xphp8691
 *
 */
public class CustomizedGroupNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6164356965254888885L;

	
	/**
	 * Constuctor.
	 */
	public CustomizedGroupNotFound() {
		super();
	}

	/**
	 * Constructor with message and cause.
	 * @param message
	 * @param cause
	 */
	public CustomizedGroupNotFound(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with message.
	 * @param message
	 */
	public CustomizedGroupNotFound(final String message) {
		super(message);
	}

	/**
	 * Constructor with cause.
	 * @param cause
	 */
	public CustomizedGroupNotFound(final Throwable cause) {
		super(cause);
	}
	
}
