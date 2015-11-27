package org.esupportail.smsu.services.ldap;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.exceptions.GroupNotFoundException;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapGroup;
import org.esupportail.commons.services.ldap.LdapGroupService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserAndGroupService;
import org.esupportail.commons.services.ldap.LdapAttributesModificationException;
import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.esupportail.smsu.services.ldap.beans.UserGroup;


public class LdapUtils {

	/**
	 * a logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * used to manage user and group (read only).
	 */
	private LdapUserAndGroupService ldapService;

	private LdapGroupService ldapGroupService;
	private LdapGroupService ldapGroupMembersService;

	/**
	 * used to manage user (read only)
	 */
	private LdapUtilsHelpers ldapUtilsHelpers;
	
	/**
	 * used to manage user (write only).
	 */
	private WriteableLdapUserServiceSMSUImpl writeableLdapUserService;
	
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
	
	private String userDnPath;
	private String userIdAttribute;
	private String groupMemberAttribute;
	private String groupNameAttribute;	
	private String groupMemberContainsUserAttribute;
	
	/**
	 * The objectClass ldap attribute to add if the userTermsOfUseAttribute or userPagerAttribute need a specific ldap schema
	 */
	private String objectClassToAdd;
	
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
			throwLdapUserNotFoundException(e, ldapUserUid);
		}
		
		return ldapUser;	
	}

	public LdapUser mayGetLdapUserByUid(final String uid) {
		try {
			return ldapService.getLdapUser(uid);
		} catch (UserNotFoundException e) {
			return null;
		}
	}
	
	private void throwLdapUserNotFoundException(UserNotFoundException e, final String ldapUserUid) 
			throws LdapUserNotFoundException {
		final String messageStr = 
			"Unable to find the user with id : [" + ldapUserUid + "]";
		logger.debug(messageStr, e);
		throw new LdapUserNotFoundException(messageStr, e);
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

	public String getUserDisplayName(LdapUser user) {
		return user.getAttribute(userDisplayName);
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
		addSpecificObjectClassIfNeeded(uid);
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
		addSpecificObjectClassIfNeeded(uid);
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
	
	
	private void addSpecificObjectClassIfNeeded(String uid) throws LdapUserNotFoundException, LdapWriteException {
		if (!StringUtils.isEmpty(objectClassToAdd)) {
			writeableLdapUserService.addUserAttribute(ldapService, uid, "objectClass", objectClassToAdd);
		}
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
		try {
			writeableLdapUserService.setOrClearUserAttribute(ldapService, uid, name, etiquette, value);
		} catch (UserNotFoundException e) {
			throwLdapUserNotFoundException(e, uid);
		} catch (LdapAttributesModificationException e) {
			logger.error("" + e, e);
			throw new LdapWriteException("" + e);
		}
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

	private UserGroup convertToUserGroup(final LdapGroup group) {
		return new UserGroup(group.getId(), group.getAttribute(groupNameAttribute));
	}

	private List<UserGroup> convertToUserGroups(final List<LdapGroup> groups) {
		List<UserGroup> l = new LinkedList<UserGroup>();
		for (LdapGroup group : groups) l.add(convertToUserGroup(group));
		return l;
	}
	
	/**
	 * Retrieve all the groups of a given user.
	 * @param uid : user identifier in the LDAP
	 * @return the list of user group
	 */
	public List<UserGroup> getUserGroupsByUid(final String uid) {
		String rid = userIdAttribute + '=' + uid + "," + userDnPath;
		if("uid".equalsIgnoreCase(groupMemberContainsUserAttribute)) {
			rid = uid;
		}
		String filter = groupMemberAttribute + "=" + rid;
		logger.debug("search ldap groups with ldap filter : " + filter);
		return convertToUserGroups(ldapGroupService.getLdapGroupsFromFilter(filter));
	}

	public List<UserGroup> searchGroupsByName(final String token) {
		return convertToUserGroups(ldapGroupService.getLdapGroupsFromToken(token));
	}

	public String getUserDisplayName(final Person p) {
		return getUserDisplayName(p.getLogin());
	}
	public String getUserDisplayName(final String uid) {
		try { 
			return getUserDisplayNameByUserUid(uid);
		} catch (LdapUserNotFoundException e) {
		    	return uid;
		}
	}

	/**
	 * Retrieve the group by id.
	 * @param id : group identifier in the LDAP
	 * @return the group name
	 */
	public String getGroupNameByIdOrNull(final String id) {
		try {
			return getLdapGroup(id).getAttribute(groupNameAttribute);
		} catch (GroupNotFoundException e) {
			return null;
		}
	}

	/**
	 * @param uids
	 * @return a list of user mails.
	 */
	public List<String> getUserEmailsAdressByUids(final Iterable<String> uids) {
		List<String> retVal = ldapUtilsHelpers.getUserMailsByUids(uids);
		return retVal;
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
		return ldapGroupService.getLdapGroup(id);
	}

	public List<String> getMemberIds(String groupId) {
		return ldapService.getMemberIds(ldapGroupMembersService.getLdapGroup(groupId));
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
	
	public void setLdapGroupService(LdapGroupService ldapGroupService) {
		this.ldapGroupService = ldapGroupService;
	}
	
	public void setLdapGroupMembersService(LdapGroupService ldapGroupMembersService) {
		this.ldapGroupMembersService = ldapGroupMembersService;
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

	public void setGroupMemberAttribute(String groupMemberAttribute) {
		this.groupMemberAttribute = groupMemberAttribute;
	}

	public void setUserDnPath(String userDnPath) {
		this.userDnPath = userDnPath;
	}

	public void setGroupNameAttribute(String groupNameAttribute) {
		this.groupNameAttribute = groupNameAttribute;
	}

	public void setGroupMemberContainsUserAttribute(String groupMemberContainsUserAttribute) {
		this.groupMemberContainsUserAttribute = groupMemberContainsUserAttribute;
	}

	public void setUserIdAttribute(String userIdAttribute) {
		this.userIdAttribute = userIdAttribute;
	}
		
	/**
	 * Standard setter used by Spring.
	 * @param objectClassToAdd
	 */
	public void setObjectClassToAdd(String objectClassToAdd) {
		this.objectClassToAdd = objectClassToAdd;
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
