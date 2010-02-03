/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.services.ldap.LdapUtils;

import sun.misc.Sort;

/**
 * A bean to manage user preferences.
 */
public class PersonsController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2503649603430502319L;

	/**
	 * const.
	 */
	private static final String NONE = "Aucun";
	
	/**
	 * The id of the selected account in "search_sms.jsp" page.
	 */	
	private String userUserId;
	
	/**
	 * ldap service.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * displayName.
	 */
	private String displayNameAttributeAsString;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public PersonsController() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Acces control method
	//////////////////////////////////////////////////////////////
	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		return getCurrentUser() != null;
	}

	//////////////////////////////////////////////////////////////
	// Principal method
	//////////////////////////////////////////////////////////////
	/**
	 * @return the userUserItems
	 */
	public List<SelectItem> getUserUserItems() {
		List<SelectItem> personItems = new ArrayList<SelectItem>();
		personItems.clear();

		

		List<String> displayNameList = new ArrayList<String>();

		List<LdapUser> ldapUserList = new ArrayList<LdapUser>();


		if (getCurrentUser().getFonctions().contains(FonctionName.FCTN_SUIVI_ENVOIS_ETABL.name())) {
			if (logger.isDebugEnabled()) {
				logger.debug("get user items");
			}
			List<Person> persons = getDomainService().getPersons();
			personItems.add(new SelectItem("0", ""));	


			if (persons != null) {
				for (Person per : persons) {
					if (!displayNameList.contains(per.getLogin())) {
						displayNameList.add(per.getLogin());
					}
				}

				if (!displayNameList.isEmpty()) {
					ldapUserList = ldapUtils.getUsersByUids(displayNameList);
				}

				for (Person per : persons) {
					String displayName = NONE;
					// 1 - Retrieve displayName
					Boolean testVal = true;
					LdapUser ldapUser;
					int i = 0;

					while ((i < ldapUserList.size()) && testVal) {
						ldapUser = ldapUserList.get(i);
						logger.debug("ldapUser.getId is: " + ldapUser.getId());
						logger.debug("per.getLogin is: " + per.getLogin());
						if (ldapUser.getId().equals(per.getLogin())) {
							displayName = ldapUser.getAttribute(displayNameAttributeAsString);
							logger.debug("displayName is: " + displayName);
							testVal = false;
						}
						i++;
					}


					if (displayName.equals(NONE)) {
						displayName = per.getLogin();
					} else {
						displayName = displayName + "  (" + per.getLogin() + ")"; 
					}
					personItems.add(new SelectItem(per.getId().toString(), displayName));
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("end of get user items");
			}
		} else {
			final String perId = getCurrentUser().getId();
			final String displayName = getCurrentUser().getDisplayName();
			
			Person person = getDomainService().getPersonByLogin(perId);
			String id = "noId";
			if (logger.isDebugEnabled()) {
				logger.debug("id set to noId");
			}
			if (person != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("person login = " + person.getLogin());
					logger.debug("person id = " + person.getId().toString());
				}
				
				id = person.getId().toString();
			}
			personItems.add(new SelectItem(id, displayName));
			
/*			if (persons != null) {
				for (Person per : persons) {
					if (getCurrentUser().getId().equals(per.getLogin())) {
						displayNameList.add(per.getLogin());
					}
				}

				if (!displayNameList.isEmpty()) {
					ldapUserList = ldapUtils.getUsersByUids(displayNameList);
				}

				for (Person per : persons) {
					if (getCurrentUser().getId().equals(per.getLogin())) {
						String displayName = NONE;
						// 1 - Retrieve displayName
						Boolean testVal = true;
						LdapUser ldapUser;
						int i = 0;

						while ((i < ldapUserList.size()) && testVal) {
							ldapUser = ldapUserList.get(i);
							logger.debug("ldapUser.getId is: " + ldapUser.getId());
							logger.debug("per.getLogin is: " + per.getLogin());
							if (ldapUser.getId().equals(per.getLogin())) {
								displayName = ldapUser.getAttribute(displayNameAttributeAsString);
								logger.debug("displayName is: " + displayName);
								testVal = false;
							}
							i++;
						}


						if (displayName.equals(NONE)) {
							displayName = per.getLogin();
						} else {
							displayName = displayName + "  (" + per.getLogin() + ")"; 
						}
						personItems.add(new SelectItem(per.getId().toString(), displayName));
					}	
				}
			}
*/
			
			
		}

		return personItems;
	}

	//////////////////////////////////////////////////////////////
	// Others
	//////////////////////////////////////////////////////////////
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	//////////////////////////////////////////////////////////////
	// Setter of ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userUserId
	//////////////////////////////////////////////////////////////
	/**
	 * A Getter method for userUserId parameter. 
	 */
	public String getUserUserId() {
		return this.userUserId;
	}
	
	/**
	 * @param String the userId to setter
	 */
	public void setUserUserId(final String userId) {
		this.userUserId = userId;
	}
	
	/**
	 * A Getter method for displayNameAttributeAsString parameter. 
	 */
	public String getDisplayNameAttributeAsString() {
		return displayNameAttributeAsString;
	}

	/**
	 * @param String the displayNameAttributeAsString to setter
	 */
	public void setDisplayNameAttributeAsString(final String displayNameAttributeAsString) {
		this.displayNameAttributeAsString = displayNameAttributeAsString;
	}

}
