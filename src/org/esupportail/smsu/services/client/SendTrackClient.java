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
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Calling web service getTrackInfo with parameter : \n");
				sb.append(" - messsage id : ").append(msgId);
				logger.debug(sb.toString());
			}
			
			final TrackInfos trackInfos = sendTrack.getTrackInfos(msgId);
			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Receiving web service response from getTrackInfo, object TrackInfos : \n");
				sb.append(" - Nb sms sent : ").append(trackInfos.getNbSentSMS()).append("\n");
				sb.append(" - Nb sms in progress : ").append(trackInfos.getNbProgressSMS()).append("\n");
				sb.append(" - Nb of recipient : ").append(trackInfos.getNbDestTotal()).append("\n");
				sb.append(" - Nb of sms in error : ").append(trackInfos.getNbErrorSMS()).append("\n");
				sb.append(" - Nb of sms in black list : ").append(trackInfos.getNbDestBlackList()).append("\n");
				sb.append(" - List of phone number in error : ").append(trackInfos.getListNumErreur()).append("\n");
				logger.debug(sb.toString());
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

