package org.esupportail.smsu.exceptions;

	public class InsufficientQuotaException extends Exception {
		
			/**
			 * The id for serialization.
			 */
			private static final long serialVersionUID = 8197090501242229324L;

			/**
			 * @param message
			 */
			public InsufficientQuotaException(final String message) {
				super(message);
			}

			/**
			 * @param cause
			 */
			public InsufficientQuotaException(final Throwable cause) {
				super(cause);
			}

			/**
			 * @param message
			 * @param cause
			 */
			public InsufficientQuotaException(final String message, final Throwable cause) {
				super(message, cause);
			}
		
	}
