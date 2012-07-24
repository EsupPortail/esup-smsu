/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.services.client; 

import java.io.Serializable;

import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.UnknownIdentifierMessageException;
import org.esupportail.ws.remote.beans.TrackInfos;



/**
 * The interface of the information remote service.
 */
public interface SendTrack extends Serializable {

	/**
	 * @return list of :
	 *  - the number of SMS recipients.
	 *  - the non-authorized phone numbers (in back list).
	 *  - the number of sent SMS.
	 */
	
	TrackInfos getTrackInfos(Integer msgId) 
			throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException;

}
