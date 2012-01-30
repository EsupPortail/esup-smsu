/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.domain;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.application.Version;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.smsu.business.ApprovalManager;
import org.esupportail.smsu.business.FonctionManager;
import org.esupportail.smsu.business.GroupManager;
import org.esupportail.smsu.business.MemberManager;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.PhoneNumbersInBlackListManager;
import org.esupportail.smsu.business.RoleManager;
import org.esupportail.smsu.business.SecurityManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.business.SendTrackManager;
import org.esupportail.smsu.business.ServiceManager;
import org.esupportail.smsu.business.TemplateManager;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.dao.DaoService;

import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.dao.beans.Template;

import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.VersionManager;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.UnknownIdentifierMessageException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.esupportail.smsu.services.client.TestConnexionClient;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UIPerson;
import org.esupportail.smsu.web.beans.UIRole;
import org.esupportail.smsu.web.beans.UIService;
import org.esupportail.smsu.web.beans.UITemplate;
import org.esupportail.smsu.web.beans.UiRecipient;
import org.esupportail.ws.remote.beans.TrackInfos;





import org.springframework.beans.factory.InitializingBean;

/**
 * The basic implementation of DomainService.
 * 
 * See /properties/domain/domain-example.xml
 */
public class DomainServiceImpl implements DomainService, InitializingBean {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -8200845058340254019L;

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link LdapUserService}.
	 */
	private LdapUserService ldapUserService;

		
	/**
	 * {@link MemberManager}.
	 */
	private MemberManager memberManager;

	/**
	 * {@link MessageManager}.
	 */
	private MessageManager messageManager;

	/**
	 * {@link MessageManager}.
	 */
	private ApprovalManager approvalManager;

	/**
	 * {@link SecurityManager}.
	 */
	private SecurityManager securityManager;
	
	/**
	 * {@link SendSmsManager}.
	 */
	private SendSmsManager sendSmsManager;
	
	/**
	 * {@link TemplateManager}.
	 */
	private TemplateManager templateManager;

	/**
	 * {@link sendTrackManager}.
	 */
	private SendTrackManager sendTrackManager;
	
	/**
	 * {@link sendTrackManager}.
	 */
	private PhoneNumbersInBlackListManager phoneNumbersInBlackListManager;
	
	private TestConnexionClient testConnexion;
	
	/**
	 * 
	 */
	//private TestConnexionClient testConnexion;
	/**
	 * The LDAP attribute that contains the display name. 
	 */
	private String displayNameLdapAttribute;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * {@link ServiceManager}.
	 */
	private ServiceManager serviceManager;
	
	/**
	 * {@link RoleManager}.
	 */
	private RoleManager roleManager;
	
	/**
	 * {@link GroupManager}.
	 */
	private GroupManager groupManager;
	
	/**
	 * {@link FonctionManager}.
	 */
	private FonctionManager fonctionManager;

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public DomainServiceImpl() {
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
		Assert.notNull(this.ldapUserService, 
				"property ldapUserService of class " + this.getClass().getName() + " can not be null");
		Assert.hasText(this.displayNameLdapAttribute, 
				"property displayNameLdapAttribute of class " + this.getClass().getName() 
				+ " can not be null");
	}

	//////////////////////////////////////////////////////////////
	// User
	//////////////////////////////////////////////////////////////
	/**
	 * Set the information of a user from a ldapUser.
	 * @param user 
	 * @param ldapUser 
	 * @return true if the user was updated.
	 */
	private boolean setUserInfo(
			final User user, 
			final LdapUser ldapUser) {
		String displayName = null;
		List<String> displayNameLdapAttributes = ldapUser.getAttributes().get(displayNameLdapAttribute);
		if (displayNameLdapAttributes != null) {
			displayName = displayNameLdapAttributes.get(0);
		}
		if (displayName == null) {
			displayName = user.getId();
		}
		if (displayName.equals(user.getDisplayName())) {
			return false;
		}
		user.setDisplayName(displayName);
		return true;
	}

	/**
	 * create user from a LDAP search.
	 * @see org.esupportail.smsu.domain.DomainService#getUser(java.lang.String)
	 */
	public User getUser(final String id) throws UserNotFoundException {
			LdapUser ldapUser = this.ldapUserService.getLdapUser(id);
			User user = new User();
			user.setId(ldapUser.getId());
			setUserInfo(user, ldapUser);
		user.setFonctions(securityManager.loadUserRightsByUsername(user.getId()));
		user.setRoles(securityManager.loadUserRolesByUsername(user.getId()));
		return user;
	}

	/**
	 * @param displayNameLdapAttribute the displayNameLdapAttribute to set
	 */
	public void setDisplayNameLdapAttribute(final String displayNameLdapAttribute) {
		this.displayNameLdapAttribute = displayNameLdapAttribute;
	}

	//////////////////////////////////////////////////////////////
	// VersionManager
	//////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getDatabaseVersion()
	 */
	public Version getDatabaseVersion() throws ConfigException {
		VersionManager versionManager = daoService.getVersionManager();
		if (versionManager == null) {
			return null;
		}
		return new Version(versionManager.getVersion());
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#setDatabaseVersion(java.lang.String)
	 */
	public void setDatabaseVersion(final String version) {
		if (logger.isDebugEnabled()) {
			logger.debug("setting database version to '" + version + "'...");
		}
		VersionManager versionManager = daoService.getVersionManager();
		versionManager.setVersion(version);
		daoService.updateVersionManager(versionManager);
		if (logger.isDebugEnabled()) {
			logger.debug("database version set to '" + version + "'.");
		}
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#setDatabaseVersion(
	 * org.esupportail.commons.services.application.Version)
	 */
	public void setDatabaseVersion(final Version version) {
		setDatabaseVersion(version.toString());
	}

	//////////////////////////////////////////////////////////////
	// Authorizations
	//////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.smsu.domain.DomainService#checkRights
	 */
	public boolean checkRights(final List<String> fonctions, final Set<String> rights) {
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
	 * @param ldapUserService the ldapUserService to set
	 */
	public void setLdapUserService(final LdapUserService ldapUserService) {
		this.ldapUserService = ldapUserService;
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
	public List<UIMessage> getApprovalUIMessages(final String idUser) {
		return  approvalManager.getApprovalUIMessages(idUser);
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#updateUIMessage(org.esupportail.smsu.web.beans.UIMessage)
	 */
	public void updateUIMessage(final UIMessage uiMessage) {
		approvalManager.updateUIMessage(uiMessage);
	}

	/**
	 * @throws CreateMessageException 
	 * @see org.esupportail.smsu.domain.DomainService#treatMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void treatUIMessage(final UIMessage uimessage) throws CreateMessageException.WebService {
		approvalManager.treatUIMessage(uimessage);
		
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
	/**
	 * @see org.esupportail.example.domain.DomainService#getDepartments()
	 */
	public List<Account> getAccounts() {
		return  this.daoService.getAccounts();

	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getAccountById(java.lang.Integer)
	 */
	public Account getAccountById(final Integer id) {
		return this.daoService.getAccountById(id);
	}

	//////////////////////////////////////////////////////////////
	// Person
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.domain.DomainService#getDepartments()
	 */
	public List<Person> getPersons() {
		return this.daoService.getPersons();
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getPersonByLogin(java.lang.String)
	 */
	public Person getPersonByLogin(final String login) {
		return this.daoService.getPersonByLogin(login);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#addPerson(org.esupportail.smsu.dao.beans.Person)
	 */
	public void addPerson(final Person person) {
		this.daoService.addPerson(person);
		
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
    
	
	//////////////////////////////////////////////////////////////
	// Used by MessagesController
	//////////////////////////////////////////////////////////////
	/**
	 * get the number of persons whose the Message sent.
	 * get the number of persons in black list.
	 * get the number of persons received Message.
	 * @param msgId 
	 * @throws UnknownIdentifierApplicationException 
	 */
	public TrackInfos getTrackInfos(final Integer msgId) 
				throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException {
		try  {
		 return sendTrackManager.getTrackInfos(msgId);
		} catch (UnknownIdentifierApplicationException e1) {
			throw e1;
		}  catch (UnknownIdentifierMessageException e2) {
			throw e2;
		}
	}
	 
	
	/**
	 * @param uiRecipients 
	 * @param login 
	 * @param content 
	 * @param smsTemplate 
	 * @param userGroup 
	 * @param serviceId 
	 * @param mail 
	 * @return a message.
	 * @throws CreateMessageException 
	 * @see org.esupportail.smsu.domain.DomainService#composeMessage(...)
	 */
	public Message composeMessage(final List<UiRecipient> uiRecipients, final String login,
			final String content, final String smsTemplate, final String userGroup,
			final Integer serviceId, final MailToSend mail) throws CreateMessageException {
		Message message = sendSmsManager.createMessage(uiRecipients, login, 
				content, smsTemplate, userGroup, 
				serviceId, mail);
		daoService.addMessage(message);
		return message;
	}

	/**
	 * @throws CreateMessageException
	 * @see org.esupportail.smsu.domain.DomainService#treatMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void treatMessage(final Message message) throws CreateMessageException.WebService {
		sendSmsManager.treatMessage(message);
	}

	/**
	 * @param portalGroupId
	 * @return the path to the parent customized group corresponding to a group
	 */
	public String getRecursiveGroupPathByLabel(String portalGroupId) {
		return sendSmsManager.getRecursiveGroupPathByLabel(portalGroupId);
	}

	//////////////////////////////////////////////////////////////
	// Role
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.domain.DomainService#getAllRoles()
	 */
	public List<UIRole> getAllRoles(final List<Integer> idRoles) {
		return roleManager.getAllRoles(idRoles);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#getAllRoles()
	 */
	public List<UIRole> getAllRoles() {
		return roleManager.getAllRoles();
	}
	
	public void saveRole(final UIRole role, final List<String> selectedValues) {
		roleManager.saveRole(role, selectedValues);
	}
	
	public void deleteRole(final UIRole role) {
		roleManager.deleteRole(role);
	}

	public void updateRole(final UIRole role, final List<String> selectedValues) {
		roleManager.updateRole(role, selectedValues);
	}
	
	public List<String> getIdFctsByRole(final UIRole role) {
		return roleManager.getIdFctsByRole(role);
	}

	//////////////////////////////////////////////////////////////
	// Fonction
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.domain.DomainService#getAllRoles()
	 */
	public List<Fonction> getAllFonctions() {
		return fonctionManager.getAllFonctions();
	}

	/**
	 * @param templateManager
	 */
	public void setTemplateManager(final TemplateManager templateManager) {
		this.templateManager = templateManager;
	}

	/**
	 * @return templateManager
	 */
	public TemplateManager getTemplateManager() {
		return templateManager;
	}

	
	/*public TestConnexionClient getTestConnexion() {
		return testConnexion;
	}

	public void setTestConnexion(final TestConnexionClient testConnexion) {
		this.testConnexion = testConnexion;
	}*/

	/**
	 * @see org.esupportail.smsu.domain.DomainService#addTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void addUITemplate(final UITemplate template) {
		templateManager.addUITemplate(template);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#deleteTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void deleteUITemplate(final UITemplate template) {
		templateManager.deleteUITemplate(template);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#isLabelAvailable(java.lang.String, java.lang.Integer)
	 */
	public Boolean isTemplateLabelAvailable(final String label, final Integer id) {
		return templateManager.isLabelAvailable(label, id);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#testMailsBeforeDeleteTemplate
	 */
	public Boolean testMailsBeforeDeleteTemplate(final Template template) {
		return templateManager.testMailsBeforeDeleteTemplate(template);
	}

	/**
	 * @see org.esupportail.smsu.domain.DomainService#testMessagesBeforeDeleteTemplate
	 */
	public Boolean testMessagesBeforeDeleteTemplate(final Template template) {
		return templateManager.testMessagesBeforeDeleteTemplate(template);
	}


	/**
	 * @see org.esupportail.smsu.domain.DomainService#updateUITemplate(org.esupportail.smsu.web.beans.UITemplate)
	 */
	public void updateUITemplate(final UITemplate template) {
		templateManager.updateUITemplate(template);
		
	}

	
	public Set<String> getListPhoneNumbersInBlackList() {
		
		final Set<String> retVal = phoneNumbersInBlackListManager.getListPhoneNumbersInBlackList();
		
		return retVal;
	}
	
	
	/**
	 * @param sendTrackManager the sendTrackManager to set
	 */
	
	public void setSendTrackManager(final SendTrackManager sendTrackManager) {
		this.sendTrackManager = sendTrackManager;
	}

	/**
	 * @return the sendTrackManager
	 */
	
	public SendTrackManager getSendTrackManager() {
		return sendTrackManager;
	}

	public void setPhoneNumbersInBlackListManager(
			final PhoneNumbersInBlackListManager phoneNumbersInBlackListManager) {
		this.phoneNumbersInBlackListManager = phoneNumbersInBlackListManager;
	}
	
	/**
	 * @see org.esupportail.smsu.domain.DomainService#testConnexion()
	 */
	public String testConnexion() {

		return testConnexion.testConnexion();
	}

	public TestConnexionClient getTestConnexion() {
		return testConnexion;
	}

	public void setTestConnexion(final TestConnexionClient testConnexion) {
		this.testConnexion = testConnexion;
	}
	
}
