/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

//import org.esupportail.commons.services.logging.Logger;
//import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;

/**
 * A bean to manage user preferences.
 */
public class GroupsController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2503649603430502319L;

	/**
	 * A logger.
	 */
	//private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * ldap service.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * The id of the selected group in "search_sms.jsp" page.
	 */	
	private String userGroupId;
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public GroupsController() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userGroupId
	//////////////////////////////////////////////////////////////
	/**
	 * A Getter method for userGroupId parameter. 
	 */
	public String getUserGroupId() {
		return this.userGroupId;
	}
	
	/**
	 * @param String the groupId to setter
	 */
	public void setUserGroupId(final String groupId) {
		this.userGroupId = groupId;
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

	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		return getCurrentUser() != null;
	}

	/**
	 * @return the userGroupItems
	 */
	public List<SelectItem> getUserGroupItems() {
		List<SelectItem> groupItems = new ArrayList<SelectItem>();
		groupItems.clear();
		groupItems.add(new SelectItem("0", ""));
		List<BasicGroup> groups = getDomainService().getGroups();
		if (groups != null) {
		for (BasicGroup grp : groups) {
			//groupItems.add(new SelectItem(grp.getBgrId().toString(), grp.getBgrLabel()));
			String groupDisplayName = ldapUtils.getGroupDisplayName(grp);
			groupItems.add(new SelectItem(grp.getId().toString(), groupDisplayName));
		}
		}
		return groupItems;
	}

	/**
	 * @param ldapUtils the ldapUtils to set
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @return the ldapUtils
	 */
	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}
	
}
