package org.esupportail.smsu.business;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;











import javax.servlet.http.HttpServletRequest;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UICustomizedGroup;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Business layer concerning smsu service.
 *
 */
public class GroupManager {
	
	@Autowired private DaoService daoService;
	@Autowired private LdapUtils ldapUtils;

	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public GroupManager() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * @param label
	 * @return Boolean
	 */
	public Boolean existsCustomizedGroupLabel(final String label) {
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupByLabel(label);
		return customizedGroup != null; 
		
	}
	
	/**
	 * @param label
	 * @param id
	 * @return Boolean
	 */
	public Boolean existsCustomizedGroupLabelWithOthersIds(final String label, final Integer id) {
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupByLabelWithOtherId(label, id);
		return customizedGroup != null; 
	}
	
	/**
	 * retrieve all the customized groups defined in smsu database.
	 * @return list
	 */
	public List<UICustomizedGroup> getAllGroups() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieve the smsu roles from the database");
		}
		List<UICustomizedGroup> result = new LinkedList<UICustomizedGroup>();
		for (CustomizedGroup group : daoService.getAllCustomizedGroups()) {
			result.add(convertToUI(group));
		}
		return result;
	}

	public UICustomizedGroup convertToUI(CustomizedGroup group) {
		UICustomizedGroup result = new UICustomizedGroup();
		result.id = group.getId();
		result.label = group.getLabel();
		result.quotaSms = group.getQuotaSms();
		result.maxPerSms = group.getQuotaOrder();
		result.consumedSms = group.getConsumedSms();
		result.role = group.getRole().getName();
		result.account = group.getAccount().getLabel();
		result.supervisors = convertToUI(group.getSupervisors());
		result.labelIsUserId= ldapUtils.getGroupNameByIdOrNull(group.getLabel()) == null;
		return result;
	}
	
	public List<String> convertToUI(Set<Person> persons) {
		List<String> result = new ArrayList<String>(); 
		for (Person person : persons) {
			result.add(person.getLogin());
		}
		return result;
	}

	private CustomizedGroup convertFromUI(final UICustomizedGroup uiCGroup, boolean isAddMode, HttpServletRequest request) {
		CustomizedGroup result = new CustomizedGroup();

		if (!isAddMode) {
			result.setId(Integer.valueOf(uiCGroup.id));
		}		
		result.setLabel(uiCGroup.label.trim());
		result.setConsumedSms(Long.parseLong("0"));
		if (request.isUserInRole("FCTN_GESTION_QUOTAS"))
			result.setQuotaSms(uiCGroup.quotaSms);
		if (request.isUserInRole("FCTN_GESTION_QUOTAS"))
			result.setQuotaOrder(uiCGroup.maxPerSms);
		if (request.isUserInRole("FCTN_GESTION_ROLES_AFFECT"))
			result.setRole(daoService.getRoleByName(uiCGroup.role));
		
		// Manage Account
		String account = uiCGroup.account.trim();
		if (daoService.getAccountByLabel(account) == null) {
			daoService.saveAccount(new Account(account)); 
		}
		result.setAccount(daoService.getAccountByLabel(account));
		
		if (request.isUserInRole("FCTN_GESTIONS_RESPONSABLES"))
			result.setSupervisors(convertFromUI(uiCGroup.supervisors));
	
		return result;
	}

	private Set<Person> convertFromUI(List<String> supervisors) {
		Set<Person> personsToAdd = new HashSet<Person>();
		for (String uip : supervisors) {			
			if (daoService.getPersonByLogin(uip) == null) { 
				// add new persons in Person DataBase
				daoService.addPerson(new Person(uip)); 
			}
			personsToAdd.add(daoService.getPersonByLogin(uip));
		}
		return personsToAdd;
	}
		
	public void deleteCustomizedGroup(int id) {
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupById(id);
		logger.info("removing cgroup" + id + " " + customizedGroup.getLabel());
		daoService.deleteCustomizedGroup(customizedGroup);
	}
	
	public void addCustomizedGroup(final UICustomizedGroup uiCGroup, HttpServletRequest request) {
		logger.info("adding cgroup" + uiCGroup.id + " " + uiCGroup.label);
		daoService.addCustomizedGroup(convertFromUI(uiCGroup, true, request));
	}

	public void updateCustomizedGroup(final UICustomizedGroup uiCGroup, HttpServletRequest request) {
		logger.info("modifying cgroup" + uiCGroup.id + " " + uiCGroup.label);
		CustomizedGroup cGroup = convertFromUI(uiCGroup, false, request);
		
		CustomizedGroup persistent = daoService.getCustomizedGroupById(cGroup.getId());
		if (persistent == null) throw new InvalidParameterException("invalid application " + cGroup.getId());

		persistent.setLabel(cGroup.getLabel());
		if (request.isUserInRole("FCTN_GESTION_QUOTAS"))
			persistent.setQuotaSms(cGroup.getQuotaSms());
		if (request.isUserInRole("FCTN_GESTION_QUOTAS"))
			persistent.setQuotaOrder(cGroup.getQuotaOrder());
		persistent.setAccount(cGroup.getAccount());
		if (request.isUserInRole("FCTN_GESTION_ROLES_AFFECT"))
			persistent.setRole(cGroup.getRole());
		if (request.isUserInRole("FCTN_GESTIONS_RESPONSABLES"))
			persistent.setSupervisors(cGroup.getSupervisors());
		
		daoService.updateCustomizedGroup(persistent);
	}
    
	// TODO unused, to remove?
	@SuppressWarnings("unused")
	private String getPreciseDisplayName(Person p) {
		return getPersonPreciseDisplayName(p.getLogin());
	}
	private String getPersonPreciseDisplayName(String login) {
		try {
			return ldapUtils.getUserDisplayNameByUserUid(login)
				+ " (" + login + ")";
		} catch (LdapUserNotFoundException e) {
		    	return login;
		}
	}
	
	/////////////////////////////////////////
	//  setter for spring object daoService
	////////////////////////////////////////
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @return ldapUtils
	 */
	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of userDisplayName
	//////////////////////////////////////////////////////////////
	/**
	 * @param userDisplayName
	 */
	public void setUserDisplayName(final String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	/**
	 * @return userDisplayName
	 */
	public String getUserDisplayName() {
		return userDisplayName;
	}

	


}
