package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserAndGroupService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.springframework.ldap.support.filter.AndFilter;
import org.springframework.ldap.support.filter.EqualsFilter;
import org.springframework.ldap.support.filter.Filter;
import org.springframework.ldap.support.filter.OrFilter;
import org.springframework.ldap.support.filter.WhitespaceWildcardsFilter;

/**
 * SMSU implementation of the LdapUserAndGroupService.
 * @author PRQD8824
 *
 */
public class LdapUtilsHelpers {
	
	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = -4824757654577713044L;
	
	/**
	 * Logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Spring template used to perform search in the ldap.
	 */
	private LdapUserAndGroupService ldapService;
	
	/**
	 * The uid ldap attribute name.
	 */
	private String userIdAttribute;
	
	/**
	 * The email ldap attribute name.
	 */
	private String userEmailAttribute;
	
	/**
	 * The pager attributeName.
	 */
	private String userPagerAttribute;
	
	/**
	 * The attribute terms of use in the ldap.
	 */
	private String userTermsOfUseAttribute;

	/**
	 * The search attribute field.
	 */
	private String searchAttribute;
	
	/**
	 * constructor.
	 */
	public LdapUtilsHelpers() {
		super();
	}
	
	
	/**
	 * 
	 * @param uid
	 * @param name
	 * @return
	 */
	public List<String> getLdapAttributesByUidAndName(final String uid, 
			final String name) throws LdapUserNotFoundException {
		List<String> retVal = null;
		
		try {
			final LdapUser ldapUser = ldapService.getLdapUser(uid);
			retVal = ldapUser.getAttributes(name);
		} catch (UserNotFoundException e) {
			throw new LdapUserNotFoundException("Unable to find the user with id : [" + uid + "]", e);
		}
		return retVal;
	}
	
	
	/**
	 * Search an user by the search attribute and a token.
	 * @param token
	 */
	public List<LdapUser> getLdapUsersFromToken(final String token) {
		final AndFilter filter = new AndFilter();
		andTokenFilter(filter, token);			
		return searchWithFilter(filter);
	}

	private void andTokenFilter(final AndFilter filter, final String token) {
		for (String tok : token.split("\\p{Blank}")) {
			if (tok.length() > 0)
				filter.and(new WhitespaceWildcardsFilter(searchAttribute, tok));
		}
	}

	public void andPagerAndConditionsAndService(final AndFilter filter,
			final String cgKeyName, final String service) {
		filter.and(new WhitespaceWildcardsFilter(userPagerAttribute, " "));
		filter.and(new EqualsFilter(userTermsOfUseAttribute, cgKeyName));
		if (service != null) {
			filter.and(new EqualsFilter(userTermsOfUseAttribute, service));
		}
	}
	
	public List<LdapUser> searchWithFilter(final Filter filter) {
		final String filterAsStr = filter.encode();
		if (logger.isDebugEnabled()) {
			logger.debug("LDAP filter applied : " + filterAsStr);
		}
		final List<LdapUser> retVal = ldapService.getLdapUsersFromFilter(filterAsStr);
		return retVal;
	}
	
	/**
	 * Search an user by the pager attribute and a token.
	 * @param token
	 */
	public List<LdapUser> getLdapUsersFromPhoneNumber(final String token) {
		final AndFilter filter = new AndFilter();
		
		//add the pager filter
		filter.and(new WhitespaceWildcardsFilter(userPagerAttribute, token));	
				
		return searchWithFilter(filter);
	}
	
	/**
	 * @param uid
	 * @param cgKeyName
	 * @param service
	 * @return the ldap user if he is eligible for the service
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromUid(final List<String> uids,
			final String cgKeyName, final String service) {

		final AndFilter filter = new AndFilter();
		
		//add the general condition, service and pager filter
		andPagerAndConditionsAndService(filter, cgKeyName, service);
		
		final OrFilter orFilter = orFilterOnUids(uids);
		if (orFilter == null) return new LinkedList<LdapUser>();

		filter.and(orFilter);		
		return searchWithFilter(filter);
	}

	boolean isEmpty(final Iterable <?> l) {
		return !l.iterator().hasNext();
	}

	OrFilter orFilterOnUids(final Iterable<String> uids) {
		// needed since empty OrFilter() is true instead of false (https://jira.springsource.org/browse/LDAP-226)
		if (isEmpty(uids)) return null;

		final OrFilter filter = new OrFilter();
		for (String uid : uids) {
			filter.or(new EqualsFilter(userIdAttribute, uid));
		}
		return filter;
	}
	
	/**
	 * @param uids
	 * @return a list of mails
	 */
	public List<LdapUser> getUsersByUids(final Iterable<String> uids) {
		final OrFilter filter = orFilterOnUids(uids);
		if (filter == null) 
		    return new LinkedList<LdapUser>();
		else
		    return searchWithFilter(filter);
	}
	
	/**
	 * @param uids
	 * @return a list for user mails.
	 */
	public List<String> getUserMailsByUids(final List<String> uids) {
		final List<LdapUser> ldapUsers = getUsersByUids(uids);
		final List<String> retVal = new ArrayList<String>();
		for (LdapUser ldapUser : ldapUsers) {
			if (logger.isDebugEnabled()) {
				logger.debug("mail recipient added to list :" 
						+ ldapUser.getAttribute(userEmailAttribute).trim());
			}
			retVal.add(ldapUser.getAttribute(userEmailAttribute).trim());
		}
		return retVal;
	}
	/**
	 * Search an user by the search attribute and a token.
	 * @param token
	 * @param cgKeyName 
	 * @param service 
	 * @return 
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromToken(final String token, 
			final String cgKeyName, final String service) {
		if (logger.isDebugEnabled()) {
			logger.debug("getConditionFriendlyLdapUsersFromToken : " + token);
		}
		final AndFilter filter = new AndFilter();
		andPagerAndConditionsAndService(filter, cgKeyName, service);
		andTokenFilter(filter, token);

		return searchWithFilter(filter);
	}
	
	/**
	 * Mutator
	 */
	
	
	/**
	 * Set the ldapService.
	 * @param ldapService
	 */
	public void setLdapService(final LdapUserAndGroupService ldapService) {
		this.ldapService = ldapService;		
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
	 */
	public void setUserIdAttribute(final String idAttribute) {
		this.userIdAttribute = idAttribute;
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
	 * @param searchAttribute
	 */
	public void setUserSearchAttribute(final String searchAttribute) {
		this.searchAttribute = searchAttribute;
	}


}
