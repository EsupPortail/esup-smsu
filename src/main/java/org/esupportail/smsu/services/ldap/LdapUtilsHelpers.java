package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserAndGroupService;
import org.apache.log4j.Logger;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;

/**
 * SMSU implementation of the LdapUserAndGroupService.
 * @author PRQD8824
 *
 */
public class LdapUtilsHelpers {
	
	/**
	 * Logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
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
	
	// instead of doing one huge filter (&(termsOfUse=CG)(|(uid=x1)(uid=x2)(uid=x3)...)
	// we do smaller filters with at most X uids.
	// for example for X=2 it gives (&(termsOfUse=CG)(|(uid=x1)(uid=x2))) then (&(termsOfUse=CG)(|(uid=x3)(uid=x4))) ...
	// on tests on a production openldap:
	// - the sweet point is between 5 and 10
	// - for X too large, it can be 10 times slower
	// - for X=1, it can be 2 times slower	
	private int ldapfilterBestNumberOfUidsForSpeed = 5; 
	
	/**
	 * constructor.
	 */
	public LdapUtilsHelpers() {
	}
	
	
	/**
	 * 
	 * @param uid
	 * @param name
	 * @return
	 */
	public List<String> getLdapAttributesByUidAndName(final String uid, 
			final String name) throws LdapUserNotFoundException {
		try {
			final LdapUser ldapUser = ldapService.getLdapUser(uid);
			return ldapUser.getAttributes(name);
		} catch (UserNotFoundException e) {
			throw new LdapUserNotFoundException("Unable to find the user with id : [" + uid + "]", e);
		}
	}
	
	
	/**
	 * Search an user by the search attribute and a token.
	 * @param token
	 */
	public List<LdapUser> getLdapUsersFromToken(final String token) {
		return searchWithFilter(tokenFilter(token));
	}

	public Filter tokenFilter(final String token) {
		AndFilter filter = new AndFilter();
		for (String tok : token.split("\\p{Blank}")) {
			if (tok.length() > 0)
				filter.and(new WhitespaceWildcardsFilter(searchAttribute, tok));
		}
		return filter;
	}

	public void andPagerAndConditionsAndService(final AndFilter filter,
			final String cgKeyName, final String service) {
		filter.and(new WhitespaceWildcardsFilter(userPagerAttribute, " "));
		if (cgKeyName != null) {
		    filter.and(new EqualsFilter(userTermsOfUseAttribute, cgKeyName));
		}
		if (service != null) {
			filter.and(new EqualsFilter(userTermsOfUseAttribute, service));
		}
	}
	
	public List<LdapUser> searchWithFilter(final Filter filter) {
		final String filterAsStr = filter.encode();
		if (logger.isDebugEnabled()) {
			logger.debug("LDAP filter applied : " + filterAsStr);
		}
		return ldapService.getLdapUsersFromFilter(filterAsStr);
	}
	
	/**
	 * Search an user by the pager attribute and a token.
	 * @param token
	 */
	public List<LdapUser> getLdapUsersFromPhoneNumber(final String token) {
		final AndFilter filter = new AndFilter();
		
		//add the pager filter
		filter.and(new EqualsFilter(userPagerAttribute, token));
				
		return searchWithFilter(filter);
	}
	
	/**
	 * @param uids
	 * @param cgKeyName
	 * @param service
	 * @return ldap users eligible for the service
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromUid(final List<String> uids,
			final String cgKeyName, final String service) {
		int nbUids = uids.size();
		int breakApart = ldapfilterBestNumberOfUidsForSpeed;
		 
		// we split the potentially big list of uids instead sublists.
		// this helps keeping the ldap filter small enough.
		// without this, on tests with openldap, it could be 10 times slower
		List<LdapUser> r = new ArrayList<>();
		for (int i = 0; i < nbUids; i += breakApart) {
			List<String> sub = uids.subList(i, Math.min(nbUids, i + breakApart));
			r.addAll(getConditionFriendlyLdapUsersFromUidRaw(sub, cgKeyName, service));
		}
		return r;
	}

	private List<LdapUser> getConditionFriendlyLdapUsersFromUidRaw(final List<String> uids,
			final String cgKeyName, final String service) {
		final OrFilter orFilter = orFilterOnUids(uids);
		if (orFilter == null) return new LinkedList<>();

        return getConditionFriendlyLdapUsers(orFilter, cgKeyName, service);
	}

	public List<LdapUser> getConditionFriendlyLdapUsers(Filter baseFilter,
			final String cgKeyName, final String service) {

		final AndFilter filter = new AndFilter();
		
		//add the general condition, service and pager filter
		andPagerAndConditionsAndService(filter, cgKeyName, service);
		
		filter.and(baseFilter);
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
		    return new LinkedList<>();
		else
		    return searchWithFilter(filter);
	}
	
	/**
	 * @param uids
	 * @return a list for user mails.
	 */
	public List<String> getUserMailsByUids(final Iterable<String> uids) {
		final List<String> retVal = new ArrayList<>();
		for (LdapUser ldapUser : getUsersByUids(uids)) {
			String mail = ldapUser.getAttribute(userEmailAttribute);
			if (mail != null) {
				logger.debug("mail added to list :" + mail);
				retVal.add(mail.trim());
			} else {
				logger.warn("no mail for " + ldapUser.getId());
			}
		}
		return retVal;
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
	@Required
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
