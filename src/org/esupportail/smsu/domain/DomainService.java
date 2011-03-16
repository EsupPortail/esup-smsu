/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.application.Version;
import org.esupportail.commons.web.beans.Paginator;

import org.esupportail.smsu.business.beans.Member;
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
import org.esupportail.smsu.exceptions.BackOfficeUnrichableException;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.UnknownIdentifierMessageException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UIPerson;
import org.esupportail.smsu.web.beans.UIRole;
import org.esupportail.smsu.web.beans.UIService;
import org.esupportail.smsu.web.beans.UITemplate;
import org.esupportail.smsu.web.beans.UiRecipient;
import org.esupportail.ws.remote.beans.TrackInfos;



/**
 * The domain service interface.
 */
public interface DomainService extends Serializable {

	//////////////////////////////////////////////////////////////
	// User
	//////////////////////////////////////////////////////////////
	/**
	 * @param id
	 * @return the User instance that corresponds to an id.
	 * @throws UserNotFoundException
	 */
	User getUser(String id) throws UserNotFoundException;

	/**
	 * @return the list of all the users.
	 */
	List<User> getUsers();

	/**
	 * Update a user.
	 * @param user
	 */
	void updateUser(User user);

	/**
	 * Update a user's information (retrieved from the LDAP directory for instance).
	 * @param user
	 */
	void updateUserInfo(User user);
	
	/**
	 * Add an administrator.
	 * @param user
	 */
	void addAdmin(User user);

	/**
	 * Delete an administrator.
	 * @param user
	 */
	void deleteAdmin(User user);

	/**
	 * @return a paginator for administrators.
	 */
	Paginator<User> getAdminPaginator();

	//////////////////////////////////////////////////////////////
	// VersionManager
	//////////////////////////////////////////////////////////////
	/**
	 * @return the database version.
	 * @throws ConfigException when the database is not initialized
	 */
	Version getDatabaseVersion() throws ConfigException;
	
	/**
	 * Set the database version.
	 * @param version 
	 */
	void setDatabaseVersion(Version version);
	
	/**
	 * Set the database version.
	 * @param version 
	 */
	void setDatabaseVersion(String version);
	
	//////////////////////////////////////////////////////////////
	// Authorizations
	//////////////////////////////////////////////////////////////
	/**
	 * @param currentUser
	 * @return 'true' if the user can view administrators.
	 */
	boolean userCanViewAdmins(User currentUser);
	
	/**
	 * @param user 
	 * @return 'true' if the user can grant the privileges of administrator.
	 */
	boolean userCanAddAdmin(User user);

	/**
	 * @param user 
	 * @param admin
	 * @return 'true' if the user can revoke the privileges of an administrator.
	 */
	boolean userCanDeleteAdmin(User user, User admin);
	
	/**
	 * @param fonctions 
	 * @param rights
	 * @return 'true' if less one of rights belong fonctions.
	 */
	boolean checkRights(List<String> fonctions, Set<String> rights);
	
	//////////////////////////////////////////////////////////////
	// Message
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages.
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 */
	List<UIMessage> getMessages(Integer userGroupId, Integer userAccountId, Integer userServiceId, 
			Integer userTemplateId, Integer userUserId, Date beginDate, Date endDate);

	/**
	 * @param messageId
	 * @return the message
	 */
	Message getMessage(Integer messageId);
	
	/**
	 * @param message
	 */
	void addMessage(Message message);
	
	/**
	 * @param message
	 */
	void updateMessage(Message message);

	//////////////////////////////////////////////////////////////
	// Approval Message
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages to approve.
	 */
	List<UIMessage> getApprovalUIMessages(String idUser);
	
	/**
	 * @param uiMessage 
	 */
	void updateUIMessage(UIMessage uiMessage);
	
	/**
	 * treat a message.
	 * @param message 
	 * @return the message
	 * @throws BackOfficeUnrichableException 
	 * @throws LdapUserNotFoundException 
	 */
	void treatUIMessage(UIMessage uimessage);

	//////////////////////////////////////////////////////////////
	// Group
	//////////////////////////////////////////////////////////////
	/**
	 * @return the groups.
	 */
	List<BasicGroup> getGroups();
	
	/**
	 * @param group
	 * @return the group.
	 */
	BasicGroup getGroupByLabel(String group);
	
	/**
	 * @param group
	 */
	void addBasicGroup(BasicGroup group);

	//////////////////////////////////////////////////////////////
	// Account
	//////////////////////////////////////////////////////////////
	/**
	 * @return the accounts.
	 */
	List<Account> getAccounts();
	
	/**
	 * @param id 
	 * @return
	 */
	Account getAccountById(Integer id);

	//////////////////////////////////////////////////////////////
	// Person
	//////////////////////////////////////////////////////////////
	/**
	 * @return the persons.
	 */
	List<Person> getPersons();
	
	/**
	 * @param login
	 * @return the person.
	 */
	Person getPersonByLogin(String login);
	
	/**
	 * @param person
	 */
	void addPerson(Person person);

	//////////////////////////////////////////////////////////////
	// Service
	//////////////////////////////////////////////////////////////
	/**
	 * @return the services.
	 */
	List<Service> getServices();
	
	/**
	 * @param uiService 
	 */
	void addUIService(UIService uiService);
	
	/**
	 * @param uiService 
	 */
	void updateUIService(UIService uiService);
	
	/**
	 * @param uiService 
	 */
	void deleteUIService(UIService uiService);
	
	/**
	 * @return the Uiservices
	 */
	List<UIService> getAllUIServices();
	
	/**
	 * @param id 
	 * @return the service
	 */
	Service getServiceById(Integer id);
	
	/**
	 * @param name
	 * @param id 
	 * @return true if the name is available
	 */
	Boolean isServiceNameAvailable(String name, Integer id);
	
	/**
	 * @param key
	 * @param id 
	 * @return true if the key is available
	 */
	Boolean isServiceKeyAvailable(String key, Integer id);

	//////////////////////////////////////////////////////////////
	// Template
	//////////////////////////////////////////////////////////////
	/**
	 * @return the templates.
	 */
	List<Template> getTemplates();
	
	/**
	 * @return the UI templates
	 */
	List<UITemplate> getUITemplates();
	/**
	 * @param id 
	 * @return the template.
	 */
	Template getTemplateById(Integer id);
	
	/**
	 * @param template
	 */
	void deleteUITemplate(UITemplate template);
	
	/**
	 * @param template
	 */
	void addUITemplate(UITemplate template);
	
	/**
	 * @param template
	 */
	void updateUITemplate(UITemplate template);
	
	/**
	 * @param label
	 * @param id
	 * @return true if the label is available
	 */
	Boolean isTemplateLabelAvailable(String label, Integer id);
	
	/**
	 * @param template
	 * @return true if no message used the template
	 */
	Boolean testMessagesBeforeDeleteTemplate(Template template);
	
	/**
	 * @param template
	 * @return true if no mail used the template
	 */
	Boolean testMailsBeforeDeleteTemplate(Template template);
	
	//////////////////////////////////////////////////////////////
	// Recipient
	//////////////////////////////////////////////////////////////
	/**
	 * @param strPhone 
	 * @return the recipient.
	 */
	Recipient getRecipientByPhone(String strPhone);
	
	/**
	 * @param recipient
	 */
	void addRecipient(Recipient recipient);
	
	//////////////////////////////////////////////////////////////
	// Customized groups
	//////////////////////////////////////////////////////////////
	/**
	 * @param label
	 * @return Boolean
	 */
	Boolean checkCustomizedGroupLabel(String label);

	/**
	 * @param label
	 * @param id
	 * @return Boolean
	 */
	Boolean checkCustomizedGroupLabelWithOthersIds(String label, Integer id);
	
	/**
	 * @param label
	 * @return the customized group
	 */
	CustomizedGroup getCustomizedGroupByLabel(String label);
	
	/**
	 * @return the first customized group from the table.
	 */
	CustomizedGroup getFirstCustomizedGroup();
	
	/**
	 * add a customized group.
	 * @param customizedGroup 
	 */
	void addCustomizedGroup(CustomizedGroup customizedGroup);

	/**
	 * get the list of customized group.
	 */
	List<CustomizedGroup> getAllGroups();
	
	/**
	 * delete customizedGroup.
	 * @param customizedGroup
	 */
	void deleteCustomizedGroup(CustomizedGroup customizedGroup);
	
	/**
	 * add a customized group.
	 * @param customizedGroup 
	 * @param role
	 * @param account
	 */
	void addCustomizedGroup(CustomizedGroup customizedGroup, UIRole role, Account account, List<UIPerson> persons);

	/**
	 * update a customized group.
	 * @param customizedGroup 
	 * @param role
	 * @param account
	 */
	void updateCustomizedGroup(CustomizedGroup customizedGroup, UIRole role, 
			Account account, Long quotaAdd, List<UIPerson> persons);
	
	/**
	 * update a customized group.
	 * @param customizedGroup
	 */
	void updateCustomizedGroup(CustomizedGroup customizedGroup);
	
	/**
	 * @param id
	 */
	List<UIPerson> getPersonsByIdCustomizedGroup(Integer id);
	
	//////////////////////////////////////////////////////////////
	// Member
	//////////////////////////////////////////////////////////////
	/**
	 * get a member based on his identifier.
	 * @param userIdentifier
	 * @return
	 * @throws LdapUserNotFoundException 
	 */
	Member getMember(final String userIdentifier) throws LdapUserNotFoundException;

	/**
	 * save or update the given member.
	 * @param member
	 * @throws LdapUserNotFoundException 
	 */
	void saveOrUpdateMember(final Member member) throws LdapUserNotFoundException;
	
	/**
	 * test if the the code entered by the pending member is correct.
	 * If it is the case, accept definitely this member in the SMSU.
	 * @param member
	 * @return a boolean that indicates if the member is accepted
	 * @throws LdapUserNotFoundException 
	 */
	boolean validMember(Member member) throws LdapUserNotFoundException;


	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	boolean isPhoneNumberInBlackList(String phoneNumber);
	
	
    //////////////////////////////////////////////////////////////
	// Used by Date methods in MessagesController
	//////////////////////////////////////////////////////////////
	/**
	 * format a date.
	 * @param Date 
	 */
	Date formatDateDao(final Date date);
	
	
	//////////////////////////////////////////////////////////////
	// Used by MessagesController
	//////////////////////////////////////////////////////////////
	/**
	 * get the number of persons whose the Message sent.
	 * get the number of persons in black list.
	 * get the number of persons received Message.
	 * @param msgId 
	 */
	TrackInfos getTrackInfos(final Integer msgId) 
				throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException;
	
	//////////////////////////////////////////////////////////////
	// Used by PerformSendSmsController
	//////////////////////////////////////////////////////////////
	/**
	 * create the message.
	 * @param uiRecipients 
	 * @param login 
	 * @param content 
	 * @param smsTemplate 
	 * @param userGroup 
	 * @param serviceId 
	 * @param mail 
	 * @return the message
	 * @throws CreateMessageException 
	 */
	Message composeMessage(List<UiRecipient> uiRecipients, 
			String login, String content, String smsTemplate, String userGroup,
			Integer serviceId, MailToSend mail) throws CreateMessageException;
	
	/**
	 * treat a message.
	 * @param message 
	 * @return the message
	 */
	String treatMessage(Message message);
	
	//////////////////////////////////////////////////////////////
	// Service
	//////////////////////////////////////////////////////////////
	/**
	 * get all services.
	 */
	List<Service> getAllServices();
	
	//////////////////////////////////////////////////////////////
	// Role
	//////////////////////////////////////////////////////////////
	/**
	 * get all roles.
	 * @param idRoles
	 */
	List<UIRole> getAllRoles(List<Integer> idRoles);
	
	/**
	 * get all roles.
	 */
	List<UIRole> getAllRoles();

	/**
	 * save role.
	 * @param role
	 * @param selectedValues
	 */
	void saveRole(UIRole role, List<String> selectedValues);
	
	/**
	 * delete role.
	 * @param role
	 */
	void deleteRole(UIRole role);
	
	/**
	 * update role.
	 * @param role
	 * @param selectedValues
	 */
	void updateRole(UIRole role, List<String> selectedValues);
	
	/**
	 * get id fonction by role.
	 * @param role
	 */
	List<String> getIdFctsByRole(UIRole role);
	
	//////////////////////////////////////////////////////////////
	// Fonction
	//////////////////////////////////////////////////////////////
	/**
	 * get all fonctions.
	 */
	List<Fonction> getAllFonctions();
	
	Set<String> getListPhoneNumbersInBlackList();
	
	String testConnexion();
}
