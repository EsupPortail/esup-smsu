package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapGroup;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserAndGroupService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.portal.ws.client.PortalGroup;
import org.esupportail.portal.ws.client.PortalGroupHierarchy;
import org.esupportail.portal.ws.client.PortalService;
import org.esupportail.portal.ws.client.exceptions.PortalErrorException;
import org.esupportail.portal.ws.client.exceptions.PortalGroupNotFoundException;
import org.esupportail.portal.ws.client.exceptions.PortalUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
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
	private LdapUserAndGroupService ldapService;

	/**
	 * used to manage user (read only)
	 */
	private LdapUtilsHelpers ldapUtilsHelpers;
	
	/**
	 * used to manage user (write only).
	 */
	private WriteableLdapUserServiceSMSUImpl writeableLdapUserService;

	/**
	 * used to manage group (read only).
	 */
	private PortalService portalService;
	
	/**
	 * The display name ldap attribute name.
	 */
	private String userDisplayName;
	
	/**
	 * The attribute first name in the ldap.
	 */
	private String firstNameAttribute;

	/**
	 * The attribute last name in the ldap.
	 */
	private String lastNameAttribute;

	/**
	 * The email ldap attribute name.
	 */
	private String userEmailAttribute;

	/**
	 * the attribute pager name in the ldap.
	 */
	private String userPagerAttribute;
	
	/**
	 * The attribute terms of use in the ldap.
	 */
	private String userTermsOfUseAttribute;

	/**
	 * http://www.cru.fr/documentation/supann/2009/etiquetageattributs
	 */
	private String userTermsOfUseAttributeEtiquetteSMSU;
	
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
			ldapUser = ldapService.getLdapUser(ldapUserUid);
		} catch (UserNotFoundException e) {
			final String messageStr = 
			    "Unable to find the user with id : [" + ldapUserUid + "]";
			logger.debug(messageStr, e);
			throw new LdapUserNotFoundException(messageStr, e);
		}
		
		return ldapUser;	
	}
	
	private String getUniqueLdapAttributeByUidAndName(final String uid, 
			final String name) throws LdapUserNotFoundException {
		final List<String> tmp = getLdapAttributesByUidAndName(uid, name);		
		return tmp.size() > 0 ? tmp.get(0) : null;
	}
	
	/**
	 * Return the display name of the specified user.
	 * @param uid
	 * @return
	 */
	public String getUserDisplayNameByUserUid(final String uid) throws LdapUserNotFoundException {
		return getUniqueLdapAttributeByUidAndName(uid, userDisplayName);
	}
	
	
	/**
	 * Return the first name of the specified user.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public String getUserFirstNameByUid(final String uid) throws LdapUserNotFoundException {
		return getUniqueLdapAttributeByUidAndName(uid, firstNameAttribute);
	}
	
	/**
	 * Return the last name of the specified user.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public String getUserLastNameByUid(final String uid) throws LdapUserNotFoundException {
		return getUniqueLdapAttributeByUidAndName(uid, lastNameAttribute);
	}
	
	
	/**
	 * Get the user email adress from the ldap.
	 * @param uid
	 * @param name
	 * @return
	 */
	public String getUserEmailAdressByUid(final String uid) throws LdapUserNotFoundException {
		return getUniqueLdapAttributeByUidAndName(uid, userEmailAttribute);
	}
	
	/**
	 * Get the pager user from the ldap.
	 * @param uid
	 * @return
	 */
	public String getUserPagerByUid(final String uid) throws LdapUserNotFoundException {
		return getUniqueLdapAttributeByUidAndName(uid, userPagerAttribute);
	}

	/**
	 * Get the pager user from the ldap.
	 * @param user
	 * @return
	 */
	public String getUserPagerByUser(LdapUser user) {
		return user.getAttribute(userPagerAttribute);
	}
	
	/**
	 * the termOfUse user from the ldap.
	 * @param uid
	 * @return
	 */
	private List<String> getUserTermsOfUseByUid(final String uid) throws LdapUserNotFoundException {
		return ldapUtilsHelpers.getLdapAttributesByUidAndName(uid, userTermsOfUseAttribute);
	}

	private String completeCgKeyName() {
		return mayAddEtiquette(cgKeyName);
	}
	
	/**
	 * Used to set the pager attribute of a specified user.
	 * @param uid
	 * @param pagerValue
	 * @throws LdapUserNotFoundException if the user is not found in the ldap 
	 * @throws LdapWriteException 
	 */
	public void setUserPagerByUid(final String uid, final String pagerValue) throws LdapUserNotFoundException, LdapWriteException {
		setOrClearLdapAttributeByUidAndName(uid, userPagerAttribute, pagerValue);

	}
	
	/**
	 * Clear the pager attribute for the specified user.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 * @throws LdapWriteException 
	 */
	public void clearUserPager(final String uid) throws LdapUserNotFoundException, LdapWriteException {
		clearLdapAttributeByUidAndName(uid, userPagerAttribute);
	}
	
	/**
	 * used to set the terms of use of a specified user.
	 * @param uid
	 * @param validateGeneralCondition
	 * @param termsOfUseValue
	 * @throws LdapUserNotFoundException if the user is not found in the ldap
	 * @throws LdapWriteException 
	 */
	public void setUserTermsOfUse(final String uid, final boolean validateGeneralCondition,
			final List<String> specificConditions) 
			throws LdapUserNotFoundException, LdapWriteException {
		List<String> values = new LinkedList<String>();
		if (validateGeneralCondition) values.add(cgKeyName);
		if (specificConditions != null) values.addAll(specificConditions);
		setOrClearLdapAttributeByUidAndName(uid, userTermsOfUseAttribute, userTermsOfUseAttributeEtiquetteSMSU, values);
	}
	
	
	/**
	 * Clear a user specified attribute. 
	 * @param uid
	 * @param name
	 * @throws LdapUserNotFoundException
	 * @throws LdapWriteException 
	 */
	private void clearLdapAttributeByUidAndName(final String uid, final String name) 
					throws LdapUserNotFoundException, LdapWriteException {
		setOrClearLdapAttributeByUidAndName(uid, name, null);
	}
	
	
	/**
	 * Set or clear a user specified attribute.
	 * It handles the attribute etiquette: 
	 * - it keeps unmodified attribute values without this etiquette
	 * - it prefixes the values with this etiquette
	 * @param uid
	 * @param name
	 * @param etiquette
	 * @param value
	 * @throws LdapUserNotFoundException
	 * @throws LdapWriteException 
	 */
	private void setOrClearLdapAttributeByUidAndName(final String uid, final String name, final String etiquette, final List<String> value) 
					throws LdapUserNotFoundException, LdapWriteException {
		writeableLdapUserService.invalidateLdapCache();
		final LdapUser ldapUser = getLdapUserByUserUid(uid);

		Map<String, List<String>> attrs = ldapUser.getAttributes();
		List<String> allValues = computeAttributeValues(attrs.get(name), etiquette, value);
		attrs.put(name, allValues);

		// call updateLdapUser with only the attribute we want to write in LDAP
		ldapUser.setAttributes(singletonMap(name, allValues));
		writeableLdapUserService.updateLdapUser(ldapUser);
		ldapUser.setAttributes(attrs); // restore other attributes

		checkAttributeWriteByUidAndNameSucceeded(uid, name, allValues);

	}

	private <A, B> Map<A, B> singletonMap(A key, B value) {
		Map<A, B> r = new HashMap<A, B>();
		r.put(key, value);
		return r;
	}

	private List<String> computeAttributeValues(List<String> currentValues,	final String etiquette, final List<String> wantedValues) {
		if (StringUtils.isEmpty(etiquette))
			return wantedValues;

		Set<String> set = new TreeSet<String>();
		if (currentValues != null) {
		    for (String s : currentValues)
			if (!s.startsWith(etiquette)) set.add(s);
		}
		for (String v : wantedValues) 
			set.add(mayAddPrefix(etiquette, v));
		return new ArrayList<String>(set);
	}

	private String mayAddPrefix(String prefix, String s) {
		return prefix == null || s.startsWith(prefix) ? s : prefix + s;
	}

	/**
	 * Ensure the service key is prefixed with the etiquette
	 */
	private String mayAddEtiquette(String service) {
		return service == null ? service : mayAddPrefix(userTermsOfUseAttributeEtiquetteSMSU, service);
	}

	/**
	 * Check wether setting or clearing attribute worked correctly
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	private void checkAttributeWriteByUidAndNameSucceeded(final String uid, final String name, final List<String> value) throws LdapUserNotFoundException, LdapWriteException {
		List<String> storedValue = getLdapAttributesByUidAndName(uid, name);

		String error = null;
		if (value == null && (storedValue == null || storedValue.isEmpty()))
			;
		// nb: we can't check wether clearing attribute really removed the attribute or simply emptied it
		else if (value != null && storedValue == null)
			// this never happens, storedValue is never null afaik
			error = "could not create attribute '" + name + "' with value " + join(value, ", ");
		else if (!value.containsAll(storedValue) || !storedValue.containsAll(value))
			error = "could not modify attribute '" + name + "' with value " + join(value, ", ") + ", it's value is still " + join(storedValue, ", "); 

		if (error != null) {
			logger.error(error);
			throw new LdapWriteException(error);
		}
	}
	
	private void setOrClearLdapAttributeByUidAndName(final String uid, final String name, String value) 
					throws LdapUserNotFoundException, LdapWriteException {
		List<String> l = value != null ? singletonList(value) : null;
		setOrClearLdapAttributeByUidAndName(uid, name, null, l);
	}
	
	/**
	 * Get the ldap user list of the specified group.
	 * @param LdapGroupId
	 * @return hte user list of the specified group, or null if no group found
	 */
	/*public List<LdapUser> getLdapUsersByGroupId(final String ldapGroupId) 
	 * throws LdapGroupNotFoundException, LdapException {
		
		final LdapGroup ldapGroup = getLdapGroupByGroupId(ldapGroupId);
		final List<LdapUser> ldapUserList = ldapGroupService.getMembers(ldapGroup);
		
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
			ldapGroup = ldapGroupService.getLdapGroup(ldapGroupId);
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
		return checkGeneralAndSpecificConditionValidate(getUserTermsOfUseByUid(uid), null, uid);
	}
	
	
	/**
	 * Add the general condition flag in the ldap.
	 * @param uid
	 * @throws LdapUserNotFoundException
	 * @throws LdapWriteException 
	 */
	public void addGeneralConditionByUid(final String uid) throws LdapUserNotFoundException, LdapWriteException {
		setUserTermsOfUse(uid, true, getSpecificConditionsValidateByUid(uid));
	}

	public boolean isGeneralAndSpecificConditionValidate(final LdapUser user, final String specificConditionKey) {
		final List<String> termsOfUse = user.getAttributes(userTermsOfUseAttribute);
		return checkGeneralAndSpecificConditionValidate(termsOfUse, specificConditionKey, user.getId());
	}

	private boolean checkGeneralAndSpecificConditionValidate(final List<String> termsOfUse, String specificConditionKey, String uidForLog) {
		if (!termsOfUse.contains(completeCgKeyName())) {
			if (logger.isDebugEnabled()) logger.debug("CG not validated, user : " + uidForLog);
			return false;
		} else if (specificConditionKey != null) {
			if (logger.isDebugEnabled()) logger.debug("Service filter activated");
			return termsOfUse.contains(mayAddEtiquette(specificConditionKey));
		} else {
			if (logger.isDebugEnabled()) logger.debug("No service filter");
			return true;
		}
	}
	
	
	/**
	 * test if the specific condition is in the ldap.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public List<String> getSpecificConditionsValidateByUid(final String uid) 
	throws LdapUserNotFoundException {
		List<String> termsOfuse = new LinkedList<String>();

		String etiquette = userTermsOfUseAttributeEtiquetteSMSU;
		if (etiquette == null) etiquette = "";

		for (String s : getUserTermsOfUseByUid(uid)) {
			if (!s.equals(completeCgKeyName()) && s.startsWith(etiquette))
				termsOfuse.add(s.substring(etiquette.length()));
		}
		return termsOfuse;
	}
	
	/**
	 * Search users by token.
	 * @param token
	 * @return a list of users
	 */
	public List<LdapUser> searchLdapUsersByToken(final String token) {
		final List<LdapUser> userList = ldapUtilsHelpers.getLdapUsersFromToken(token);
		return userList;
	}
	
	/**
	 * @param filter
	 * @return a list of users
	 * @throws LdapException 
	 */
	public List<LdapUser> searchLdapUsersByFilter(final String filter) throws LdapException {
		final List<LdapUser> userList = ldapService.getLdapUsersFromFilter(filter);
		return userList;
	}
	/**
	 * Search users by token.
	 * @param token
	 * @return a list of users
	 */
	public List<LdapUser> searchLdapUsersByPhoneNumber(final String token) {
		final List<LdapUser> userList = ldapUtilsHelpers.getLdapUsersFromPhoneNumber(token);
		return userList;
	}
	
	/**
	 * Search filtered users by token.
	 * @param token
	 * @return a list of users
	 */
	public List<LdapUser> searchConditionFriendlyLdapUsersByToken(final String token, final String service) {
		return ldapUtilsHelpers.getConditionFriendlyLdapUsersFromToken(
			token, completeCgKeyName(), mayAddEtiquette(service));
	}

	/**
	 * @param uids
	 * @param service
	 * @return a list of users
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromUid(final List<String> uids, final String service) {
		return ldapUtilsHelpers.getConditionFriendlyLdapUsersFromUid(
			uids, completeCgKeyName(), mayAddEtiquette(service));
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
		String retVal = getGroupNameByUidOrNull(uid);
		return retVal != null ? retVal : uid;
	}

	/**
	 * Retrieve the group by id.
	 * @param uid : group identifier in the LDAP
	 * @return the group name
	 */
	public String getGroupNameByUidOrNull(final String uid) {
		try {
		    PortalGroup portalGroup = portalService.getGroupById(uid);
		    if (portalGroup != null) return portalGroup.getName();
		} catch (PortalGroupNotFoundException e) {
		}
		return null;
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
		List<String> retVal = ldapUtilsHelpers.getUserMailsByUids(uids);
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
				logger.debug("Parent group of " + groupId + " : [" + group.getName() + "] found");
			}
			parentGroupIds.add(group.getId());
		}
		String parentGroupId = null;
		if (parentGroupIds.size() > 0) {
			parentGroupId = parentGroupIds.get(0);
		}
		return parentGroupId;
		} catch (PortalErrorException e) {
			logger.debug("discarded exception " + e, e);
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
			attributes = ldapUtilsHelpers.getLdapAttributesByUidAndName(uid, name);
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
	public List<LdapUser> getUsersByUids(final Iterable<String> uids) {
		return ldapUtilsHelpers.getUsersByUids(uids);
	}
	
	/**
	 * @param id
	 * @return a ldap group corresponding to an id
	 */
	public LdapGroup getLdapGroup(final String id) {
		return ldapService.getLdapGroup(id);
	}
	
	/**
	 * @param ldapGroup
	 * @return the string id list of a ldap group. 
	 */
	public List<String> getMemberIds(final LdapGroup ldapGroup) {
		return ldapService.getMemberIds(ldapGroup);
	}
	
	/**
	 * @param gd
	 * @param serviceKey 
	 * @return the string id list of a ldap group. 
	 */
	@SuppressWarnings("unchecked")
	public List<LdapUser> getMembers(final GroupDefinition gd, String serviceKey) {
		if (logger.isDebugEnabled()) {
			logger.debug("getMembers.start");
		}
		serviceKey = mayAddEtiquette(serviceKey);
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
					// TODO : parametrer le DN dans le service au lieu d'utiliser le parametrage general du Ldap?
					List<LdapGroup> ldapGroups = ldapService.getLdapGroupsFromToken(filter.toString());
					if (ldapGroups.isEmpty()) {
						logger.error("skipping LDAP group " + testValue + " which does not exist");
					} else {
						LdapGroup ldapGroup = ldapGroups.get(0);
						List<String> uids = getMemberIds(ldapGroup);
						List<LdapUser> usersToAdd = ldapUtilsHelpers.getConditionFriendlyLdapUsersFromUid(uids, completeCgKeyName(), serviceKey);
						if (logger.isDebugEnabled())
							logger.debug("found " + uids.size() + " users in group " + testValue + " and " + usersToAdd.size() + " users having pager+CG");
						users.addAll(usersToAdd);
					}
				} else {
					final String attributeName = (String) smsuLdapPersonAttributeDaoImpl.getReverseAttributeMappings().get(portalAttributeName);
					Filter filter = test.getLdapFilter(attributeName, testValue);
					if (filter != null) {
						if (andFilter == null) {
							andFilter = new AndFilter();
						}
						andFilter.and(filter);
					}
				}
			}
			if (andFilter != null) {
				ldapUtilsHelpers.andPagerAndConditionsAndService(andFilter, completeCgKeyName(), serviceKey);

				if (orFilter == null) {
					orFilter = new OrFilter();
				}
				orFilter.or(andFilter);
			}
		}
		if (orFilter != null) {
			logger.debug("getMember : person attribute search");
			users.addAll(ldapUtilsHelpers.searchWithFilter(orFilter));
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
	 * @param ldapUtilsHelpers
	 */
	public void setLdapUtilsHelpers(
			final LdapUtilsHelpers ldapUtilsHelpers) {
		this.ldapUtilsHelpers = ldapUtilsHelpers;
	}
		
	/**
	 * Standard setter used by spring.
	 * @param ldapUtilsHelpers
	 */
	public void setLdapService(
			final LdapUserAndGroupService ldapGroupService) {
		this.ldapService = ldapGroupService;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param writeableLdapUserService
	 */
	public void setWriteableLdapUserService(final WriteableLdapUserServiceSMSUImpl writeableLdapUserService) {
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
	 * @param userDisplayName
	 */
	public void setUserDisplayName(final String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param firstNameAttribute
	 */
	public void setFirstNameAttribute(final String firstNameAttribute) {
		this.firstNameAttribute = firstNameAttribute;
	}

	/**
	 * Standard setter used by spring.
	 * @param lastNameAttribute
	 */
	public void setLastNameAttribute(final String lastNameAttribute) {
		this.lastNameAttribute = lastNameAttribute;
	}

	/**
	 * Standard setter used by Spring.
	 * @param ldapEmailAttribute
	 */
	public void setUserEmailAttribute(final String userEmailAttribute) {
		this.userEmailAttribute = userEmailAttribute;
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
	 * Standard setter used by spring.
	 * @param userTermsOfUseAttributeEtiquetteSMSU
	 */
	public void setUserTermsOfUseAttributeEtiquetteSMSU(final String userTermsOfUseAttributeEtiquetteSMSU) {
		this.userTermsOfUseAttributeEtiquetteSMSU = userTermsOfUseAttributeEtiquetteSMSU;
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

	private <A> LinkedList<A> singletonList(A e) {
		final LinkedList<A> l = new LinkedList<A>();
		l.add(e);
		return l;
	}	

	public static String join(Iterable<?> elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object s : elements) {
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}
}
