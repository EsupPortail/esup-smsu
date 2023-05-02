/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsu.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.beans.*;
import org.esupportail.smsu.dao.repositories.*;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * The JPA implementation of the DAO service.
 */
@Transactional
@org.springframework.stereotype.Service
public class JpaDaoServiceImpl implements DaoService {
		
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private MailRecipientRepository mailRecipientRepository;
	
	@Autowired
	private MailRepository mailRepository;
	
	@Autowired
	private SupervisorRepository supervisorRepository;
	
	@Autowired
	private FonctionRepository fonctionRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private SupervisorSenderRepository supervisorSenderRepository;
	
	@Autowired
	private ToRecipientRepository toRecipientRepository;
	
	@Autowired
	private BasicGroupRepository basicGroupRepository;

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private TemplateRepository templateRepository;
	
	@Autowired
	private RecipientRepository recipientRepository;
	
	@Autowired
	private CustomizedGroupRepository customizedGroupRepository;
	
	@Autowired
	private PendingMemberRepository pendingMemberRepository;
	
	@Autowired
	private ToMailRecipientRepository toMailRecipientRepository;

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public JpaDaoServiceImpl() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Messages Count
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesCount()
	 */
	public int getMessagesCount() {
		return Math.toIntExact(messageRepository.count());
	}

	//////////////////////////////////////////////////////////////
	// Message
	//////////////////////////////////////////////////////////////
	@Override
	public List<Message> getMessages(final Person sender, int maxResults) {
		Example<Message> example = Example.of( //
				Message.builder() //
						.sender(sender) //
						.build() //
		);
		
		Sort sort = Sort.by(Message.PROP_DATE).descending();

		if(maxResults <= 0) {
			return messageRepository.findAll(example, sort);
		}
		
		return messageRepository.findAll(example, PageRequest.of(0, maxResults, sort)).getContent();
	}

	/**
	 * @param message 
	 * @see org.esupportail.smsu.dao.DaoService#addMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void addMessage(final Message message) {
		messageRepository.save(message);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void updateMessage(final Message message) {
		messageRepository.save(message);

	}

	/**
	 * @param service 
	 * @return 
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesByService(org.esupportail.smsu.dao.beans.Service)
	 */
	@Override
	public List<Message> getMessagesByService(final Service service) {
		return messageRepository.findByService(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#existsMessagesByService(org.esupportail.smsu.dao.beans.Service)
	 */
	@Override
	public boolean existsMessagesByService(Service service) {
		return messageRepository.existsByService(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesByTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	@Override
	public List<Message> getMessagesByTemplate(final Template template) {
		return messageRepository.findByTemplate(template);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#existsMessagesByTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	@Override
	public boolean existsMessagesByTemplate(Template template) {
		return messageRepository.existsByTemplate(template);
	}

	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesByState
	 */
	@Override
	public List<Message> getMessagesByState(final MessageStatus state) {
		return messageRepository.findByState(state);
		
	}
	
	public void deleteMessageContentOlderThan(final Date date) {
		final int modifiedMessages = messageRepository.deleteContentOlderThan(date);
		logger.debug("Modified items from deleteMessageContentOlderThan " + date + ": " + modifiedMessages);
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteMessageOlderThan(java.util.Date)
	 */
	@Override
	public void deleteMessageOlderThan(final Date date) {
		// delete Supervisor sender
		final int deletedSupervisorSender = supervisorSenderRepository.deleteByMsg_DateLessThan(date);
		
		// delete to recipient
		final int deletedToRecipient = toRecipientRepository.deleteByMsg_DateLessThan(date);

		// delete message
		final int deletedMessage = messageRepository.deleteByDateLessThan(date);
		
		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteMessageOlderThan : \n" + 
				     " - Supervisor_Sender : " + deletedSupervisorSender + "\n" +
				     " - To_Recipient : " + deletedToRecipient + "\n" +
				     " - Message : " + deletedMessage);
		}
		
	}

	//////////////////////////////////////////////////////////////
	// Approval Messages
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages to approve.
	 */
	@Override
	public List<Message> getApprovalMessages() {
		return messageRepository.findByStateOrderByIdAsc(MessageStatus.WAITING_FOR_APPROVAL);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMessageById(java.lang.Integer)
	 */
	public Message getMessageById(final Integer id) {
		return messageRepository.findById(id).orElse(null);
	}
	
	//////////////////////////////////////////////////////////////
	// Basic Group
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@Override
	public List<BasicGroup> getGroups() {
		return basicGroupRepository.findAll(Sort.by(BasicGroup.PROP_ID).ascending());
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getGroupByLabel(java.lang.String)
	 */
	public BasicGroup getGroupByLabel(final String strGroup) {
		return  basicGroupRepository.findByLabel(strGroup);
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanBasicGroup()
	 */
	public void deleteOrphanBasicGroup() {
		final int deletedBasicGroup = basicGroupRepository.deleteOrphanBasicGroup();

		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanBasicGroup : \n" + 
				  " - Basic group : " + deletedBasicGroup);
		}

	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addBasicGroup(org.esupportail.smsu.dao.beans.BasicGroup)
	 */
	public void addBasicGroup(final BasicGroup group) {
		basicGroupRepository.save(group);
	}

	//////////////////////////////////////////////////////////////
	// Account
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@Override
	public List<Account> getAccounts() {
		return accountRepository.findAll(Sort.by(Account.PROP_LABEL).ascending());
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getAccountById(java.lang.Integer)
	 */
	public Account getAccountById(final Integer id) {
		return accountRepository.findById(id).orElse(null);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getAccountByLabel(java.lang.String)
	 */
	@Override
	public Account getAccountByLabel(final String label) {
		return accountRepository.findByLabel(label);
	}

	public void saveAccount(final Account account) {
		accountRepository.save(account);
	}
	
	//////////////////////////////////////////////////////////////
	// Person
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@Override
	public List<Person> getPersons() {
		return personRepository.findAll(Sort.by(Person.PROP_ID).ascending());
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getPersonByLogin(java.lang.String)
	 */
	public Person getPersonByLogin(final String login) {
		return personRepository.findByLogin(login);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addPerson(org.esupportail.smsu.dao.beans.Person)
	 */
	public void addPerson(final Person person) {
		personRepository.save(person);

	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanPerson()
	 */
	@Override
	public void deleteOrphanPerson() {
		final int deletedPerson = personRepository.deleteOrphanPerson();
		
		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanPerson : \n" + 
				     " - Person : " + deletedPerson);
		}
	}

	//////////////////////////////////////////////////////////////
	// Service
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@Override
	public List<Service> getServices() {
		return serviceRepository.findAll(Sort.by(Service.PROP_NAME).ascending());
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void updateService(final Service service) {
		addService(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#addService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void addService(final Service service) {
		serviceRepository.save(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#deleteService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void deleteService(final Service service) {
		serviceRepository.delete(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getServiceByKey(java.lang.String)
	 */
	public Service getServiceByKey(final String key) {
		return serviceRepository.findByKey(key);
	}


	/**
	 * @see org.esupportail.smsu.dao.DaoService#getServiceByName(java.lang.String)
	 */
	public Service getServiceByName(final String name) {
		return serviceRepository.findByName(name);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getServiceById(java.lang.Integer)
	 */
	public Service getServiceById(final Integer id) {
		return serviceRepository.findById(id).orElse(null);
	}

	//////////////////////////////////////////////////////////////
	// Template
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@Override
	public List<Template> getTemplates() {
		return templateRepository.findAll(Sort.by(Template.PROP_LABEL).ascending());
	}


	/**
	 * @see org.esupportail.smsu.dao.DaoService#getTemplateById(java.lang.Integer)
	 */
	public Template getTemplateById(final Integer id) {
		return templateRepository.findById(id).orElse(null);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void addTemplate(final Template template) {
		templateRepository.save(template);
		
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#deleteTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void deleteTemplate(final Template template) {
		templateRepository.delete(template);
		
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void updateTemplate(final Template template) {
		addTemplate(template);
		
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getTemplateByLabel(java.lang.String)
	 */
	@Override
	public Template getTemplateByLabel(final String label) {
		return templateRepository.findByLabel(label);
	}
	
	//////////////////////////////////////////////////////////////
	// Recipient
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getRecipient(java.lang.String, java.lang.String)
	 */
	@Override
	public Recipient getRecipient(final String strPhone, String login) {
		return recipientRepository.findByPhoneAndLogin(strPhone, login);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addRecipient(org.esupportail.smsu.dao.beans.Recipient)
	 */
	public void addRecipient(final Recipient recipient) {
		recipientRepository.save(recipient);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateRecipient(org.esupportail.smsu.dao.beans.Recipient)
	 */
	public void updateRecipient(final Recipient recipient) {
		addRecipient(recipient);
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanRecipient()
	 */
	@Override
	public void deleteOrphanRecipient() {
		final int deletedRecipient = recipientRepository.deleteOrphanRecipient();
		
		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanRecipient : \n" + 
				     " - Recipient : " + deletedRecipient);
		}
	}


	//////////////////////////////////////////////////////////////
	// Customized group
	//////////////////////////////////////////////////////////////	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#addCustomizedGroup(org.esupportail.smsu.dao.beans.CustomizedGroup)
	 */
	public void addCustomizedGroup(final CustomizedGroup customizedGroup) {
		saveCustomizedGroup(customizedGroup);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getCustomizedGroupById(java.lang.Integer)
	 */
	@Override
	public CustomizedGroup getCustomizedGroupById(final Integer id) {
		return customizedGroupRepository.findById(id).orElse(null);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getCustomizedGroupByLabel(java.lang.String)
	 */
	@Override
	public CustomizedGroup getCustomizedGroupByLabel(final String label) {
		return customizedGroupRepository.findByLabel(label);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getCustomizedGroupByLabelWithOtherId
	 */
	@Override
	public CustomizedGroup getCustomizedGroupByLabelWithOtherId(final String label, final Integer id) {
		return customizedGroupRepository.findByLabelAndIdNot(label, id);
	}

	@Override
	public boolean isRoleInUse(final Role role) {
		return customizedGroupRepository.existsByRole(role);
	}
	/**
	 * @see org.esupportail.example.dao.DaoService#getAllCustomizedGroups()
	 */
	@Override
	public List<CustomizedGroup> getAllCustomizedGroups() {
		return customizedGroupRepository.findAll(Sort.by(CustomizedGroup.PROP_LABEL).ascending());
    }
	
	/**
	 * @see org.esupportail.example.dao.DaoService#saveCustomizedGroup
	 */
	public void saveCustomizedGroup(final CustomizedGroup group) {
		customizedGroupRepository.save(group);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#deleteCustomizedGroup
	 */
	public void deleteCustomizedGroup(final CustomizedGroup group) {
		customizedGroupRepository.delete(group);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#updateCustomizedGroup
	 */
	public void updateCustomizedGroup(final CustomizedGroup group) {
		saveCustomizedGroup(group);
	}
	
	//////////////////////////////////////////////////////////////
	// Pending member
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#isPendingMember(java.lang.String)
	 */
	public boolean isPendingMember(final String userIdentifier) {
		return pendingMemberRepository.existsById(userIdentifier);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getPendingMember(java.lang.String)
	 */
	public PendingMember getPendingMember(final String login) {
		return pendingMemberRepository.findById(login).orElse(null);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#deletePendingMember(java.lang.String)
	 */
	public void deletePendingMember(final String login) {
		pendingMemberRepository.deleteById(login);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#saveOrUpdatePendingMember(java.lang.String, java.lang.String)
	 */
	public void saveOrUpdatePendingMember(final String login, final String code) {
		// retrieve the pendingMember if exists
		PendingMember pendingMember = getPendingMember(login);
		// update or add the pending member
		if (pendingMember == null) { 
			pendingMember = new PendingMember(login, code);
		} else {
			pendingMember.setValidationCode(code);
		}
		pendingMemberRepository.save(pendingMember);
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deletePendingMemberOlderThan(java.util.Date)
	 */
	public int deletePendingMemberOlderThan(final Date date) {
		return pendingMemberRepository.deleteByDateSubscriptionLessThan(date);
	}


	//////////////////////////////////////////////////////////////
	// Role
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getRoles()
	 */
	public List<Role> getRoles() {
		return roleRepository.findAll(Sort.by(Role.PROP_NAME).ascending());
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#saveRole()
	 */
	public void saveRole(final Role role) {
		roleRepository.save(null);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#deleteRole()
	 */
	public void deleteRole(final Role role) {
		roleRepository.delete(role);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#updateRole()
	 */
	public void updateRole(final Role role) {
		saveRole(role);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getRoleById(java.lang.Integer)
	 */
	@Override
	public Role getRoleById(final Integer id) {
		return roleRepository.findById(id).orElse(null);
	}
	

	@Override
	public Role getRoleByName(final String name) {
		return roleRepository.findByName(name);
	}
	
	//////////////////////////////////////////////////////////////
	// Fonction
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getFonctions()
	 */
	public List<Fonction> getFonctions() {
		return fonctionRepository.findAll(Sort.by(Fonction.PROP_ID).ascending());
	}
	
	/**
	 * @param fonction
	 */
	public void addFonction(Fonction fonction) {
		fonctionRepository.save(fonction);
	}
	
	/**
	 * @param fonction
	 */
	public void deleteFonction(Fonction fonction) {
		fonctionRepository.delete(fonction);
	}
	

	//////////////////////////////////////////////////////////////
	// Mails
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMailsByTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	@Override
	public List<Mail> getMailsByTemplate(final Template template) {
		return mailRepository.findByTemplate(template);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#existsMessagesByService(org.esupportail.smsu.dao.beans.Service)
	 */
	@Override
	public boolean existsMailsByTemplate(Template template) {
		return mailRepository.existsByTemplate(template);
	}

	public Fonction getFonctionById(final Integer id) {
		return fonctionRepository.findById(id).orElse(null);
	}

	public Fonction getFonctionByName(final String name) {
		return fonctionRepository.findByName(name);
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanMail()
	 */
	public void deleteOrphanMail() {
		final int deletedToMailRecipient = toMailRecipientRepository.deleteOrphanToMailRecipient();
		final int deletedMail = mailRepository.deleteOrphanMail();
		
		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanMail : \n" + 
				     " - To mail recipient : " + deletedToMailRecipient + "\n" + 
				     " - Mail : " + deletedMail);
		}
	}
	
	
	
	//////////////////////////////////////////////////////////////
	// Mails recipient
	//////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanMailRecipient()
	 */
	@Override
	public void deleteOrphanMailRecipient() {
		final int deletedMailRecipient = mailRecipientRepository.deleteOrphanMailRecipient();
		
		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanMailRecipient : \n" + 
				     " - Mail Recipient : " + deletedMailRecipient);
		}
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMailRecipientByAddress(java.lang.String)
	 */
	@Override
	public MailRecipient getMailRecipientByAddress(final String addresse) {
		return mailRecipientRepository.findByAddress(addresse);
	}
	
	@Override
	public boolean isSupervisor(final Person person) {
		return supervisorRepository.existsByPerson(person);
	}
}
