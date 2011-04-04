package org.esupportail.smsu.business;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIPerson;
import org.esupportail.smsu.web.beans.UIRole;

/**
 * Business layer concerning smsu service.
 *
 */
public class GroupManager {
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;
	
	/**
	 * ldap service.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * user display name ldap attribute.
	 */
	private String userDisplayName;
	
	/**
	 * Log4j logger.
	 */
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
	public Boolean checkCustomizedGroupLabel(final String label) {
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupByLabel(label);
		return customizedGroup != null; 
		
	}
	
	/**
	 * @param label
	 * @param id
	 * @return Boolean
	 */
	public Boolean checkCustomizedGroupLabelWithOthersIds(final String label, final Integer id) {
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupByLabelWithOtherId(label, id);
		return customizedGroup != null; 
	}
	
	/**
	 * retrieve all the customized groups defined in smsu database.
	 * @return list
	 */
	public List<CustomizedGroup> getAllGroups() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieve the smsu roles from the database");
		}
		List<CustomizedGroup> allgroups = daoService.getAllCustomizedGroups();
		
		return allgroups;
	}
	
	/**
	 * delete customizedGroup.
	 * @return
	 */
	public void deleteCustomizedGroup(final CustomizedGroup customizedGroup) {
		daoService.deleteCustomizedGroup(customizedGroup);
	}
	
	
	/**
	 * add customizedGroup.
	 * @return
	 */
	public void addCustomizedGroup(final CustomizedGroup customizedGroup, final UIRole role, 
			final Account account, final List<UIPerson> persons) {
		CustomizedGroup newcustomizedGroup = new CustomizedGroup();
		
		// Manage CustomizedGroup Table fields
		newcustomizedGroup.setLabel(customizedGroup.getLabel().trim());
		newcustomizedGroup.setQuotaSms(customizedGroup.getQuotaSms() != null ?
					       customizedGroup.getQuotaSms() :
					       Long.parseLong("0"));
		newcustomizedGroup.setQuotaOrder(customizedGroup.getQuotaOrder() != null ?
						 customizedGroup.getQuotaOrder() :
						 Long.parseLong("0"));
		newcustomizedGroup.setConsumedSms(Long.parseLong("0"));
		
		// Manage Role
		Role newrole = daoService.getRoleById(role.getId());
		newcustomizedGroup.setRole(newrole);
		
		// Manage Account
		if (daoService.getAccountByLabel(account.getLabel().trim()) == null) { 
			daoService.saveAccount(account); 
		}
		Account newaccount = daoService.getAccountByLabel(account.getLabel().trim());	
		newcustomizedGroup.setAccount(newaccount);
		
		// Manage Supervisors
		
			// 1 - add new persons in Person DataBase
		for (UIPerson uip : persons) {
			Person per = new Person();
			per.setLogin(uip.getLogin().trim());
			
			if (daoService.getPersonByLogin(per.getLogin().trim()) == null) { 
				daoService.addPerson(per); 
			}
		}
			// 2 - add supervisors in SUPERVISOR Data Table
		Set<Person> personsToAdd = new HashSet<Person>();
		for (UIPerson uip : persons) {
			Person perSup = new Person();
			perSup = daoService.getPersonByLogin(uip.getLogin().trim());
			personsToAdd.add(perSup);
		    }
		
		newcustomizedGroup.setSupervisors(personsToAdd);
		
		// save newcustomizedGroup
		daoService.addCustomizedGroup(newcustomizedGroup);
	}
	
	
	/**
	 * update customizedGroup.
	 * @return
	 */
	public void updateCustomizedGroup(final CustomizedGroup customizedGroup, final UIRole role, 
			final Account account, final Long quotaAdd, final List<UIPerson> persons) {
		CustomizedGroup newcustomizedGroup = new CustomizedGroup();
		
		// Manage CustomizedGroup Table fields
		newcustomizedGroup.setId(customizedGroup.getId());
		newcustomizedGroup.setLabel(customizedGroup.getLabel().trim());
		newcustomizedGroup.setQuotaSms(customizedGroup.getQuotaSms() + quotaAdd);
		newcustomizedGroup.setQuotaOrder(customizedGroup.getQuotaOrder());	
		newcustomizedGroup.setConsumedSms(customizedGroup.getConsumedSms());
		
		// Manage Role
		Role newrole = daoService.getRoleById(role.getId());
		newcustomizedGroup.setRole(newrole);
		
		// Manage Account
		if (daoService.getAccountByLabel(account.getLabel().trim()) == null) { 
			daoService.saveAccount(account); 
		}
		Account newaccount = daoService.getAccountByLabel(account.getLabel().trim());	
		newcustomizedGroup.setAccount(newaccount);
		
		// Manage Supervisors
		
			// 1 - add new persons in Person DataBase
		for (UIPerson uip : persons) {
			Person per = new Person();
			per.setLogin(uip.getLogin().trim());
			
			if (daoService.getPersonByLogin(per.getLogin().trim()) == null) { 
				daoService.addPerson(per); 
			}
		}
			// 2 - add supervisors in SUPERVISOR Data Table
		Set<Person> personsToAdd = new HashSet<Person>();
		for (UIPerson uip : persons) {
			Person perSup = new Person();
			perSup = daoService.getPersonByLogin(uip.getLogin().trim());
			personsToAdd.add(perSup);
		    }
		
		newcustomizedGroup.setSupervisors(personsToAdd);
		// save newcustomizedGroup
		daoService.updateCustomizedGroup(newcustomizedGroup);
	}
	
	public void updateCustomizedGroup(final CustomizedGroup customizedGroup) {
		daoService.updateCustomizedGroup(customizedGroup);
		
	}
	
	/**
	 * Retrieve customizedGroup by id.
	 * @return
	 */
	public List<UIPerson> getPersonsByIdCustomizedGroup(final Integer id) {
		List<UIPerson> persons = new ArrayList<UIPerson>(); 
		
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupById(id);
		Set<Person> per = customizedGroup.getSupervisors();
		
		for (Person pert : per) {
			UIPerson uiper = new UIPerson();
			uiper.setId(pert.getId().toString());
			uiper.setLogin(pert.getLogin());
		    try { 
			uiper.setDisplayName(ldapUtils.getUserDisplayNameByUserUid(pert.getLogin())
					+ " (" + pert.getLogin() + ")");	
		    } catch (LdapUserNotFoundException e) {
		    	uiper.setDisplayName(pert.getLogin());
		    } 
			persons.add(uiper);
		    }
		
		return persons;
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
