package org.esupportail.smsu.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.services.ldap.LdapUtils;
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
	public Map<String,String> getUsersHavingSentASms(@Context HttpServletRequest request) {
		if (request.isUserInRole("FCTN_SUIVI_ENVOIS_ETABL")) {
			return domainService.getPersons();
		} else {
			return domainService.fakePersonsWithCurrentUser(request.getRemoteUser());
		}
	}

	@GET
	@Produces("application/json")
	@Path("/search")
	public Map<String, String> search(
				@QueryParam("token") String token, 
				@QueryParam("service") String serviceKey,
				@QueryParam("ldapFilter") String ldapFilter) {
		if (ldapFilter != null)
			return searchLdapWithFilter(ldapFilter);
		else if (token != null)
			return searchLdapUser(token, serviceKey);
		else
			throw new InvalidParameterException("missing param uid or ldapFilter");
	}
	
	private Map<String, String> searchLdapUser(String token, String serviceKey) {
		if (token.trim().length() < NB_MIN_CHARS_FOR_LDAP_SEARCH ) throw new InvalidParameterException("token too short");
		
		List<LdapUser> list = serviceKey == null ?
			ldapUtils.searchLdapUsersByToken(token) :
			ldapUtils.searchConditionFriendlyLdapUsersByToken(token, serviceKey);			
		return convertToUI(list);
	}
	
	private Map<String, String> searchLdapWithFilter(@PathParam("filter") String ldapFilter) {
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

	private Map<String,String> convertToUI(List<LdapUser> list) {
		Map<String, String> result = new HashMap<String,String>();
		for (LdapUser user : list) {
			String userId = user.getId();
			String displayName = ldapUtils.getUserDisplayName(user) + " (" + user.getId() + ")";
			String phone = ldapUtils.getUserPagerByUser(user);

			logger.debug("ajout de la personne : uid =" + userId 
					+ " displayName=" + displayName 
					+ " phone=" + phone + " a la liste");

			if (phone == null
				|| !StringUtils.isEmpty(this.phoneNumberPattern) 
			       && !phone.matches(this.phoneNumberPattern)) {
				continue;			    
			}
			result.put(userId, displayName);
		}
		return result;
	}
	
	//////////////////////////////////////////////////////////////
	// Setter
	//////////////////////////////////////////////////////////////
	@Required
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

}
