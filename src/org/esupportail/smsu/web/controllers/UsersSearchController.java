package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
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
		
		if (this.ldapUid != null) {
			if (this.ldapUid.length() > 0 ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Recherche d'utilisateurs à partir du token : " + this.ldapUid);
				}
				List<LdapUser> list = ldapUtils.searchConditionFriendlyLdapUsersByToken(
									  this.ldapUid, null);
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
		ldapUsers = new ArrayList<UiRecipient>();
		
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
					}
					recipient = new  SingleUserRecipient(displayName, userId, userId, phone);
					ldapUsers.add(recipient);
				}
				if (ldapUsers.size() == 0) {
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
	
}
