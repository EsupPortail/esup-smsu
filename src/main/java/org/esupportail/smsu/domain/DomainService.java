/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
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
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.smsu.business.SecurityManager;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.services.client.SmsuapiWS;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


public class DomainService implements InitializingBean {

	@Autowired private DaoService daoService;
	@Autowired private LdapUtils ldapUtils;	
	@Autowired private SecurityManager securityManager;	
	@Autowired private SmsuapiWS smsuapiWS;

	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	public DomainService() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Other
	//////////////////////////////////////////////////////////////
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.daoService, 
				"property daoService of class " + this.getClass().getName() + " can not be null");
	}

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
		} catch (LdapException e) {
		}
		return user;
	}
		
	public List<String> getUserRights(String id) {
		return securityManager.loadUserRightsByUsername(id);
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
		List<String> result = new ArrayList<String>();
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
		Map<String, String> result = new HashMap<String,String>();
		result.put(user.getId(), user.getDisplayName());
		return result;
	}
	
	public Map<String, String> getPersons() {
		Map<String, String> result = new HashMap<String,String>();

		List<String> displayNameList = new ArrayList<String>();
		for (Person per : daoService.getPersons()) {
				displayNameList.add(per.getLogin());
				result.put(per.getLogin(), per.getLogin()); // default value
		}
		List<LdapUser> ldapUserList = ldapUtils.getUsersByUids(displayNameList);

		for (LdapUser ldapUser : ldapUserList) {
			String displayName = ldapUtils.getUserDisplayName(ldapUser);
			if (!StringUtils.isEmpty(displayName)) {
				logger.debug("displayName is: " + displayName);	
				result.put(ldapUser.getId(), displayName + "  (" + ldapUser.getId() + ")");
			}
		}
		return result;
	}

	public boolean isSupervisor(User user) {
		Person person = daoService.getPersonByLogin(user.getId());
		return person != null && daoService.isSupervisor(person);
	}

	//////////////////////////////////////////////////////////////
	// Service
	//////////////////////////////////////////////////////////////
	public Service getServiceById(final Integer id) {
		Service service = this.daoService.getServiceById(id);
		return service;
	}
	
	//////////////////////////////////////////////////////////////
	// Recipient
	//////////////////////////////////////////////////////////////
	public Recipient getRecipientByPhone(final String strPhone) {
		return this.daoService.getRecipientByPhone(strPhone);
	}

	public void addRecipient(final Recipient recipient) {
		this.daoService.addRecipient(recipient);
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
    
	public TrackInfos getMessageStatuses(final Integer msgId) {
		return smsuapiWS.getMessageStatus(msgId);
	}

	public Set<String> getListPhoneNumbersInBlackList() {
		return smsuapiWS.getListPhoneNumbersInBlackList();
	}
	
	public String testConnexion() {
		return smsuapiWS.testConnexion();
	}

}
