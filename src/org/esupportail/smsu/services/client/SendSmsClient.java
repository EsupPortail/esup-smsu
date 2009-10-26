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
	public  Boolean isQuotaOk(final Integer nbDest, final String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("isQuotaOK method client send with parameters : ");
			sb.append(" - nbDest = ").append(nbDest);
			sb.append(" - labelAccount = ").append(labelAccount);
			logger.debug(sb.toString());
		}
		final boolean retVal = sendSms.isQuotaOk(nbDest, labelAccount);
		return retVal;
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
			final StringBuilder sb = new StringBuilder(200);
			sb.append("SendSms client message : ");
			sb.append(" - message id = ").append(msgId);
			sb.append(" - sender id = ").append(perId);
			sb.append(" - group sender id = ").append(bgrId);
			sb.append(" - service id = ").append(svcId);
			sb.append(" - recipient phone number = ").append(smsPhone);
			sb.append(" - user label account = ").append(labelAccount);
			sb.append(" - message = ").append(msgContent);
			logger.debug(sb.toString());
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
