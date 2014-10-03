/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsu.services.client; 

import java.util.Set;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.services.client.HttpRequestSmsuapiWS;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * The basic implementation of the information remote service.
 */
public class SmsuapiWS  {
	
	@Autowired private HttpRequestSmsuapiWS ws;
	
	private final Logger logger = Logger.getLogger(this.getClass());
		
	public void mayCreateAccountCheckQuotaOk(final Integer nbDest, final String labelAccount) throws HttpException, InsufficientQuotaException {
		logger.info("mayCreateAccountCheckQuotaOk method client send with parameters : " + 
				     " - nbDest = " + nbDest + 
				     " - labelAccount = " + labelAccount);
		ws.mayCreateAccountCheckQuotaOk(labelAccount, nbDest);
	}

	public void sendSMS(final Integer msgId, final Integer perId, 
			final String smsPhone, final String labelAccount, 
			final String msgContent) throws HttpException, InsufficientQuotaException {
		logger.info("SendSms client message : " + 
				     " - message id = " + msgId + 
				     " - sender id = " + perId + 
				     " - recipient phone number = " + smsPhone + 
				     " - user label account = " + labelAccount + 
				     " - message = " + msgContent);
		ws.sendSms(msgId, smsPhone, msgContent, labelAccount, perId);
	}
	/**
	 * @return a string to test the back office connexion.
	 */
	public String testConnexion() {
		try {
			logger.debug("test de la connexion back office");
			return ws.testConnexion(); 
		} catch (Exception e) {
			logger.error(e);
			return "Erreur de connexion au back office smsuapi";
		}
	}

	public TrackInfos getMessageStatus(final Integer msgId) throws HttpException, UnknownMessageIdException {
			logger.info("Calling web service getTrackInfo with parameter : \n" + 
					     " - messsage id : " + msgId);
			
			final TrackInfos trackInfos = ws.messageInfos(msgId);
			
			logger.info("Receiving web service response from getTrackInfo, object TrackInfos : \n" + 
					     " - Nb sms sent : " + trackInfos.getNbSentSMS() + "\n" + 
					     " - Nb sms in progress : " + trackInfos.getNbProgressSMS() + "\n" + 
					     " - Nb of recipient : " + trackInfos.getNbDestTotal() + "\n" + 
					     " - Nb of sms in error : " + trackInfos.getNbErrorSMS() + "\n" + 
					     " - Nb of sms in black list : " + trackInfos.getNbDestBlackList() + "\n" + 
					     " - List of phone number in error : " + trackInfos.getListNumErreur() + "\n");

			return trackInfos;
	}

	public Set<String> getListPhoneNumbersInBlackList() throws HttpException {
		logger.info("getListPhoneNumbersInBlackList method client send ");
		return ws.getListPhoneNumbersInBlackList();
	}

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 * @throws HttpException 
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) throws HttpException {
		logger.info("isPhoneNumberInBlackList method client send with parameter : " + " - phoneNumber = " + phoneNumber);
		return ws.isPhoneNumberInBlackList(phoneNumber);
	}

}
