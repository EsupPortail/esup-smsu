package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapGroup;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.WriteableLdapUserService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.portal.ws.client.PortalGroup;
import org.esupportail.portal.ws.client.PortalGroupHierarchy;
import org.esupportail.portal.ws.client.PortalService;
import org.esupportail.portal.ws.client.exceptions.PortalErrorException;
import org.esupportail.portal.ws.client.exceptions.PortalGroupNotFoundException;
import org.esupportail.portal.ws.client.exceptions.PortalUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.groups.SmsuLdapGroupPersonAttributeDaoImpl;
import org.esupportail.smsu.groups.SmsuLdapPersonAttributeDaoImpl;
import org.esupportail.smsu.groups.pags.SmsuPersonAttributesGroupStore.GroupDefinition;
import org.esupportail.smsu.groups.pags.SmsuPersonAttributesGroupStore.TestGroup;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.jasig.portal.groups.pags.testers.BaseAttributeTester;
import org.springframework.ldap.support.filter.AndFilter;
import org.springframework.ldap.support.filter.EqualsFilter;
import org.springframework.ldap.support.filter.Filter;
import org.springframework.ldap.support.filter.OrFilter;



/**
 * @author PRQD8824
 *
 */
public class LdapUtils {

	/**
	 * a logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * used to manage user and group (read only).
	 */
	private SearchableLdapUserAndGroupServiceSMSUImpl ldapUserAndGroupService;
	
	/**
	 * used to manage user (write only).
	 */
	private WriteableLdapUserService writeableLdapUserService;

	/**
	 * used to manage group (read only).
	 */
	private PortalService portalService;

	/**
	 * the attribute pager name in the ldap.
	 */
	private String userPagerAttribute;
	
	/**
	 * The attribute terms of use in the ldap.
	 */
	private String userTermsOfUseAttribute;
	
	
	/**
	 * The key used to represent the CG in the ldap (up1terms).
	 */
	private String cgKeyName;
	
	/**
	 * 
	 */
	private SmsuLdapGroupPersonAttributeDaoImpl smsuLdapGroupPersonAttributeDaoImpl;
	
	private SmsuLdapPersonAttributeDaoImpl smsuLdapPersonAttributeDaoImpl;
	
	public LdapUtils() {
		
	}
	
	/**
	 * Return the ldap user by this id.
	 * @param ldapUserUid 
	 * @return the ldapuser 
	 * @throws LdapException
	 * @throws LdapUserNotFoundException if the user is not found in the ldap 
	 */
	public LdapUser getLdapUserByUserUid(final String ldapUserUid) 
			throws LdapUserNotFoundException, LdapException {
		
		LdapUser ldapUser = null;
		try {
			ldapUser = ldapUserAndGroupService.getLdapUser(ldapUserUid);
		} catch (UserNotFoundException e) {
			final String messageStr = 
			    "Unable to find the user with id : [" + ldapUserUid + "]";
			logger.debug(messageStr, e);
			throw new LdapUserNotFoundException(messageStr, e);
		}
		
		return ldapUser;	
	}
	
	/**
	 * Return the display name of the specified user.
	 * @param uid
	 * @return
	 */
	public String getUserDisplayNameByUserUid(final String uid) throws LdapUserNotFoundException {
		final String retVal = ldapUserAndGroupService.getUserDisplayNameByUid(uid);
		return retVal;
	}
	
	
	/**
	 * Return the first name of the specified user.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public String getUserFirstNameByUid(final String uid) throws LdapUserNotFoundException {
		final String firstName = ldapUserAndGroupService.getUserFirstNameByUid(uid);
		return firstName;
	}
	
	/**
	 * Return the last name of the specified user.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public String getUserLastNameByUid(final String uid) throws LdapUserNotFoundException {
		final String lastName = ldapUserAndGroupService.getUserLastNameByUid(uid);
		return lastName;
	}
	
	
	/**
	 * Get the user email adress from the ldap.
	 * @param uid
	 * @param name
	 * @return
	 */
	public String getUserEmailAdressByUid(final String uid) throws LdapUserNotFoundException {
		final String retVal = ldapUserAndGroupService.getUserEmailAdressByUid(uid);
		return retVal;
	}
	
	/**
	 * Get the pager user from the ldap.
	 * @param uid
	 * @param name
	 * @return
	 */
	public String getUserPagerByUid(final String uid) throws LdapUserNotFoundException {
		final String retVal = ldapUserAndGroupService.getUserPagerByUid(uid);
		return retVal;
	}
	
	/**
	 * the termOfUse user from the ldap.
	 * @param uid
	 * @return
	 */
	private List<String> getUserTermsOfUseByUid(final String uid) throws LdapUserNotFoundException {
		final List<String> retVal = ldapUserAndGroupService.getUserTermsOfUse(uid);
		return retVal;
	}
	
	/**
	 * Used to set the pager attribute of a specified user.
	 * @param uid
	 * @param pagerValue
	 * @throws LdapUserNotFoundException if the user is not found in the ldap 
	 */
	public void setUserPagerByUid(final String uid, final String pagerValue) throws LdapUserNotFoundException {
		final LdapUser ldapUser = getLdapUserByUserUid(uid);
		// flush all paramter to update only specified attributes
		ldapUser.getAttributes().clear();
		// create pager attribute
		final List<String> pagerValueList = new LinkedList<String>();
		pagerValueList.add(pagerValue);
		ldapUser.getAttributes().put(userPagerAttribute, pagerValueList);
		// update the user
		writeableLdapUserService.updateLdapUser(ldapUser);
	}
	
	/**
	 * Clear the pager attribute for the specified user.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 */
	public void clearUserPager(final String uid) throws LdapUserNotFoundException {
		clearLdapAttributeByUidAndName(uid, userPagerAttribute);
	}
	
	/**
	 * used to set the terms of use of a specified user.
	 * @param uid
	 * @param termsOfUseValue
	 * @throws LdapUserNotFoundException if the user is not found in the ldap
	 */
	private void setUserTermsOfUse(final String uid, 
			final List<String> termsOfUseValue) 
			throws LdapUserNotFoundException {
		final LdapUser ldapUser = getLdapUserByUserUid(uid);
		// flush all paramter to update only specified attributes
		ldapUser.getAttributes().clear();
		ldapUser.getAttributes().put(userTermsOfUseAttribute, termsOfUseValue);
		// update the user
		writeableLdapUserService.updateLdapUser(ldapUser);
	}
	
	

	/**
	 * Clear the term of use attribute for the specified user.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 */
	private void clearUserTermsOfUse(final String uid) throws LdapUserNotFoundException {
		clearLdapAttributeByUidAndName(uid, userTermsOfUseAttribute);
	}
	
	
	/**
	 * Clear a user specified attribute. 
	 * @param uid
	 * @param name
	 * @throws LdapUserNotFoundException
	 */
	private void clearLdapAttributeByUidAndName(final String uid, final String name) 
					throws LdapUserNotFoundException {
		final LdapUser ldapUser = getLdapUserByUserUid(uid);
		ldapUser.getAttributes().clear();
		ldapUser.getAttributes().put(name, null);
		writeableLdapUserService.updateLdapUser(ldapUser);
	}
	
		
	/**
	 * Get the ldap user list of the specified group.
	 * @param LdapGroupId
	 * @return hte user list of the specified group, or null if no group found
	 */
	/*public List<LdapUser> getLdapUsersByGroupId(final String ldapGroupId) 
	 * throws LdapGroupNotFoundException, LdapException {
		
		final LdapGroup ldapGroup = getLdapGroupByGroupId(ldapGroupId);
		final List<LdapUser> ldapUserList = ldapUserAndGroupService.getMembers(ldapGroup);
		
		return ldapUserList;
	}*/
	
	
	/**
	 * Get the ldap with the specified id.
	 * @param LdapGroupId
	 * @return
	 * @throws LdapException
	 * @throws LdapGroupNotFoundException if the group is not found
	 */
/*	public LdapGroup getLdapGroupByGroupId(final String ldapGroupId) 
 * 						throws LdapGroupNotFoundException, LdapException {
		LdapGroup ldapGroup = null;
		try {
			ldapGroup = ldapUserAndGroupService.getLdapGroup(ldapGroupId);
		} catch (GroupNotFoundException e) {
			final String messageStr = "Unable to find the group with id : [" + ldapGroupId + "]";
			logger.warn(messageStr, e);
			throw new LdapGroupNotFoundException(messageStr, e);
		}
		return ldapGroup;
	}*/

	/**
	 * 
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public boolean isGeneralConditionValidateByUid(final String uid) throws LdapUserNotFoundException {
		boolean retVal = isSpecificConditionValidateByUidAndSpecificConditionKey(uid, cgKeyName);
		return retVal;
	}
	
	
	/**
	 * Add the general condition flag in the ldap.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 */
	public void addGeneralConditionByUid(final String uid) throws LdapUserNotFoundException {
		addSpecificConditionByUidAndSpecificConditionKey(uid, cgKeyName);
	}
	
	/**
	 * Remove the general condition flag in the ldap.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 */
	public void removeGeneralConditionByUid(final String uid) throws LdapUserNotFoundException {
		removeSpecificConditionByUidAndSpecificConditionKey(uid, cgKeyName);
	}
	
	/**
	 * test if the specific condition is in the ldap.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public boolean isSpecificConditionValidateByUidAndSpecificConditionKey(final String uid, 
						final String specificConditionKey) 
						throws LdapUserNotFoundException {
		boolean retVal = false;
		final List<String> termsOfuse = getUserTermsOfUseByUid(uid);
		if (termsOfuse != null) {
			for (String termOfUse : termsOfuse) {
				if (specificConditionKey.equalsIgnoreCase(termOfUse)) {
					retVal = true;
				}
			}
		}
		
		return retVal;
	}
	
	
	/**
	 * test if the specific condition is in the ldap.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public List<String> getSpecificConditionValidateByUid(final String uid) 
	throws LdapUserNotFoundException {
		List<String> termsOfuse = getUserTermsOfUseByUid(uid);
		if (termsOfuse != null) {
			termsOfuse.remove(cgKeyName);
		}	
		return termsOfuse;
	}
	

	/**
	 * Update the list of specific conditions for the given user.
	 * @param uid
	 * @param newListSP
	 * @throws LdapUserNotFoundException 
	 */
	public void updateAllSpecificConditionByUid(final String uid,
			final List<String> newListSP) throws LdapUserNotFoundException {
		// retrieve the list of specific conditions stored in the LDAP
		final List<String> originalTermsOfuse = getSpecificConditionValidateByUid(uid);
		// add the new specific conditions not already stored in the LDAP
		for (String newSP : newListSP) {
			if (!originalTermsOfuse.contains(newSP)) {
				addSpecificConditionByUidAndSpecificConditionKey(uid, newSP);
			}
		}
		// remove the specific conditions from the LDAP that are not anymore in the list of the user
		for (String oldSP : originalTermsOfuse) {
			if (!newListSP.contains(oldSP)) {
				removeSpecificConditionByUidAndSpecificConditionKey(uid, oldSP);
			}
		}
	}
	
	/**
	 * Remove the list of specific conditions for the given user.
	 * @param uid
	 * @throws LdapUserNotFoundException 
	 */
	public void removeAllSpecificConditionByUid(final String uid) throws LdapUserNotFoundException {
		// retrieve the list of specific conditions stored in the LDAP
		final List<String> originalTermsOfuse = getSpecificConditionValidateByUid(uid);
		// remove the specific conditions from the LDAP 
		for (String oldSP : originalTermsOfuse) {
			removeSpecificConditionByUidAndSpecificConditionKey(uid, oldSP);
		}
	}

	
	/**
	 * Add a specific condition in the ldap.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 */
	public void addSpecificConditionByUidAndSpecificConditionKey(final String uid, 
			 			final String specificConditionKey)  
			 			throws LdapUserNotFoundException {
		final List<String> termsOfuse = getUserTermsOfUseByUid(uid);
		
		if (termsOfuse != null) {
			if (!termsOfuse.contains(specificConditionKey)) {
				termsOfuse.add(specificConditionKey);
				clearUserTermsOfUse(uid);
				setUserTermsOfUse(uid, termsOfuse);
			}
		}
	}
	
	
	/**
	 * Remove a specific condition in the ldap.
	 * @param uid
	 * @param specificConditionKey
	 * @throws LdapUserNotFoundException
	 */
	public void removeSpecificConditionByUidAndSpecificConditionKey(final String uid, 
								final String specificConditionKey) 
								throws LdapUserNotFoundException {
		final List<String> termsOfuse = getUserTermsOfUseByUid(uid);
		
		if (termsOfuse != null) {
			if (termsOfuse.contains(specificConditionKey)) {
				termsOfuse.remove(specificConditionKey);
				clearUserTermsOfUse(uid);
				setUserTermsOfUse(uid, termsOfuse);
			}
		}
	}
	
	/**
	 * Search users by token.
	 * @param token
	 * @return a list of users
	 */
	public List<LdapUser> searchLdapUsersByToken(final String token) {
		final List<LdapUser> userList = ldapUserAndGroupService.getLdapUsersFromToken(token);
		return userList;
	}
	
	/**
	 * @param filter
	 * @return a list of users
	 * @throws LdapException 
	 */
	public List<LdapUser> searchLdapUsersByFilter(final String filter) throws LdapException {
		final List<LdapUser> userList = ldapUserAndGroupService.getLdapUsersFromFilter(filter);
		return userList;
	}
	/**
	 * Search users by token.
	 * @param token
	 * @return a list of users
	 */
	public List<LdapUser> searchLdapUsersByPhoneNumber(final String token) {
		final List<LdapUser> userList = ldapUserAndGroupService.getLdapUsersFromPhoneNumber(token);
		return userList;
	}
	
	/**
	 * Search filtered users by token.
	 * @param token
	 * @return a list of users
	 */
	public List<LdapUser> searchConditionFriendlyLdapUsersByToken(final String token, final String service) {
		final List<LdapUser> userList = ldapUserAndGroupService.getConditionFriendlyLdapUsersFromToken(
										token, cgKeyName, service);
		return userList;
	}

	/**
	 * @param uids
	 * @param service
	 * @return a list of users
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromUid(final List<String> uids, final String service) {
		final List<LdapUser> userList = ldapUserAndGroupService.getConditionFriendlyLdapUsersFromUid(
										uids, cgKeyName, service);
		return userList;
	}

	
	/**
	 * Retrieve all the groups of a given user.
	 * @param uid : user identifier in the LDAP
	 * @return the list of user group
	 */
	public List<UserGroup> getUserGroupsByUid(final String uid)
	throws PortalErrorException, PortalUserNotFoundException {
		List<PortalGroup> portalGroups = portalService.getUserGroups(uid);
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		for (PortalGroup portalGroup : portalGroups) {
			UserGroup userGroup = convertToUserGroup(portalGroup);
			userGroups.add(userGroup);
		}
		return userGroups;
	}
	
	/**
	 * Retrieve the group by id.
	 * @param uid : group identifier in the LDAP
	 * @return the group name
	 */
	public String getGroupNameByUid(final String uid) {
		String retVal;
		try {
		PortalGroup portalGroup = portalService.getGroupById(uid);
		if (portalGroup != null) {
			retVal = portalGroup.getName();
		} else {
			retVal = uid;
		}
		} catch (PortalGroupNotFoundException e) {
			retVal = uid;
		}
		return retVal;
	}
	
	/**
	 * Retrieve all the groups of a given user.
	 * @param uid : user identifier in the LDAP
	 * @return the list of user group
	 */
	public List<UserGroup> searchGroupsByName(final String uid)
	throws PortalErrorException, PortalUserNotFoundException {
		List<PortalGroup> portalGroups = portalService.searchGroupsByName(uid);
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		for (PortalGroup portalGroup : portalGroups) {
			UserGroup userGroup = convertToUserGroup(portalGroup);
			userGroups.add(userGroup);
		}
		return userGroups;
	}
	
	/**
	 * @param uids
	 * @return a list of user mails.
	 */
	public List<String> getUserEmailsAdressByUids(final List<String> uids) {
		List<String> retVal = ldapUserAndGroupService.getUserMailsByUids(uids);
		return retVal;
	}
	/**
	 * @param groupName
	 * @return the corresponding portal group.
	 */
	public PortalGroup getPortalGroupByName(final String groupName) {
		PortalGroup pGroup = portalService.getGroupByName(groupName);
		return pGroup;
	}
	
	/**
	 * @param groupId
	 * @return the parent group
	 */
	public String getParentGroupIdByGroupId(final String groupId) {
		try {
		List<PortalGroup> containingGroups = portalService.getContainingGroupsById(groupId);
		List<String> parentGroupIds = new ArrayList<String>();
		for (PortalGroup group : containingGroups)  {
			if (logger.isDebugEnabled()) {
				logger.debug("Parent group : [" + group.getName() + "] found");
			}
			parentGroupIds.add(group.getId());
		}
		String parentGroupId = null;
		if (parentGroupIds.size() > 0) {
			parentGroupId = parentGroupIds.get(0);
		}
		return parentGroupId;
		} catch (Exception e) {
			List<PortalGroup> userGroups = portalService.getUserGroups(groupId);
			PortalGroup rootGroup = portalService.getRootGroup();
			Integer position = isPortalGroupInList(userGroups, rootGroup);
			if (position != null) {
				userGroups.remove(position.intValue());
			}
			PortalGroup leafGroup = getLeafGroup(rootGroup, userGroups);
			if (logger.isDebugEnabled()) {
				logger.debug("Leaf found : " + leafGroup.getId() + ":" + leafGroup.getName());
			}
			return leafGroup.getId();

		}
	}
	
	private PortalGroup getLeafGroup(final PortalGroup parentGroup, final List<PortalGroup> userGroups) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting leaf groups for [" 
					+ parentGroup.getId() + ":" + parentGroup.getName() + "]");
			for (PortalGroup g : userGroups) {
				logger.debug("UserGroup [" + g.getId() + ":" + g.getName() + "]");
			}
		}
		List<PortalGroup> subGroups = portalService.getSubGroups(parentGroup);
		if (logger.isDebugEnabled()) {
			for (PortalGroup g : subGroups) {
				logger.debug("Subgroup : [" + g.getId() + ":" + g.getName() + "]");
			}
		}
		PortalGroup childGroup = parentGroup;
		Iterator<PortalGroup> i = subGroups.iterator();
		Boolean newLeaf = false;
		while (i.hasNext() && !newLeaf) {
			PortalGroup subGroup = i.next();
			Integer position = isPortalGroupInList(userGroups, subGroup); 
			if (position != null) {
				newLeaf = true;
				if (logger.isDebugEnabled()) {
					logger.debug("New node found : [" + subGroup.getId() + ":"  
							+ subGroup.getName() + "] on position " + position.toString());
				}
				List<PortalGroup> gList = new ArrayList<PortalGroup>();
				gList.addAll(userGroups);
				gList.remove(position.intValue());
				if (gList.size() > 1) {
					childGroup = getLeafGroup(subGroup, gList);
				} else {
					childGroup = gList.get(0);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("SubGroup not found in user groups : [" + subGroup.getId() 
							+ ":" + subGroup.getName() + "]");
				}
			}
		}
		return childGroup;
	}
	
	/**
	 * @param listGroup
	 * @param groupToSearch
	 * @return null or the position in the list.
	 */
	Integer isPortalGroupInList(final List<PortalGroup> listGroup, final PortalGroup groupToSearch) {
		Integer position = null;
		
		Integer listSize = listGroup.size();
		Integer i = 0;
		while (i < listSize && position == null) {
			PortalGroup group = listGroup.get(i);
			if (group.getId().equals(groupToSearch.getId()) 
					&& group.getName().equals(groupToSearch.getName())) {
				position = i;
			}
			i++;
		}
		
		return position;
	}
	/**
	 * Retrieve the parent group of a given group or a given user.
	 * @param groupId identifier of the group or the user
	 * @return a UserGroup if it  exists
	 * 
	public UserGroup getParentGroupByGroupId(final String groupId)
	throws PortalErrorException, PortalUserNotFoundException, PortalGroupNotFoundException {
		UserGroup result = null;
		final PortalGroup portalGroup = portalService.getGroupById(groupId);
		UserGroup formattedGroup = convertToUserGroup(portalGroup);
		PortalGroup rootGroup = portalService.getRootGroup();
		UserGroup formattedRootGroup = convertToUserGroup(rootGroup);
		if (formattedGroup.equals(formattedRootGroup)) {
			result = null;
		} else {
			List<UserGroup> parentGroups = getParentsGroupByGroupId(groupId);
			if ((parentGroups == null) || (parentGroups.isEmpty())) {
				result = null;	
			} else if (parentGroups.size() == 1) {
				result = parentGroups.get(0);
			} else {
				// detect the first parent
				Set<UserGroup> groupsTmp = new HashSet<UserGroup>();
				for (UserGroup group : parentGroups) {
					String currentGroupId = group.getLdapId();
					List<UserGroup> currentGroups = getParentsGroupByGroupId(currentGroupId);
					groupsTmp.addAll(currentGroups);
				}
				parentGroups.removeAll(groupsTmp);
				if (parentGroups.size() == 1) {
					Iterator<UserGroup> iterator = parentGroups.iterator();
					result = iterator.next();
				} else {
					result = null;	
				}
			}
		}
		return result;
	}
	 */

	/**
	 * convert the portalGroup to a user Group.
	 * @param group
	 * @return
	 */
	private UserGroup convertToUserGroup(final PortalGroup portalGroup) {
		final String idGroup = portalGroup.getId();
		final String nameGroup = portalGroup.getName();
		UserGroup formattedGroup = new UserGroup(idGroup, nameGroup);
		return formattedGroup;
	}
	
	/**
	 * Retrieve the parent groups of a given group or a given user.
	 * @param id identifier of the group or the user
	 * @return a list of UserGroup
	 */
	@SuppressWarnings("unused")
	private  List<UserGroup> getParentsGroupByGroupId(final String id)
				throws PortalErrorException, PortalGroupNotFoundException {
		List<UserGroup> parentGroups = new ArrayList<UserGroup>();
		List<PortalGroup> parentPortalGroups = portalService.getContainingGroupsById(id);
		for (PortalGroup portalGroup : parentPortalGroups) {
			UserGroup userGroup = convertToUserGroup(portalGroup);
			parentGroups.add(userGroup);
		}
		return parentGroups;
	}
	
	/**
	 * @param uid
	 * @param name
	 * @return the attributes
	 * @throws LdapUserNotFoundException
	 */
	public List<String> getLdapAttributesByUidAndName(final String uid, final String name) 
	throws LdapUserNotFoundException {
		List<String> attributes;
		try {
			attributes = ldapUserAndGroupService.getLdapAttributesByUidAndName(uid, name);
		} catch (LdapUserNotFoundException e) {
			final String messageStr = "Unable to find the user with id : [" + uid + "]";
			throw new LdapUserNotFoundException(messageStr, e);
		}
		return attributes;
	}

	/**
	 * @return the group hierarchy from the portal.
	 */
	public PortalGroupHierarchy getPortalGroupHierarchy() {
		PortalGroupHierarchy groupsHierarcchy = portalService.getGroupHierarchy();
		return groupsHierarcchy;
	}
	
	/**
	 * @param groupName
	 * @return the PortalGroupHierarchy corresponding to a group name.
	 */
	public PortalGroupHierarchy getPortalGroupHierarchyByGroupName(final String groupName) {
		if (logger.isDebugEnabled()) {
			logger.debug("get portal group hierarchy for group : " + groupName);
		}
		//get the portal group from the group name
		PortalGroup group = portalService.getGroupByName(groupName);
		//get the recipient group hierarchy
		PortalGroupHierarchy groupHierarchy = portalService.getGroupHierarchy(group);
		return groupHierarchy;
	}
	
	/**
	 * @param uids
	 * @return a list of LDAP user from a list of uids.
	 */
	public List<LdapUser> getUsersByUids(final List<String> uids) {
		return ldapUserAndGroupService.getUsersByUids(uids);
	}
	
	/**
	 * @param id
	 * @return a ldap group corresponding to an id
	 */
	public LdapGroup getLdapGroup(final String id) {
		return ldapUserAndGroupService.getLdapGroup(id);
	}
	
	/**
	 * @param ldapGroup
	 * @return the string id list of a ldap group. 
	 */
	public List<String> getMemberIds(final LdapGroup ldapGroup) {
		return ldapUserAndGroupService.getMemberIds(ldapGroup);
	}
	
	/**
	 * @param serviceKey 
	 * @param cgKeyName2 
	 * @param string 
	 * @param serviceKey 
	 * @param serviceKey2 
	 * @param cgKeyName2 
	 * @param ldapGroup
	 * @return the string id list of a ldap group. 
	 */
	@SuppressWarnings("unchecked")
	public List<LdapUser> getMembers(final GroupDefinition gd, String cgPropertyName, String serviceKey) {
		if (logger.isDebugEnabled()) {
			logger.debug("getMembers.start");
		}
		final List<LdapUser> users = new LinkedList<LdapUser>();
		final List<TestGroup> tgs = gd.getTestGroups();
		final String groupPortalParameter = smsuLdapGroupPersonAttributeDaoImpl.getPortalAttribute();
		OrFilter orFilter = null; 
		for (TestGroup testGroup : tgs) {
			if (logger.isDebugEnabled()) {
				logger.debug("test group : " + testGroup.toString());
			}
			final List<BaseAttributeTester> tests = testGroup.getTests();
			AndFilter andFilter = null;
			for (BaseAttributeTester test : tests) {
				if (logger.isDebugEnabled()) {
					logger.debug("test : " + test.toString());
				}
				final String portalAttributeName = test.getAttributeName();
				final String testValue = test.getTestValue();
				if (portalAttributeName.equals(groupPortalParameter)) {
					final String groupLdapAttribute = smsuLdapGroupPersonAttributeDaoImpl.getLdapAttribute();
					EqualsFilter filter = new EqualsFilter(groupLdapAttribute,testValue);
					if (logger.isDebugEnabled()) {
						logger.debug("Search group with filter : " + filter.toString());
					}
					// TODO : paramétrer le DN dans le service au lieu d'utiliser le paramétrage général du Ldap?
					List<LdapGroup> ldapGroups = ldapUserAndGroupService.getLdapGroupsFromToken(filter.toString());
					LdapGroup ldapGroup = ldapGroups.get(0);
					List<String> uids = getMemberIds(ldapGroup);
					List<LdapUser> usersToAdd = ldapUserAndGroupService.getConditionFriendlyLdapUsersFromUid(uids, cgKeyName, serviceKey);
					//users.addAll(ldapUserAndGroupService.getMembers(ldapGroup));
					users.addAll(usersToAdd);
				} else {
					final String attributeName = (String) smsuLdapPersonAttributeDaoImpl.getReverseAttributeMappings().get(portalAttributeName);
					Filter filter = test.getLdapFilter(attributeName, testValue);
					if (filter != null) {
						if (andFilter == null) {
							andFilter = new AndFilter();
						}
						andFilter.and(filter);
						andFilter.and(new EqualsFilter(cgPropertyName,cgKeyName));

						if (serviceKey != null) {
							andFilter.and(new EqualsFilter(cgPropertyName ,serviceKey));
						}
					}
				}
			}
			if (andFilter != null) {
				if (orFilter == null) {
					orFilter = new OrFilter();
				}
				orFilter.or(andFilter);
			}
		}
		if (orFilter != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("getMember : executed filter for person attribute search : " + orFilter.toString());
			}
			users.addAll(ldapUserAndGroupService.getLdapUsersFromFilter(orFilter.toString()));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getMembers.end");
		}
		return users;
	}
	
	/**
	 * Mutator
	 */
		
	/**
	 * Standard setter used by spring.
	 * @param ldapUserAndGroupService
	 */
	public void setLdapUserAndGroupService(
			final SearchableLdapUserAndGroupServiceSMSUImpl ldapUserAndGroupService) {
		this.ldapUserAndGroupService = ldapUserAndGroupService;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param writeableLdapUserService
	 */
	public void setWriteableLdapUserService(final WriteableLdapUserService writeableLdapUserService) {
		this.writeableLdapUserService = writeableLdapUserService;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param portalService
	 */
	public void setPortalService(final PortalService portalService) {
		this.portalService = portalService;
	}

	/**
	 * Standard setter used by spring.
	 * @param userPagerAttribute
	 */
	public void setUserPagerAttribute(final String userPagerAttribute) {
		this.userPagerAttribute = userPagerAttribute;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param userTermsOfUseAttribute
	 */
	public void setUserTermsOfUseAttribute(final String userTermsOfUseAttribute) {
		this.userTermsOfUseAttribute = userTermsOfUseAttribute;
	}
	
	/**
	 * Standard setter used by Spring.
	 * @param cgKeyName
	 */
	public void setCgKeyName(final String cgKeyName) {
		this.cgKeyName = cgKeyName;
	}

	public SmsuLdapGroupPersonAttributeDaoImpl getSmsuLdapGroupPersonAttributeDaoImpl() {
		return smsuLdapGroupPersonAttributeDaoImpl;
	}

	public void setSmsuLdapGroupPersonAttributeDaoImpl(
			final SmsuLdapGroupPersonAttributeDaoImpl smsuLdapGroupPersonAttributeDaoImpl) {
		this.smsuLdapGroupPersonAttributeDaoImpl = smsuLdapGroupPersonAttributeDaoImpl;
	}

	public SmsuLdapPersonAttributeDaoImpl getSmsuLdapPersonAttributeDaoImpl() {
		return smsuLdapPersonAttributeDaoImpl;
	}

	public void setSmsuLdapPersonAttributeDaoImpl(
			final SmsuLdapPersonAttributeDaoImpl smsuLdapPersonAttributeDaoImpl) {
		this.smsuLdapPersonAttributeDaoImpl = smsuLdapPersonAttributeDaoImpl;
	}

	
}
