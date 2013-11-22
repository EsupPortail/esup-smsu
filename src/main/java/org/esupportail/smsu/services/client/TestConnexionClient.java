package org.esupportail.smsu.services.client;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

/**
 * @author xphp8691
 *
 */
public class TestConnexionClient {
	@SuppressWarnings("unused")
	private static final String BEAN_NAME = "remoteTestConnexion";
	
	private TestConnexion remoteService;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	public TestConnexionClient() {
		super();
	}


	/**
	 * @return a string to test the back office connexion.
	 */
	public String testConnexion() {
		String response;
		try {
		
			logger.debug("test de la connexion back office");
			
			response = remoteService.testConnexion(); 
		} catch (Exception e) {
			response = "Erreur de connexion au back office smsuapi";
			logger.error(e);
		}
		return response;
	}


	public TestConnexion getRemoteService() {
		return remoteService;
	}


	public void setRemoteService(TestConnexion remoteService) {
		this.remoteService = remoteService;
	}
	
	
}
