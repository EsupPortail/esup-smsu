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

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public DomainService() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Other
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.daoService, 
				"property daoService of class " + this.getClass().getName() + " can not be null");
	}

	/**
	 * create user from a LDAP search or create a simple one
	 * @see org.esupportail.smsu.domain.DomainService#getUser(java.lang.String)
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
	

	/**
	 * @param displayNameLdapAttribute the displayNameLdapAttribute to set
	 */
	public void setDisplayNameLdapAttribute(final String displayNameLdapAttribute) {
		this.displayNameLdapAttribute = displayNameLdapAttribute;
	}

	//////////////////////////////////////////////////////////////
	// Authorizations
	//////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.smsu.domain.DomainService#checkRights
	 */
	public boolean checkRights(final List<String> fonctions, final Set<FonctionName> rights) {
		return securityManager.checkRights(fonctions, rights);
	}
	//////////////////////////////////////////////////////////////
	// Misc
	//////////////////////////////////////////////////////////////
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	/**
	 * @param ldapService the ldapService to set
	 */
	public void setLdapService(final LdapUserAndGroupService ldapService) {
		this.ldapService = ldapService;
	}

	//////////////////////////////////////////////////////////////
	// Message
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages.
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 */
	public List<UIMessage> getMessages(final Integer userGroupId, final Integer userAccountId, 
			final Integer userServiceId, final Integer userTemplateId, final Integer userUserId, 
			final Date beginDate, final Date endDate) {
		return  messageManager.getMessages(userGroupId, userAccountId, userServiceId, userTemplateId, 
				userUserId, beginDate, endDate);

	}

	/**
	 * {@inheritDoc}
	 * @see org.esupportail.smsu.domain.DomainService#getMessage(java.lang.Integer)
	 */
	public Message getMessage(Integer messageId) {
		return messageManager.getMessage(messageId);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#addMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void addMessage(final Message message) {
		this.daoService.addMessage(message);

	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#updateMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void updateMessage(final Message message) {
		this.daoService.updateMessage(message);

	}

	//////////////////////////////////////////////////////////////
	// Approval Message
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages to approve.
	 */
	public List<UIMessage> getApprovalUIMessages(final User user) {
		return  approvalManager.getApprovalUIMessages(user);
	}
	
	public void cancelMessage(final UIMessage uiMessage, User currentUser) {
		approvalManager.cancelMessage(uiMessage, currentUser);
	}

	/**
	 * @throws CreateMessageException 
	 */
	public void approveMessage(final UIMessage uimessage, User currentUser) throws CreateMessageException.WebService {
		approvalManager.approveMessage(uimessage, currentUser);
		
	}
	//////////////////////////////////////////////////////////////
	// Group
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.domain.DomainService#getDepartments()
	 */
	public List<BasicGroup> getGroups() {
		return this.daoService.getGroups();
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getGroupByLabel(java.lang.String)
	 */
	public BasicGroup getGroupByLabel(final String group) {
		return this.daoService.getGroupByLabel(group);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#addBasicGroup(org.esupportail.smsu.dao.beans.BasicGroup)
	 */
	public void addBasicGroup(final BasicGroup group) {
		this.daoService.addBasicGroup(group);
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
	/**
	 * @see org.esupportail.example.domain.DomainService#getDepartments()
	 */
	public List<Service> getServices() {
		return this.daoService.getServices();
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#updateService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void updateUIService(final UIService uiService) {
		serviceManager.updateUIService(uiService);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#getServiceById(java.lang.Integer)
	 */
	public Service getServiceById(final Integer id) {
		Service service = this.daoService.getServiceById(id);
		return service;
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#addService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void addUIService(final UIService uiService) {
		serviceManager.addUIService(uiService);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#isServiceNameAvailable(java.lang.String, java.lang.Integer)
	 */
	public Boolean isServiceNameAvailable(final String name, final Integer id) {
		return serviceManager.isNameAvailable(name, id);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#isServiceKeyAvailable(java.lang.String, java.lang.Integer)
	 */
	public Boolean isServiceKeyAvailable(final String key, final Integer id) {
		return serviceManager.isKeyAvailable(key, id);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#deleteService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void deleteUIService(final UIService uiService) {
		serviceManager.deleteUIService(uiService);
	}
	
	//////////////////////////////////////////////////////////////
	// Template
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.domain.DomainService#getDepartments()
	 */
	public List<Template> getTemplates() {
		return this.templateManager.getTemplates();
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getTemplateById(java.lang.Integer)
	 */
	public Template getTemplateById(final Integer id) {
		return this.templateManager.getTemplateById(id);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getUiTemplates()
	 */
	public List<UITemplate> getUITemplates() {
		return this.templateManager.getUITemplates();
	}
	
	//////////////////////////////////////////////////////////////
	// Recipient
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.domain.DomainService#getRecipientByPhone(java.lang.String)
	 */
	public Recipient getRecipientByPhone(final String strPhone) {
		return this.daoService.getRecipientByPhone(strPhone);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#addRecipient(org.esupportail.smsu.dao.beans.Recipient)
	 */
	public void addRecipient(final Recipient recipient) {
		this.daoService.addRecipient(recipient);
	}

	
	//////////////////////////////////////////////////////////////
	// Customized groups
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.domain.DomainService#checkCustomizedGroupLabel
	 */
	public Boolean checkCustomizedGroupLabel(final String label) {
		return groupManager.checkCustomizedGroupLabel(label);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#checkCustomizedGroupLabel
	 */
	public Boolean checkCustomizedGroupLabelWithOthersIds(final String label, final Integer id) {
		return groupManager.checkCustomizedGroupLabelWithOthersIds(label, id);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#addCustomizedGroup
	 */
	public void addCustomizedGroup(final CustomizedGroup customizedGroup) {
		this.daoService.addCustomizedGroup(customizedGroup);		
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getCustomizedGroupByLabel(java.lang.String)
	 */
	public CustomizedGroup getCustomizedGroupByLabel(final String label) {
		return this.daoService.getCustomizedGroupByLabel(label);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getFirstCustomizedGroup()
	 */
	public CustomizedGroup getFirstCustomizedGroup() {
		return this.daoService.getFirstCustomizedGroup();
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getAllGroupes()
	 */
	public List<CustomizedGroup> getAllGroups() {
		List<CustomizedGroup> allGroups = groupManager.getAllGroups();
		return allGroups;
		
	}
	
	
	public void deleteCustomizedGroup(final CustomizedGroup customizedGroup) {
		groupManager.deleteCustomizedGroup(customizedGroup);
	}
	
	public void addCustomizedGroup(final CustomizedGroup customizedGroup, final UIRole role, final Account account, 
			final List<UIPerson> persons) {
		groupManager.addCustomizedGroup(customizedGroup, role, account, persons);
	}
	
	public void updateCustomizedGroup(final CustomizedGroup customizedGroup, final UIRole role, 
			final Account account, final Long quotaAdd, final List<UIPerson> persons) {
		groupManager.updateCustomizedGroup(customizedGroup, role, account, quotaAdd, persons);
	}
	
	public void updateCustomizedGroup(final CustomizedGroup customizedGroup) {
		groupManager.updateCustomizedGroup(customizedGroup);
	}
	
	public List<UIPerson> getPersonsByIdCustomizedGroup(final Integer id) {
		return groupManager.getPersonsByIdCustomizedGroup(id);
	}
	
	//////////////////////////////////////////////////////////////
	// Member 
	//////////////////////////////////////////////////////////////
	/** 
	 * @throws LdapUserNotFoundException 
	 * @see org.esupportail.smsu.domain.DomainService#getMember(java.lang.String)
	 */
	public Member getMember(final String userIdentifier) throws LdapUserNotFoundException {
		return memberManager.getMember(userIdentifier);
	}

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) {
		if (logger.isDebugEnabled()) {
			logger.debug("Request in domaineService : " + phoneNumber);
		}
		Boolean retVal = memberManager.isPhoneNumberInBlackList(phoneNumber); 
		if (logger.isDebugEnabled()) {
			logger.debug("Response return in domaineService for : " 
						 + phoneNumber + " is : " + retVal);
		}
		
		return retVal;
	}

	public String checkWhySmsuapiFailed(Throwable cause) {
		return sendSmsManager.checkWhySmsuapiFailed(cause);
	}

	/** 
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 * @see org.esupportail.smsu.domain.DomainService#getMember(java.lang.String)
	 */
	public void saveOrUpdateMember(final Member member) throws LdapUserNotFoundException, LdapWriteException {
		memberManager.saveOrUpdateMember(member);
	}

	/** 
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 * @see org.esupportail.smsu.domain.DomainService#validMember(org.esupportail.smsu.business.beans.Member)
	 */
	public boolean validMember(final Member member) throws LdapUserNotFoundException, LdapWriteException {
		return memberManager.valid(member);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getAllServices()
	 */
	public List<Service> getAllServices() {
		return serviceManager.getAllServices();
	}

	/**
	 * @return all the services in the format used for display.
	 */
	public List<UIService> getAllUIServices() {
		return serviceManager.getAllUIServices();
	}

	//////////////////////////////////////////////////////////////
	// Mutator
	//////////////////////////////////////////////////////////////
	/**
	 * @return memberManager
	 */
	public MemberManager getMemberManager() {
		return memberManager;
	}
	/**
	 * set memberManager.
	 */
	public void setMemberManager(final MemberManager memberManager) {
		this.memberManager = memberManager;
	}

	/**
	 * set messageManager.
	 */
	public void setMessageManager(final MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	/**
	 * set approvalManager.
	 */
	public void setApprovalManager(final ApprovalManager approvalManager) {
		this.approvalManager = approvalManager;
	}

	/**
	 * set serviceManager.
	 */
	public void setServiceManager(final ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	/**
	 * set securityManager.
	 */
	public void setSecurityManager(final SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	/**
	 * set sendSmsManager.
	 */
	public void setSendSmsManager(final SendSmsManager sendSmsManager) {
		this.sendSmsManager = sendSmsManager;
	}

	/**
	 * @return sendSmsManager
	 */
	public SendSmsManager getSendSmsManager() {
		return sendSmsManager;
	}

	/**
	 * set roleManager.
	 */
	public void setRoleManager(final RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	/**
	 * set groupManager.
	 */
	public void setGroupManager(final GroupManager groupManager) {
		this.groupManager = groupManager;
	}
	
	/**
	 * set fonctionManager.
	 */
	public void setFonctionManager(final FonctionManager fonctionManager) {
		this.fonctionManager = fonctionManager;
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

	//////////////////////////////////////////////////////////////
	// Mutator
	//////////////////////////////////////////////////////////////
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	public void setSecurityManager(final SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
		
	public void setSmsuapiWS(SmsuapiWS smsuapiWS) {
		this.smsuapiWS = smsuapiWS;
	}

	public void setLdapUtils(LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

}
