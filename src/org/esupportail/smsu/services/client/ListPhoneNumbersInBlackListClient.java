/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.services.client; 

import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;


/**
 * The interface of the information remote service.
 */
public class ListPhoneNumbersInBlackListClient {

	private ListPhoneNumbersInBlackList listPhoneNumbers;
	
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * Bean constructor.
	 */
	public ListPhoneNumbersInBlackListClient() {
		
	}
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public Set<String> getListPhoneNumbersInBlackList() {
		logger.info("getListPhoneNumbersInBlackList method client send ");
		final Set<String> retVal = listPhoneNumbers.getListPhoneNumbersInBlackList();
		
		return retVal;
	}

	public void setListPhoneNumbers(
			final ListPhoneNumbersInBlackList listPhoneNumbers) {
		this.listPhoneNumbers = listPhoneNumbers;
	}

}
