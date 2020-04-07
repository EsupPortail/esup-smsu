/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsu.domain;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.esupportail.commons.services.ldap.LdapException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.SecurityManager;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.services.client.SmsuapiWS;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.ws.remote.beans.TrackInfos;
import javax.inject.Inject;
import org.springframework.util.StringUtils;


public class DomainService {

	@Inject private DaoService daoService;
	@Inject private LdapUtils ldapUtils;	
	@Inject private SecurityManager securityManager;	
	@Inject private SmsuapiWS smsuapiWS;

	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * create user from a LDAP search or create a simple one
	 */
	public User getUser(final String id) {
		User user = new User();
		user.setId(id);
		try {
			String displayName = ldapUtils.getUserDisplayName(id);
			if (displayName == null) {
				displayName = id;
			}
			user.setDisplayName(displayName);
			user.rights = securityManager.loadUserRightsByUsername(id);
		} catch (LdapException e) {
		}
		return user;
	}
		
	//////////////////////////////////////////////////////////////
	// Message
	//////////////////////////////////////////////////////////////
	public void addMessage(final Message message) {
		this.daoService.addMessage(message);
	}

	public void updateMessage(final Message message) {
		this.daoService.updateMessage(message);

	}

	//////////////////////////////////////////////////////////////
	// Account
	//////////////////////////////////////////////////////////////
	public List<String> getAccounts() {
		List<String> result = new ArrayList<>();
		List<Account> accounts = daoService.getAccounts();
		for (Account acc : accounts) {
			result.add(acc.getLabel());
		}
		return result;
	}

	//////////////////////////////////////////////////////////////
	// Person
	//////////////////////////////////////////////////////////////
	public Map<String, String> fakePersonsWithCurrentUser(String userId) {
		User user = getUser(userId);
		Map<String, String> result = new HashMap<>();
		result.put(user.getId(), user.getDisplayName());
		return result;
	}
	
	public Map<String, String> getPersons() {
		Map<String, String> result = new HashMap<>();

		List<String> displayNameList = new ArrayList<>();
		for (Person per : daoService.getPersons()) {
				displayNameList.add(per.getLogin());
				result.put(per.getLogin(), per.getLogin()); // default value
		}
		List<LdapUser> ldapUserList = ldapUtils.getUsersByUids(displayNameList);

		for (LdapUser ldapUser : ldapUserList) {
			String displayName = ldapUtils.getUserDisplayName(ldapUser);
			if (!StringUtils.isEmpty(displayName)) {
				logger.debug("displayName is: " + displayName);	
				result.put(ldapUser.getId(), displayName);
			}
		}
		return result;
	}

	//////////////////////////////////////////////////////////////
	// Service
	//////////////////////////////////////////////////////////////
	public Service getServiceById(final Integer id) {
		Service service = this.daoService.getServiceById(id);
		return service;
	}
	
	//////////////////////////////////////////////////////////////
	// Customized groups
	//////////////////////////////////////////////////////////////	
	public void addCustomizedGroup(final CustomizedGroup customizedGroup) {
		this.daoService.addCustomizedGroup(customizedGroup);		
	}

	public CustomizedGroup getCustomizedGroupByLabel(final String label) {
		return this.daoService.getCustomizedGroupByLabel(label);
	}

	public CustomizedGroup getFirstCustomizedGroup() {
		return this.daoService.getFirstCustomizedGroup();
	}
	
	//////////////////////////////////////////////////////////////
	// Used by Date methods in MessagesController
	//////////////////////////////////////////////////////////////
	/** 
 	 *@param date
	 * @see org.esupportail.smsu.domain.DomainService#formatDateDao
	 */
	public Date formatDateDao(final Date date) {
    	String out;
    	Date dateTemp = date;
    	
    	// 1- Convert Date "dd/MM/YYYY" to String
    	SimpleDateFormat inFmt = new SimpleDateFormat("dd/MM/yyyy");
    	String dateStr = inFmt.format(date);
    
    	// 2- Convert String to "YYYY-MM-DD"
    	// 3- Convert String to Date "YYYY-MM-DD"
    	SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd");
    	try {
			out = outFmt.format(inFmt.parse(dateStr));
			dateTemp = outFmt.parse(out);
		} catch (ParseException e) {
			e.printStackTrace();
		}
          
    	return dateTemp;
	   }
    
	public TrackInfos getMessageStatuses(final Integer msgId) throws HttpException, UnknownMessageIdException {
		return smsuapiWS.getMessageStatus(msgId);
	}

	public Set<String> getListPhoneNumbersInBlackList() throws HttpException {
		return smsuapiWS.getListPhoneNumbersInBlackList();
	}
	
	public String testConnexion() {
		return smsuapiWS.testConnexion();
	}

}
