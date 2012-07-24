/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.services.client; 

import java.io.Serializable;




/**
 * The interface of the information remote service.
 */
public interface NotificationPhoneNumberInBlackList extends Serializable {

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	boolean isPhoneNumberInBlackList(String phoneNumber);
	

}
