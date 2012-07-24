/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.services.client; 

import java.io.Serializable;
import java.util.Set;




/**
 * The interface of the information remote service.
 */
public interface ListPhoneNumbersInBlackList extends Serializable {

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	Set<String> getListPhoneNumbersInBlackList();
	

}
