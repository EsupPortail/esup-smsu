package org.esupportail.smsu.exceptions;

	/**
	 * A class for identifer applica exceptions.
	 */
	public class UnknownIdentifierMessageException extends Exception {
		
			/**
			 * The id for serialization.
			 */
			private static final long serialVersionUID = 8197090501242229324L;

			/**
			 * @param message
			 */
			public UnknownIdentifierMessageException(final String message) {
				super(message);
			}

			/**
			 * @param cause
			 */
			public UnknownIdentifierMessageException(final Throwable cause) {
				super(cause);
			}

			/**
			 * @param message
			 * @param cause
			 */
			public UnknownIdentifierMessageException(final String message, final Throwable cause) {
				super(message, cause);
			}
		
	}
