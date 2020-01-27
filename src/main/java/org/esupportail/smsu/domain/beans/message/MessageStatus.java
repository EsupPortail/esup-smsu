package org.esupportail.smsu.domain.beans.message;

/**
 * @author xphp8691
 *
 */
public enum MessageStatus {
	/**
	 * The message is being created.
	 */
	IN_PROGRESS,
	/**
	 * The message waits for approval.
	 */
	WAITING_FOR_APPROVAL,
	/**
	 * Message ready to be sent to the back office. 
	 */
	WAITING_FOR_SENDING,
	/**
	 * The message has been sent to the back office.
	 */
	SENT,
	/**
	 * The message has already been sent to the back office but something append before response.
	 */
	ALREADY_SENT,
	/**
	 * The service couldn't connect to the back office.
	 */
	WS_ERROR,
	/**
	 * An error occurred during a LDAP request.
	 */
	LDAP_ERROR,
	/**
	 * The back office sent a quota error. 
	 */
	WS_QUOTA_ERROR,
	/**
	 * The message is rejected by the approval supervisor. 
	 */
	CANCEL,
	/**
	 * no recipient found. 
	 */
	NO_RECIPIENT_FOUND
}
