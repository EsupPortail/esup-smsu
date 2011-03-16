/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsu.services.client; 

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.InsufficientQuotaException;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;


/**
 * The basic implementation of the information remote service.
 */
public class SendSmsClient  {
	
	private SendSms sendSms;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * Bean constructor.
	 */
	public SendSmsClient() {
	
	}
	
	
	/**
	 * @return the Quota check.
	 */
	public void mayCreateAccountCheckQuotaOk(final Integer nbDest, final String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {
		if (logger.isDebugEnabled()) {
			logger.debug("isQuotaOK method client send with parameters : " + 
				     " - nbDest = " + nbDest + 
				     " - labelAccount = " + labelAccount);
		}
		sendSms.mayCreateAccountCheckQuotaOk(nbDest, labelAccount);
	}
	
	
	/**
	 * send SMS.
	 * @throws UnknownIdentifierApplicationException 
	 */
	public void sendSMS(final Integer msgId, final Integer perId, 
			final Integer bgrId, final Integer svcId, 
			final String smsPhone, final String labelAccount, 
			final String msgContent) {
		if (logger.isDebugEnabled()) {
			logger.debug("SendSms client message : " + 
				     " - message id = " + msgId + 
				     " - sender id = " + perId + 
				     " - group sender id = " + bgrId + 
				     " - service id = " + svcId + 
				     " - recipient phone number = " + smsPhone + 
				     " - user label account = " + labelAccount + 
				     " - message = " + msgContent);
		}
		sendSms.sendSMS(msgId, perId, bgrId, svcId, smsPhone, labelAccount, msgContent);
		
	}

	/**
	 * Standard setter used by spring.
	 * @param sendSms
	 */
	public void setSendSms(final SendSms sendSms) {
		this.sendSms = sendSms;
	}

}
