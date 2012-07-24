/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.services.client; 

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;


/**
 * The interface of the information remote service.
 */
public class NotificationPhoneNumberInBlackListClient {

	private NotificationPhoneNumberInBlackList notificationPhoneNumber;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * Bean constructor.
	 */
	public NotificationPhoneNumberInBlackListClient() {
		
	}
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) {
		logger.info("isPhoneNumberInBlackList method client send with parameter : " + " - phoneNumber = " + phoneNumber);

		final boolean retVal = notificationPhoneNumber.isPhoneNumberInBlackList(phoneNumber);
		
		return retVal;
	}

	public void setNotificationPhoneNumber(
			final NotificationPhoneNumberInBlackList notificationPhoneNumber) {
		this.notificationPhoneNumber = notificationPhoneNumber;
	}
	
}
