package org.esupportail.smsu.business;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Fonction;
import javax.inject.Inject;

/**
 * Business layer concerning smsu service.
 *
 */
public class FonctionManager {
	
	@Inject private DaoService daoService;
	
	@Inject private ServiceManager serviceManager;
	
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Retrieve all the fonctions defined in smsu database.
	 * @return
	 */
	public Set<String> getAllFonctions() {
		Set<String> result = new HashSet<>();
		for (Fonction fct : daoService.getFonctions()) {
			result.add(fct.getName());
		}
		result.addAll(serviceManager.getAllAddonServicesSendFctn());
		result.addAll(serviceManager.getAllAddonServicesAdhFctn());
		return result;
	}

}
