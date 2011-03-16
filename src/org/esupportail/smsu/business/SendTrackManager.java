package org.esupportail.smsu.business;


import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.UnknownIdentifierMessageException;
import org.esupportail.smsu.services.client.SendTrackClient;
import org.esupportail.ws.remote.beans.TrackInfos;

/**
 * @author xphp8691
 *
 */
public class SendTrackManager {


	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link SendTrackClient}.
	 */
	private SendTrackClient sendTrackClient;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());


	//////////////////
	// constructor
	//////////////////
	/**
	 * Bean constructor.
	 */
	public SendTrackManager() {
		super();
	}

	///////////////////////////////////////
	//  Principal method
	//////////////////////////////////////	
	/**
	 * get the number of persons whose the Message sent.
	 * get the number of persons in black list.
	 * get the number of persons received Message.
	 * @param msgId 
	 */
	public TrackInfos getTrackInfos(final Integer msgId) 
				throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException {
		TrackInfos infos;
		
		logger.debug("WS getTrackInfos method begin call");
		//infos = new SendTrackClient().getTrackInfos(msgId);
		  infos = sendTrackClient.getTrackInfos(msgId);	
			
		logger.debug("WS count : " + infos.getNbDestTotal().toString());
		logger.debug("WS sent : " + infos.getNbSentSMS().toString());
		logger.debug("WS progress : " + infos.getNbProgressSMS().toString());
		logger.debug("WS blacklist : " + infos.getNbDestBlackList().toString());
		logger.debug("WS error : " + infos.getNbErrorSMS().toString());
		
		for (String phone : infos.getListNumErreur()) {
	    	logger.debug("WS phone : " + phone);
	    	}
		logger.debug("WS getTrackInfos method end call");
		return 	infos;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of daoService
	//////////////////////////////////////////////////////////////
	/**
	 * @return the daoService.
	 */
	public DaoService getDaoService() {
		return daoService;
	}

	/**
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of sendTrackClient
	//////////////////////////////////////////////////////////////
	/**
	 * @return the sendTrackClient.
	 */
	public SendTrackClient getSendTrackClient() {
		return sendTrackClient;
	}
	
	/**
	 * @param sendTrackClient
	 */
	public void setSendTrackClient(final SendTrackClient sendTrackClient) {
		this.sendTrackClient = sendTrackClient;
	}

	
}
