/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsu.services.client; 

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.UnknownIdentifierMessageException;
import org.esupportail.ws.remote.beans.TrackInfos;


/**
 * The basic implementation of the information remote service.
 */

public class SendTrackClient  {
	
	/**
     * logger.
     */
	private final Logger logger = new LoggerImpl(getClass());

	
	private SendTrack sendTrack;
	
	/**
	 * Bean constructor.
	 */
	public SendTrackClient() {
	
	}

	public TrackInfos getTrackInfos(final Integer msgId) 
				throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException {
		try {
			
			if (logger.isDebugEnabled()) {
				logger.debug("Calling web service getTrackInfo with parameter : \n" + 
					     " - messsage id : " + msgId);
			}
			
			final TrackInfos trackInfos = sendTrack.getTrackInfos(msgId);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Receiving web service response from getTrackInfo, object TrackInfos : \n" + 
					     " - Nb sms sent : " + trackInfos.getNbSentSMS() + "\n" + 
					     " - Nb sms in progress : " + trackInfos.getNbProgressSMS() + "\n" + 
					     " - Nb of recipient : " + trackInfos.getNbDestTotal() + "\n" + 
					     " - Nb of sms in error : " + trackInfos.getNbErrorSMS() + "\n" + 
					     " - Nb of sms in black list : " + trackInfos.getNbDestBlackList() + "\n" + 
					     " - List of phone number in error : " + trackInfos.getListNumErreur() + "\n");
			}
			return trackInfos;
		} catch (UnknownIdentifierApplicationException e1) {
			  throw e1; 	
		} catch (UnknownIdentifierMessageException e2) {
			  throw e2; 	
		}
	}
	
	/**
	 * Stnadard setter used by spring.
	 * @param sendTrack
	 */
	public void setSendTrack(final SendTrack sendTrack) {
		this.sendTrack = sendTrack;
	}
}

