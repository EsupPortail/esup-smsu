package org.esupportail.smsu.web.controllers;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIRecipientUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

@Path("/users")
public class UsersController {

	private static final long NB_MIN_CHARS_FOR_LDAP_SEARCH = 4;
	
	@Autowired private LdapUtils ldapUtils;
	@Autowired private DomainService domainService;

	/**
	 * the phone number validation pattern.
	 */
	private String phoneNumberPattern;
	
	private final Logger logger = new LoggerImpl(getClass());

	@GET
	@Produces("application/json")
	@Path("/search")
	public List<UIRecipientUser> search(
				@QueryParam("token") String token, 
				@QueryParam("service") String serviceKey,
				@QueryParam("id") String id, 
				@QueryParam("ldapFilter") String ldapFilter) {
		if (ldapFilter != null)
			return searchLdapWithFilter(ldapFilter);
		else if (token != null)
			return searchLdapUser(token, serviceKey);
		else if (id != null)
			return searchLdapUserId(id, serviceKey);
		else
			throw new InvalidParameterException("missing param 'token' or 'id' or 'ldapFilter'");
	}
	
	private List<UIRecipientUser> searchLdapUser(String token, String serviceKey) {
		if (token.trim().length() < NB_MIN_CHARS_FOR_LDAP_SEARCH ) throw new InvalidParameterException("token too short");
		
		List<LdapUser> list = serviceKey == null ?
			ldapUtils.searchLdapUsersByToken(token) :
			ldapUtils.searchConditionFriendlyLdapUsersByToken(token, serviceKey);			
		return convertToUI(list);
	}

	private List<UIRecipientUser> searchLdapUserId(String id, String serviceKey) {
		List<String> uids = singletonList(id);
		List<LdapUser> list = ldapUtils.getConditionFriendlyLdapUsersFromUid(uids, serviceKey);
		return convertToUI(list);
	}

	private List<UIRecipientUser> searchLdapWithFilter(@PathParam("filter") String ldapFilter) {
		if (StringUtils.isBlank(ldapFilter)) return null;

		logger.debug("Execution de la requete utilisateur : " + ldapFilter);
		try {
			List<LdapUser> list = ldapUtils.searchLdapUsersByFilter(ldapFilter);
			return convertToUI(list);
		} catch (LdapException  e) {
			logger.error("Erreur lors de l'execution de la requete : [" + ldapFilter + "]", e);
			throw new InvalidParameterException("SENDSMS.MESSAGE.LDAPREQUESTERROR");
		}
	}

	private List<UIRecipientUser> convertToUI(List<LdapUser> list) {
		List<UIRecipientUser> result = new LinkedList<UIRecipientUser>();
		for (LdapUser user : list) {
			String userId = user.getId();
			String displayName = ldapUtils.getUserDisplayName(user);
			String phone = ldapUtils.getUserPagerByUser(user);

			logger.debug("ajout de la personne : uid =" + userId 
					+ " displayName=" + displayName 
					+ " phone=" + phone + " a la liste");

			boolean noSMS = false;
			if (phone == null
				|| !StringUtils.isEmpty(this.phoneNumberPattern) 
			       && !phone.matches(this.phoneNumberPattern)) {
				noSMS = true;    
			}
			result.add(new UIRecipientUser(userId, displayName, noSMS));
		}
		return result;
	}

	private <A> LinkedList<A> singletonList(A e) {
		final LinkedList<A> l = new LinkedList<A>();
		l.add(e);
		return l;
	}	
	
	//////////////////////////////////////////////////////////////
	// Setter
	//////////////////////////////////////////////////////////////
	@Required
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

}
