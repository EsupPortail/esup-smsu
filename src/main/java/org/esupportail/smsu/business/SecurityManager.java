package org.esupportail.smsu.business;


import java.util.HashSet;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.services.GroupUtils;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Business layer concerning smsu service.
 *
 */
public class SecurityManager {
	@Autowired private DaoService daoService;
	@Autowired private GroupUtils groupUtils;

	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * Get the user object corresponding to the given login and grant corresponding rights for the user.
	 * @param login: user login
	 * @return an List of Strings objectrepresenting the authenticated user and his rights 
	 * if the authentication is successfull.
	 */
	public Set<String> loadUserRightsByUsername(final String login) {
		Set<String> fonctions = new HashSet<String>();
		logger.debug("parameter login in loadUserRightsByUsername method is: " + login);
		
		for (CustomizedGroup grp : groupUtils.getCustomizedGroups(login)) {
				logger.debug("group label in loadUserRightsByUsername method is: " + grp.getLabel());
				addFonctions(fonctions, grp.getRole().getFonctions());
		}
		return fonctions;
	}

	private void addFonctions(Set<String> fonctions, Set<Fonction> fonctions_to_add) {
		for (Fonction fct : fonctions_to_add) {
				logger.debug("parameter fct in addFonction method is: " + fct);
				fonctions.add(fct.getName());
		}
	}
	
}
