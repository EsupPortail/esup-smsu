package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.mail.search.RecipientStringTerm;

import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.SingleUserRecipient;
import org.esupportail.smsu.web.beans.UIPerson;
import org.esupportail.smsu.web.beans.UiRecipient;


/**
 * @author xphp8691
 *
 */
public class UsersSearchController extends AbstractContextAwareController {

	/**
	 * the serial version UID.
	 */
	private static final long serialVersionUID = -4589875505982426308L;

	/**
	 * the serial version UID.
	 */
	private static final long NB_MIN_CHARS_FOR_LDAP_SEARCH = 4;
	/**
	 * ldapUid.
	 */
	private String ldapUid;
	
	/**
	 * the ldap service.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * list of ldapUsers.
	 */
	private List<UiRecipient> ldapUsers;
	
	/**
	 * list of ldapUsers get from a user ldap request
	 */
	private List<UiRecipient> ldapRequestUsers;
	
	/**
	 * list of valid ldapUsers get from a user ldap request
	 */
	private List<UiRecipient> ldapValidUsers;
 
	/**
	 * list of ldapUsers for Person.
	 */
	private List<UIPerson> ldapPersons;

	/**
	 * ldap filter.
	 */
	private String ldapFilter;
	
	/**
	 * user pager ldap attribute.
	 */
	private String userPagerAttribute; 
	
	/**
	 * user display name ldap attribute.
	 */
	private String userDisplayName;
	
	/**
	 * the service chosen by the user
	 */
	private String service;
	
	/**
	 * number of user with a phone number in the user LDAP request result.
	 */
	private Integer nbAvailableUsersInTheList = 0;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Constructor.
	 */
	public UsersSearchController() {
		super();
		ldapUsers = new ArrayList<UiRecipient>();
	}

	
	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * @return null
	 */
	public String searchUser() {

		ldapUsers = new ArrayList<UiRecipient>();
		logger.debug("SERVICE : " + service);
		if (this.ldapUid != null) {
			if (this.ldapUid.length() > 0 ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Recherche d'utilisateurs à partir du token : " + this.ldapUid);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Chosen service ? => " + service);
				}
				String serviceKey = null;
				
				if (!service.equals("none")) {
					if (logger.isDebugEnabled()) {
						logger.debug("Chosen service id : " + service);
					}
					Integer serviceId = Integer.valueOf(service);
					Service serviceFromDb = getDomainService().getServiceById(serviceId);
					serviceKey = serviceFromDb.getKey();

				}

				List<LdapUser> list = ldapUtils.searchConditionFriendlyLdapUsersByToken(
									  this.ldapUid, serviceKey);
				String displayName;
				String phone;
				String userId;
				SingleUserRecipient recipient;
				
				
				for (LdapUser user : list) {
					userId = user.getId();
					displayName = user.getAttribute(userDisplayName) + " (" + user.getId() + ")";
					phone = user.getAttribute(userPagerAttribute);
					if (logger.isDebugEnabled()) {
						logger.debug("ajout de la personne : uid =" + userId 
								+ " displayName=" + displayName 
								+ " phone=" + phone + " à la liste");
					}
					recipient = new  SingleUserRecipient(displayName, userId, userId, phone);
					ldapUsers.add(recipient);
				}
				if (ldapUsers.size() == 0) {
					addInfoMessage("formGeneral:ldapUid", "SENDSMS.MESSAGE.NOUSERFOUND");
				}
			} 
		}
		return null;
	}
	
	/**
	 * @return null
	 */
	public String searchLdap() {

		ldapPersons = new ArrayList<UIPerson>();
		
		if (this.ldapUid != null) {
			if (this.ldapUid.trim().length() >= NB_MIN_CHARS_FOR_LDAP_SEARCH ) {
				List<LdapUser> list = ldapUtils.searchLdapUsersByToken(this.ldapUid);
				String displayName;
				String userId;
				UIPerson person;
				
				
				for (LdapUser user : list) {
					userId = user.getId();
					displayName = user.getAttribute(userDisplayName) + " (" + user.getId() + ")";
					
					person = new  UIPerson(displayName, userId);
					ldapPersons.add(person);
				}
			} 
		}
		return null;
	}
	
	/**
	 * @return null
	 */
	public String searchLdapWithFilter() {
		ldapValidUsers = new LinkedList<UiRecipient>();
		ldapRequestUsers = new LinkedList<UiRecipient>();
		
		if (this.ldapFilter != null) {
			if (this.ldapFilter.length() > 0 ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Exécution de la requete utilisateur : " + this.ldapFilter);
				}
				List<LdapUser> list = new ArrayList<LdapUser>();
				try {
					list = ldapUtils.searchLdapUsersByFilter(this.ldapFilter);
				} catch (LdapException  e) {
					final StringBuffer buff = new StringBuffer();
					buff.append("Erreur lors de l'exécution de la requete : [");
					buff.append(this.ldapFilter);
					buff.append("]");
					logger.error(buff.toString(), e);
					addErrorMessage(null, "SENDSMS.MESSAGE.LDAPREQUESTERROR");
				}
				String displayName;
				String userId;
				String phone;
				nbAvailableUsersInTheList = 0;
				SingleUserRecipient recipient;
				
				for (LdapUser user : list) {
					userId = user.getId();
					phone = user.getAttribute(userPagerAttribute);
					displayName = user.getAttribute(userDisplayName) + " (" + user.getId() + ")";
					logger.debug(userId + " : " + displayName);
					
					if (logger.isDebugEnabled()) {
						logger.debug("ajout de la personne : uid =" + userId 
								+ " displayName=" + displayName 
								+ " phone=" + phone + " à la liste");
					}
					
					if (phone == null) {
						displayName = displayName.concat(" ".concat(this.getI18nService().
								getString("SENDSMS.MESSAGE.UNSELECTABLEUSER")));
						recipient = new  SingleUserRecipient(displayName, userId, userId, phone);
					} else {
						nbAvailableUsersInTheList++;
						recipient = new  SingleUserRecipient(displayName, userId, userId, phone);
						ldapValidUsers.add(recipient);
					}
					ldapRequestUsers.add(recipient);
				}
				if (ldapRequestUsers.size() == 0) {
					addInfoMessage("formGeneral:ldapFilter", "SENDSMS.MESSAGE.NOUSERFOUND");
				}
			}
		}
		return null;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of userPagerAttribute
	//////////////////////////////////////////////////////////////
	/**
	 * @param userPagerAttribute
	 */
	public void setUserPagerAttribute(final String userPagerAttribute) {
		this.userPagerAttribute = userPagerAttribute;
	}

	/**
	 * @return userPagerAttribute
	 */
	public String getUserPagerAttribute() {
		return userPagerAttribute;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userDisplayName
	//////////////////////////////////////////////////////////////
	/**
	 * @param userDisplayName
	 */
	public void setUserDisplayName(final String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	/**
	 * @return userDisplayName
	 */
	public String getUserDisplayName() {
		return userDisplayName;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapPersons
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapPersons the ldapPersons to set
	 */
	public void setLdapPersons(final List<UIPerson> ldapPersons) {
		this.ldapPersons = ldapPersons;
	}

	/**
	 * @return the ldapPersons
	 */
	public List<UIPerson> getLdapPersons() {
		return ldapPersons;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUid
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUid
	 */
	public void setLdapUid(final String ldapUid) {
		this.ldapUid = ldapUid;
	}

	/**
	 * @return ldapUid
	 */
	public String getLdapUid() {
		return ldapUid;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUsers
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUsers
	 */
	public void setLdapUsers(final List<UiRecipient> ldapUsers) {
		this.ldapUsers = ldapUsers;
	}

	/**
	 * @return ldapUsers
	 */
	public List<UiRecipient> getLdapUsers() {
		return ldapUsers;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapFilter
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapFilter
	 */
	public void setLdapFilter(final String ldapFilter) {
		this.ldapFilter = ldapFilter;
	}

	/**
	 * @return ldapFilter
	 */
	public String getLdapFilter() {
		return ldapFilter;
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

	/**
	 * @return nbAvailableUsersInTheList
	 */
	public Integer getNbAvailableUsersInTheList() {
		return nbAvailableUsersInTheList;
	}


	/**
	 * @param nbAvailableUsersInTheList
	 */
	public void setNbAvailableUsersInTheList(final Integer nbAvailableUsersInTheList) {
		this.nbAvailableUsersInTheList = nbAvailableUsersInTheList;
	}


	/**
	 * @param ldapRequestUsers
	 */
	public void setLdapRequestUsers(final List<UiRecipient> ldapRequestUsers) {
		this.ldapRequestUsers = ldapRequestUsers;
	}


	/**
	 * @return ldapRequestUsers
	 */
	public List<UiRecipient> getLdapRequestUsers() {
		return ldapRequestUsers;
	}


	/**
	 * @param ldapValidUsers
	 */
	public void setLdapValidUsers(final List<UiRecipient> ldapValidUsers) {
		this.ldapValidUsers = ldapValidUsers;
	}


	/**
	 * @return ldapValidUsers
	 */
	public List<UiRecipient> getLdapValidUsers() {
		return ldapValidUsers;
	}


	/**
	 * setter for the service
	 * @param service
	 */
	public void setService(final String service) {
		this.service = service;
	}


	/**
	 * 
	 * @return the current service
	 */
	public String getService() {
		return service;
	}
}
