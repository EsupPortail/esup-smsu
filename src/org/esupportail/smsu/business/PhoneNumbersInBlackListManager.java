package org.esupportail.smsu.business;

import java.util.Iterator;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.services.client.ListPhoneNumbersInBlackListClient;

/**
 * Business layer concerning smsu service.
 *
 */
public class PhoneNumbersInBlackListManager {
	
	/**
	 * {@link listPhoneNumbersInBlackListClient}.
	 */
	private ListPhoneNumbersInBlackListClient listPhoneNumbersInBlackListClient;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public PhoneNumbersInBlackListManager() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Principal method
	//////////////////////////////////////////////////////////////
	/**
	 * Retrieve phone numbers in black list.
	 * @return the list of blacklist phone numbers
	 */
	public Set<String> getListPhoneNumbersInBlackList() {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("getListPhoneNumbersInBlackList method client send ");
			logger.debug(sb.toString());
		}
		final Set<String> retVal = listPhoneNumbersInBlackListClient.getListPhoneNumbersInBlackList();
		 if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(500);
				sb.append("Response for getListPhoneNumbersInBlackList request :");
				Iterator<String> iter = retVal.iterator();
				while (iter.hasNext()) {
				sb.append(" - phone number in blacklist = ").append(iter.next());	
				}
				logger.debug(sb.toString());
			}
		return retVal;
	}

	////////////////////////////////////////////////////////////////
	//  setter for spring object listPhoneNumbersInBlackListClient
	////////////////////////////////////////////////////////////////
	public void setListPhoneNumbersInBlackListClient(
			final ListPhoneNumbersInBlackListClient listPhoneNumbersInBlackListClient) {
		this.listPhoneNumbersInBlackListClient = listPhoneNumbersInBlackListClient;
	}

}
