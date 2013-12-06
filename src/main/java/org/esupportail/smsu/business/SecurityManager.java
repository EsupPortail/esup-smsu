package org.esupportail.smsu.business;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Business layer concerning smsu service.
 *
 */
public class SecurityManager {
	@Autowired private DaoService daoService;

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
	public List<String> loadUserRightsByUsername(final String login) {
		List<String> fonctions = new ArrayList<String>();
		logger.debug("parameter login in loadUserRightsByUsername method is: " + login);
		
		for (CustomizedGroup grp : getCustomizedGroups(login)) {
				logger.debug("group label in loadUserRightsByUsername method is: " + grp.getLabel());
				addFonctions(fonctions, grp.getRole().getFonctions());
		}
		return fonctions;
	}

	private void addFonctions(List<String> fonctions, Set<Fonction> fonctions_to_add) {
		for (Fonction fct : fonctions_to_add) {
			if (!fonctions.contains(fct.getName())) {
				logger.debug("parameter fct in addFonction method is: " + fct);
				fonctions.add(fct.getName());
			}
		}
	}

	private List<UserGroup> getUserGroupsPlusSelfGroup(String login) {
		List<UserGroup> groups = new ArrayList<UserGroup>();
		try {
		    //TODO groups = ldapUtils.getUserGroupsByUid(login);
		} catch (Exception e) {
		    logger.debug("" + e, e); // nb: exception already logged in SmsuCachingUportalServiceImpl
		    // go on, things can still work using only the self group
		}
		UserGroup selfGroup = new UserGroup(login, login);
		groups.add(selfGroup);
		return groups;
	}
	
	private List<CustomizedGroup> getCustomizedGroups(String login) {
		List<CustomizedGroup> l = new ArrayList<CustomizedGroup>();

		for (UserGroup group : getUserGroupsPlusSelfGroup(login)) {
			logger.debug("group login is: " + group.getLdapId());
			CustomizedGroup grp = daoService.getCustomizedGroupByLabel(group.getLdapId());
			if (grp != null) l.add(grp);
		}
		return l;
	}
	
}
