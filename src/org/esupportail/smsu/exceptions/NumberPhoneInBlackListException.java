package org.esupportail.smsu.exceptions;

	/**
	 * A class for identifer applica exceptions.
	 */
	public class NumberPhoneInBlackListException extends Exception {
		
			/**
			 * The id for serialization.
			 */
			private static final long serialVersionUID = 8197090501242229324L;

			/**
			 * @param message
			 */
			public NumberPhoneInBlackListException(final String message) {
				super(message);
			}

			/**
			 * @param cause
			 */
			public NumberPhoneInBlackListException(final Throwable cause) {
				super(cause);
			}

			/**
			 * @param message
			 * @param cause
			 */
			public NumberPhoneInBlackListException(final String message, final Throwable cause) {
				super(message, cause);
			}
		
	}
