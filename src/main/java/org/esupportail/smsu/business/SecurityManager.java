package org.esupportail.smsu.business;


import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.services.GroupUtils;
import javax.inject.Inject;

/**
 * Business layer concerning smsu service.
 *
 */
public class SecurityManager {
	@Inject private DaoService daoService;
	@Inject private GroupUtils groupUtils;

	private final Logger logger = Logger.getLogger(getClass());

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
		Set<String> fonctions = new HashSet<>();
		logger.debug("parameter login in loadUserRightsByUsername method is: " + login);
		
		for (CustomizedGroup grp : groupUtils.getCustomizedGroups(login)) {
				logger.debug("group label in loadUserRightsByUsername method is: " + grp.getLabel());
				addFonctions(fonctions, grp.getRole().getFonctions());
		}
		if (fonctions.contains("FCTN_GESTIONS_RESPONSABLES") || isSupervisor(login)) {
			fonctions.add("APPROBATION_ENVOI");
		}
		if (!groupUtils.ldapUtils.disabled) {
		    fonctions.add("FCTN_SMS_ADHESION_SERVICE_CG");
		}
		return fonctions;
	}

	private boolean isSupervisor(String login) {
		Person person = daoService.getPersonByLogin(login);
		return person != null && daoService.isSupervisor(person);
	}

	private void addFonctions(Set<String> fonctions, Set<Fonction> fonctions_to_add) {
		for (Fonction fct : fonctions_to_add) {
				logger.debug("parameter fct in addFonction method is: " + fct);
				fonctions.add(fct.getName());
		}
	}
	
}
