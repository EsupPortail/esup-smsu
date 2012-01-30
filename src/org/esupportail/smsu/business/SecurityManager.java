package org.esupportail.smsu.business;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;


/**
 * Business layer concerning smsu service.
 *
 */
public class SecurityManager {

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * ldap service.
	 */
	private LdapUtils ldapUtils;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public SecurityManager() {
		super();
	}

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
		// Retrieve all Groups of User "login"
		logger.debug("parameter login in loadUserRightsByUsername method is: " + login);
		List<UserGroup> groups = ldapUtils.getUserGroupsByUid(login);	
		UserGroup selfGroup = new UserGroup(login, login);
		groups.add(selfGroup);
		
		for (UserGroup group : groups) {
			// 1- Retrieve the DAO CustomizedGroup by "cgr_Label"
			logger.debug("group login in loadUserRightsByUsername method is: " + group.getLdapId());
			CustomizedGroup grp = daoService.getCustomizedGroupByLabel(group.getLdapId());
			// 2 - Retrieve the role associate to the CustomizedGroup, So all these fonctions
			if (grp != null) { 
				logger.debug("group label in loadUserRightsByUsername method is: " + grp.getLabel());
				for (Fonction fct : grp.getRole().getFonctions()) {
					// 3 - add fonctions to "fonctions" ArrayList
					if (!fonctions.contains(fct.getName())) {
						logger.debug("parameter fct in addFonction method is: " + fct);
						fonctions.add(fct.getName());
					}
				}
			}
		}
		return fonctions;
	}

	/**
	 * Get the user object corresponding to the given login and grant corresponding roles for the user.
	 * @param login: user login
	 * @return an List of Strings objectrepresenting the authenticated user and his roles 
	 * if the authentication is successfull.
	 */
	public List<Integer> loadUserRolesByUsername(final String login) {
		List<Integer> roles = new ArrayList<Integer>();
		// Retrieve all Groups of User "login"
		logger.debug("parameter login in loadUserRolesByUsername method is: " + login);
		List<UserGroup> groups = ldapUtils.getUserGroupsByUid(login);	
		UserGroup selfGroup = new UserGroup(login, login);
		groups.add(selfGroup);
		
		for (UserGroup group : groups) {
			// 1- Retrieve the DAO CustomizedGroup by "cgr_Label"
			logger.debug("group login in loadUserRolesByUsername method is: " + group.getLdapId());
			CustomizedGroup grp = daoService.getCustomizedGroupByLabel(group.getLdapId());
			// 2 - Retrieve the role associate to the CustomizedGroup, So all these fonctions
			if (grp != null) { 
				logger.debug("group label in loadUserRolesByUsername method is: " + grp.getLabel());
				Role role = grp.getRole();
				
				if (!roles.contains(role.getId())) {
					logger.debug("parameter role in addRole method is: " + role.getId());
					roles.add(role.getId());
				}
			}
		}
		return roles;
	}

	/**
	 * check if less one of rights belong fonctions.
	 * @param fonctions: list of user fonctions
	 * @param rights: list of required rights
	 */
	public boolean checkRights(final List<String> fonctions, final Set<FonctionName> rights) {
		logger.debug("users rights: " + join(fonctions, " "));
		logger.debug("one the following rights is required: " + join(rights, " "));
		for (FonctionName right : rights) {
			if (fonctions.contains(right.name())) { 
			    logger.debug("checkRights ok: user has right " + right.name());
			    return true; 
			}
		}
		return false;
	}

	public static String join(Iterable elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object o : elements) {
			String s = o.toString();
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}

	////////////////////////////////////////
	//  setter for spring object daoService
	///////////////////////////////////////
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
	

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @return ldapUtils
	 */
	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}
	

}
