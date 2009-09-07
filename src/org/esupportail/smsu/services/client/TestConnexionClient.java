package org.esupportail.smsu.services.client;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.BeanUtils;

/**
 * @author xphp8691
 *
 */
public class TestConnexionClient {
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
		//	logger.debug("récupération du bean");
		//TestConnexion remoteService = (TestConnexion) BeanUtils.getBean(BEAN_NAME);
	
		
			logger.debug("test de la connexion back office");
			
			response = remoteService.testConnexion(); 
		} catch (Exception e) {
			response = "Erreur connexion";
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
