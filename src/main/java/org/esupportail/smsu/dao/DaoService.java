/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.MailRecipient;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.PendingMember;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.dao.beans.Template;

import org.esupportail.smsu.domain.beans.message.MessageStatus;



/**
 * The DAO service interface.
 */
public interface DaoService extends Serializable {

	//////////////////////////////////////////////////////////////
	// Message Count
	//////////////////////////////////////////////////////////////
	
	/**
	 * @return message count.
	 */
	int getMessagesCount();
	
	//////////////////////////////////////////////////////////////
	// Message
	//////////////////////////////////////////////////////////////
	/**
	 * @param userGroupId 
	 * @param userAccountId 
	 * @param userServiceId 
	 * @param userTemplateId 
	 * @param userUserId 
	 * @param beginDate 
	 * @param endDate 
	 * @return the messages.
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 */
	List<Message> getMessages(Integer userGroupId, Integer userAccountId, Integer userServiceId, 
			Integer userTemplateId, Integer userUserId, java.sql.Date beginDate, java.sql.Date endDate);

	/**
	 * @param message
	 */
	void addMessage(Message message);
	
	/**
	 * @param message
	 */
	void updateMessage(Message message);
	
	/**
	 * @param service 
	 * @return the list of messages
	 */
	List<Message> getMessagesByService(Service service);
	
	/**
	 * @param template
	 * @return the list of messages
	 */
	List<Message> getMessagesByTemplate(Template template);
	
	/**
	 * Return all message by state.
	 * @param state
	 * @return
	 */
	List<Message> getMessagesByState(MessageStatus state);
	
	//////////////////////////////////////////////////////////////
	// Approval Messages
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages to approve.
	 */
	List<Message> getApprovalMessages();
	
	/**
	 * @return the message by id.
	 */
	Message getMessageById(Integer id);
	
	/**
	 * remove message content in db older than the specified date.
	 * @param date
	 */
	void deleteMessageContentOlderThan(Date date);
	
	/**
	 * delete message in db (and supervisor sender and to recipient associated)
	 * older than the specified date.
	 * @param date
	 */
	void deleteMessageOlderThan(Date date);
	
	//////////////////////////////////////////////////////////////
	// Baisc Group
	//////////////////////////////////////////////////////////////
	
	/**
	 * @return the list of all the groups.
	 */
	List<BasicGroup> getGroups();
	
	/**
	 * @param group 
	 * @return the group
	 */
	BasicGroup getGroupByLabel(String group);
	
	/**
	 * @param group
	 */
	void addBasicGroup(BasicGroup group);
	
	/**
	 * Delete all orphan basic groups in table basic group.
	 */
	void deleteOrphanBasicGroup();

	//////////////////////////////////////////////////////////////
	// Account
	//////////////////////////////////////////////////////////////
	/**
	 * @return the accounts.
	 */
	List<Account> getAccounts();
	
	/**
	 * @param id
	 * @return a account
	 */
	Account getAccountById(Integer id);
	
	/**
	 * @param 
	 * @return a account
	 */
	Account getAccountByLabel(String label);
	
	void saveAccount(Account account);

	//////////////////////////////////////////////////////////////
	// Person
	//////////////////////////////////////////////////////////////
	/**
	 * @return the persons.
	 */
	List<Person> getPersons();
	
	/**
	 * @param login
	 * @return the person
	 */
	Person getPersonByLogin(String login);
	
	/**
	 * @param person
	 */
	void addPerson(Person person);
	
	/**
	 * Delete orphan person in table person.
	 */
	void deleteOrphanPerson();

	//////////////////////////////////////////////////////////////
	// Service
	//////////////////////////////////////////////////////////////
	/**
	 * @return the services.
	 */
	List<Service> getServices();
	
	/**
	 * @param service
	 */
	void updateService(Service service);
	
	/**
	 * @param service
	 */
	void addService(Service service);
	
	/**
	 * @param service
	 */
	void deleteService(Service service);
	
	/**
	 * @param key
	 * @return a service
	 */
	Service getServiceByKey(String key);
	
	/**
	 * @param name 
	 * @return a service
	 */
	Service getServiceByName(String name);
	
	/**
	 * @param id
	 * @return the service.
	 */
	Service getServiceById(Integer id);

	//////////////////////////////////////////////////////////////
	// Template
	//////////////////////////////////////////////////////////////
	/**
	 * @return the templates.
	 */
	List<Template> getTemplates();

	/**
	 * @param id
	 * @return the template
	 */
	Template getTemplateById(Integer id);
	
	/**
	 * @param label
	 * @return a template
	 */
	Template getTemplateByLabel(String label);
	
	/**
	 * @param template
	 */
	void deleteTemplate(Template template);
	
	/**
	 * @param template
	 */
	void addTemplate(Template template);
	
	/**
	 * @param template
	 */
	void updateTemplate(Template template);
	
	
	
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
	
	/**
	 * @param recipient
	 */
	void updateRecipient(Recipient recipient);
	
	/**
	 * Delete mail recipient orphan.
	 */
	void deleteOrphanRecipient();
	
	
	//////////////////////////////////////////////////////////////
	// Customized groups
	//////////////////////////////////////////////////////////////
	/**
	 * @param label
	 * @return the customized group
	 */
	CustomizedGroup getCustomizedGroupById(Integer id);
	
	/**
	 * @param label
	 * @param id
	 * @return the customized group
	 */
	CustomizedGroup getCustomizedGroupByLabelWithOtherId(String label, Integer id);
	
	/**
	 * @param label
	 * @return the customized group
	 */
	CustomizedGroup getCustomizedGroupByLabel(String label);
	
	boolean isRoleInUse(Role role);
	
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
	 * update a customized group.
	 * @param customizedGroup 
	 */
	void updateCustomizedGroup(CustomizedGroup customizedGroup);
	
	
	/**
	 * get the list of customized group.
	 */
	List<CustomizedGroup> getAllCustomizedGroups();
	
	/**
	 * delete customizedGroup.
	 * @param customizedGroup
	 */
	void deleteCustomizedGroup(CustomizedGroup customizedGroup);
	
	
	//////////////////////////////////////////////////////////////
	// Pending member
	//////////////////////////////////////////////////////////////
	
	/**
	 * return true if the given user is a pending member.
	 * return false otherwise
	 * @param userIdentifier
	 * @return
	 */
	boolean isPendingMember(final String userIdentifier);

	/**
	 * retrieve the given pending member.
	 * @param login
	 * @return
	 */
	PendingMember getPendingMember(final String login);

	/**
	 * delete the given pending member.
	 * @param login
	 */
	void deletePendingMember(final String login);

	/**
	 * add the given pending member.
	 * @param login
	 * @param code
	 */
	void saveOrUpdatePendingMember(final String login, final String code);
	
	/**
	 * Delete pending member older than the specified date.
	 * @param date
	 * @return
	 */
	int deletePendingMemberOlderThan(Date date);
	
	//////////////////////////////////////////////////////////////
	// Role
	//////////////////////////////////////////////////////////////
	/**
	 * @return the roles.
	 */
	List<Role> getRoles();
	
	/**
	 * @param role.
	 */
	void saveRole(Role role);
	
	/**
	 * @param role.
	 */
	void deleteRole(Role role);
	
	/**
	 * @param role.
	 */
	void updateRole(Role role);
	
	/**
	 * @param id 
	 * @return 
	 */
	Role getRoleById(Integer id);
	
	/**
	 * @param name
	 * @return
	 */
	Role getRoleByName(String name);
	
	/**
	 * @return set of Fonction.
	 */
	Set<Fonction> getFctsByRole(Role role);
	
	//////////////////////////////////////////////////////////////
	// Fonction
	//////////////////////////////////////////////////////////////
	/**
	 * @return the fonctions.
	 */
	List<Fonction> getFonctions();
	Fonction getFonctionById(Integer id);
	Fonction getFonctionByName(String name);
	
	//////////////////////////////////////////////////////////////
	// Mails
	//////////////////////////////////////////////////////////////
	/**
	 * @return the mails.
	 */
	List<Mail> getMailsByTemplate(Template template);
	
	/**
	 * Delete all emails(and To mail recipient associated).
	 */
	void deleteOrphanMail();
	
	
	//////////////////////////////////////////////////////////////
	// Mails recipient
	//////////////////////////////////////////////////////////////
	
	/**
	 * Delete all orphan mail recipient.
	 */
	void deleteOrphanMailRecipient();
	
	/**
	 * @param addresse
	 * @return
	 */
	MailRecipient getMailRecipientByAddress(String addresse);


	
	boolean isSupervisor(final Person person);
}
