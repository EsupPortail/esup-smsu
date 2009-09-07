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
	 * The message doesn't match the front office quota. 
	 */
	FO_QUOTA_ERROR,
	/**
	 * The message doesn't match the front office max sms for one sent. 
	 */
	FO_NB_MAX_CUSTOMIZED_GROUP_ERROR,
	/**
	 * no recipient found. 
	 */
	NO_RECIPIENT_FOUND
}
