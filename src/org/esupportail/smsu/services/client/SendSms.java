/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
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
	 * @return the Quota check.
	 */
	Boolean isQuotaOk(Integer nbDest, String labelAccount)
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException;
	
	/**
	 * send SMS.
	 */
	void sendSMS(Integer msgId, Integer perId, Integer bgrId, 
			Integer svcId, String smsPhone, 
			String labelAccount, String msgContent);
	

}
