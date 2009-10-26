/**
 * ESUP-Portal Example Application - Copyright (c) 2006 ESUP-Portal consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsu.services.client; 

import java.io.Serializable;

import org.esupportail.smsu.exceptions.InsufficientQuotaException;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;


/**
 * The interface of the information remote service.
 */
public interface SendSms extends Serializable {

	/**
	 * @param nbDest 
	 * @param labelAccount 
	 * @return the Quota check.
	 * @throws UnknownIdentifierApplicationException 
	 * @throws InsufficientQuotaException 
	 */
	Boolean isQuotaOk(Integer nbDest, String labelAccount)
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException;
	
	/**
	 * send SMS.
	 * @param msgId 
	 * @param perId 
	 * @param bgrId 
	 * @param svcId 
	 * @param smsPhone 
	 * @param labelAccount 
	 * @param msgContent 
	 * @throws UnknownIdentifierApplicationException 
	 */
	void sendSMS(Integer msgId, Integer perId, Integer bgrId, 
			Integer svcId, String smsPhone, 
			String labelAccount, String msgContent);
	

}
