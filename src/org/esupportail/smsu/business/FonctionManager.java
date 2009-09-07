package org.esupportail.smsu.business;

import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Fonction;

/**
 * Business layer concerning smsu service.
 *
 */
public class FonctionManager {
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public FonctionManager() {
		super();
	}
	
	///////////////////////////////////////
	//  Principal method
	//////////////////////////////////////
	/**
	 * Retrieve all the fonctions defined in smsu database.
	 * @return
	 */
	public List<Fonction> getAllFonctions() {
		if (logger.isDebugEnabled()) {
			logger.debug("Begin Retrieve the smsu fonctions from the database");
		}
		
		List<Fonction> allFonctions = daoService.getFonctions();
		
		if (logger.isDebugEnabled()) {
			logger.debug("End Retrieve the smsu fonctions from the database");
		}
		return allFonctions;
	}

	/////////////////////////////////////////
	//  setter for spring object daoService
	////////////////////////////////////////
	/**
	 * @param daoService 
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}





}
