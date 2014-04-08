/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
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
import org.esupportail.smsu.dao.beans.Supervisor;
import org.esupportail.smsu.dao.beans.Template;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The Hiberate implementation of the DAO service.
 */
public class HibernateDaoServiceImpl extends HibernateDaoSupport 
									 implements DaoService, InitializingBean {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 3152554337896617315L;


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
	public HibernateDaoServiceImpl() {
		super();
	}

	/**
	 * retrieve the current session.
	 * @return
	 */
	private Session getCurrentSession() {
		return getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	//////////////////////////////////////////////////////////////
	// Messages Count
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesCount()
	 */
	public int getMessagesCount() {
		return getQueryIntResult(
		"select count(*) from Message ");
	}

	//////////////////////////////////////////////////////////////
	// Message
	//////////////////////////////////////////////////////////////
	/**
	 * @return the messages.
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 */
	@SuppressWarnings("unchecked")
	public List<Message> getMessages(final Integer userGroupId, final Integer userAccountId, 
			final Integer userServiceId, final Integer userTemplateId, final Person sender, 
			final java.sql.Date beginDate, final java.sql.Date endDate, int maxResults) {

		Criteria criteria = getCurrentSession().createCriteria(Message.class);
		
		if (userGroupId != null) {
			criteria.createCriteria(Message.PROP_GROUP_SENDER).add(Restrictions.eq(BasicGroup.PROP_ID, userGroupId));
		}
		
		if (userAccountId != null) {
			criteria.createCriteria(Message.PROP_ACCOUNT).add(Restrictions.eq(Account.PROP_ID, userAccountId));
		}
		
		if (userServiceId != null) {		
			if (userServiceId != 0) {
				criteria.createCriteria(Message.PROP_SERVICE).add(Restrictions.eq(Service.PROP_ID, userServiceId));				
			} 			
		} else {
			//criteria.createCriteria(Message.PROP_SERVICE).add(Restrictions.isNull(Service.PROP_ID));				
		}
		
		if (userTemplateId != null) {
			criteria.createCriteria(Message.PROP_TEMPLATE).add(Restrictions.eq(Template.PROP_ID, userTemplateId));				
		}
		
		if (sender != null) {
			criteria.add(Restrictions.eq(Message.PROP_SENDER, sender));
		}				

		if (beginDate != null) {
			criteria.add(Restrictions.ge(Message.PROP_DATE, beginDate));
		}
		
		if (endDate != null) {
			criteria.add(Restrictions.lt(Message.PROP_DATE, endDate));
		}
		
		criteria.addOrder(Order.desc(Message.PROP_DATE));

		if (maxResults > 0) criteria.setMaxResults(maxResults);
		
		List<Message> result = criteria.list();

		return result;
	}

	/**
	 * @param message 
	 * @see org.esupportail.smsu.dao.DaoService#addMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void addMessage(final Message message) {
		addObject(message);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateMessage(org.esupportail.smsu.dao.beans.Message)
	 */
	public void updateMessage(final Message message) {
		updateObject(message);

	}

	/**
	 * @param service 
	 * @return 
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesByServiceId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Message> getMessagesByService(final Service service) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
		criteria.add(Restrictions.eq(Message.PROP_SERVICE, service));
		
		return getHibernateTemplate().findByCriteria(criteria);
		
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesByTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	@SuppressWarnings("unchecked")
	public List<Message> getMessagesByTemplate(final Template template) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
		criteria.add(Restrictions.eq(Message.PROP_TEMPLATE, template));
		
		return getHibernateTemplate().findByCriteria(criteria);
	}

	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#getMessagesByState
	 */
	@SuppressWarnings("unchecked")
	public List<Message> getMessagesByState(final MessageStatus state) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
		criteria.add(Restrictions.eq(Message.PROP_STATE, state.name()));
		
		final List<Message> messageList = getHibernateTemplate().findByCriteria(criteria);
		
		return messageList;
		
	}
	
	public void deleteMessageContentOlderThan(final Date date) {
		final String request =
		    "update Message as mess set mess.Content = '' " + 
		    " where mess.Date < :date and mess.Content <> ''";
		
		final Query query = getCurrentSession().createQuery(request);
		query.setTimestamp("date", date);
		
		final int modifiedMessages = query.executeUpdate();

		logger.debug("Modified items from deleteMessageContentOlderThan " + date + ": " + modifiedMessages);
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteMessageOlderThan(java.util.Date)
	 */
	public void deleteMessageOlderThan(final Date date) {
		final Session currentSession = getCurrentSession();
		
		// delete Supervisor sender
		final String deleteSupervisorSenderRequest =
		    "delete from SupervisorSender " + 
		    " where Msg.id in ( select Id from Message where Date < :date )";
		
		final Query querySupervisorSender = currentSession.createQuery(
				deleteSupervisorSenderRequest);
		
		querySupervisorSender.setTimestamp("date", date);
		
		final int deletedSupervisorSender = querySupervisorSender.executeUpdate();
		
		// delete to recipient
		final String deleteToRecipientRequest =
		    "delete from ToRecipient " + 
		    " where Msg.id in ( select Id from Message where Date < :date )";
		
		final Query queryDeleteToRecipient = currentSession.createQuery(deleteToRecipientRequest);
		queryDeleteToRecipient.setTimestamp("date", date);
		
		final int deletedToRecipient = queryDeleteToRecipient.executeUpdate();

		// delete message
		final String deleteMessageRequest =
		    "delete from Message as mess " + 
		    " where mess.Date < :date";
		
		final Query queryDeleteMessage = currentSession.createQuery(deleteMessageRequest);
		queryDeleteMessage.setTimestamp("date", date);
		
		final int deletedMessage = queryDeleteMessage.executeUpdate();
		
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
	@SuppressWarnings("unchecked")
	public List<Message> getApprovalMessages() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
		criteria.add(Restrictions.eq(Message.PROP_STATE, MessageStatus.WAITING_FOR_APPROVAL.toString()));
		criteria.addOrder(Order.asc(Message.PROP_ID));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMessageById(java.lang.Integer)
	 */
	public Message getMessageById(final Integer id) {
		return (Message) getHibernateTemplate().get(Message.class, id);
	}
	
	//////////////////////////////////////////////////////////////
	// Basic Group
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@SuppressWarnings("unchecked")
	public List<BasicGroup> getGroups() {
		DetachedCriteria criteria = DetachedCriteria.forClass(BasicGroup.class);
		criteria.addOrder(Order.asc(BasicGroup.PROP_ID));
		return  getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getGroupByLabel(java.lang.String)
	 */
	public BasicGroup getGroupByLabel(final String strGroup) {
		BasicGroup group = new BasicGroup();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(BasicGroup.class);
		criteria.add(Restrictions.eq(BasicGroup.PROP_LABEL, strGroup));

		group = (BasicGroup) criteria.uniqueResult();

		return  group;
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanBasicGroup()
	 */
	public void deleteOrphanBasicGroup() {

		final Session currentSession = getCurrentSession();

		final String deleteBasicGroupRequest =
		    "delete from BasicGroup " + " where Id not in " + 
		    "( select GroupSender.Id from Message where GroupSender.Id is not null )" + 
		    "   and Id not in " +
		    "( select GroupRecipient.Id from Message where GroupRecipient.Id is not null )";
		
		final Query queryRecipient = currentSession.createQuery(deleteBasicGroupRequest);

		final int deletedBasicGroup = queryRecipient.executeUpdate();

		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanBasicGroup : \n" + 
				  " - Basic group : " + deletedBasicGroup);
		}

	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addBasicGroup(org.esupportail.smsu.dao.beans.BasicGroup)
	 */
	public void addBasicGroup(final BasicGroup group) {
		addObject(group);
	}

	//////////////////////////////////////////////////////////////
	// Account
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@SuppressWarnings("unchecked")
	public List<Account> getAccounts() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
		criteria.addOrder(Order.asc(Account.PROP_LABEL));
		return  getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getAccountById(java.lang.Integer)
	 */
	public Account getAccountById(final Integer id) {
		return (Account) getHibernateTemplate().get(Account.class, id);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getAccountByLabel(java.lang.String)
	 */
	public Account getAccountByLabel(final String label) {
		Account account = new Account();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Account.class);
		criteria.add(Restrictions.eq(Account.PROP_LABEL, label));

		account = (Account) criteria.uniqueResult();

		return account;
	}

	public void saveAccount(final Account account) {
		addObject(account);
	}
	
	//////////////////////////////////////////////////////////////
	// Person
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@SuppressWarnings("unchecked")
	public List<Person> getPersons() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		criteria.addOrder(Order.asc(Person.PROP_ID));
		return  getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getPersonByLogin(java.lang.String)
	 */
	public Person getPersonByLogin(final String login) {
		Person person = new Person();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Person.class);
		criteria.add(Restrictions.eq(Person.PROP_LOGIN, login));

		person = (Person) criteria.uniqueResult();

		return person;
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addPerson(org.esupportail.smsu.dao.beans.Person)
	 */
	public void addPerson(final Person person) {
		addObject(person);

	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanPerson()
	 */
	public void deleteOrphanPerson() {
		final Session currentSession = getCurrentSession();
		
		final String deletePersonRequest =
		    "delete from Person " + 
		    " where Id not in " +
		    "( select Supervisor.Id from SupervisorSender where Supervisor.Id is not null )" +
		    "   and Id not in " +
		    "( select Sender.Id from Message where Sender.Id is not null )" +
		    "   and Id not in " + 
		    "( select person.Id from Supervisor where person.Id  is not null )";
		
		final Query queryRecipient = currentSession.createQuery(deletePersonRequest);
		
		final int deletedPerson = queryRecipient.executeUpdate();
		
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
	@SuppressWarnings("unchecked")
	public List<Service> getServices() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Service.class);
		criteria.addOrder(Order.asc(Service.PROP_NAME));
		return  getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void updateService(final Service service) {
		updateObject(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#addService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void addService(final Service service) {
		addObject(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#deleteService(org.esupportail.smsu.dao.beans.Service)
	 */
	public void deleteService(final Service service) {
		deleteObject(service);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getServiceByKey(java.lang.String)
	 */
	public Service getServiceByKey(final String key) {
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Service.class);
		criteria.add(Restrictions.eq(Service.PROP_KEY, key));
		Service service = (Service) criteria.uniqueResult();
		return service;
	}


	/**
	 * @see org.esupportail.smsu.dao.DaoService#getServiceByName(java.lang.String)
	 */
	public Service getServiceByName(final String name) {
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Service.class);
		criteria.add(Restrictions.eq(Service.PROP_NAME, name));
		Service service = (Service) criteria.uniqueResult();
		return service;
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getServiceById(java.lang.Integer)
	 */
	public Service getServiceById(final Integer id) {
		return (Service) getHibernateTemplate().get(Service.class, id);
	}

	//////////////////////////////////////////////////////////////
	// Template
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getDepartments()
	 */
	@SuppressWarnings("unchecked")
	public List<Template> getTemplates() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Template.class);
		criteria.addOrder(Order.asc(Template.PROP_LABEL));
		return  getHibernateTemplate().findByCriteria(criteria);
	}


	/**
	 * @see org.esupportail.smsu.dao.DaoService#getTemplateById(java.lang.Integer)
	 */
	public Template getTemplateById(final Integer id) {
		return (Template) getHibernateTemplate().get(Template.class, id);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void addTemplate(final Template template) {
		addObject(template);
		
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#deleteTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void deleteTemplate(final Template template) {
		deleteObject(template);
		
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	public void updateTemplate(final Template template) {
		updateObject(template);
		
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getTemplateByLabel(java.lang.String)
	 */
	public Template getTemplateByLabel(final String label) {
		Template template = new Template();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Template.class);
		criteria.add(Restrictions.eq(Template.PROP_LABEL, label));

		template = (Template) criteria.uniqueResult();
		
		return template;
	}
	
	//////////////////////////////////////////////////////////////
	// Recipient
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getRecipientByPhone(java.lang.String)
	 */
	public Recipient getRecipientByPhone(final String strPhone) {
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Recipient.class);
		criteria.add(Restrictions.eq(Recipient.PROP_PHONE, strPhone));
		Recipient recipient = (Recipient) criteria.uniqueResult();
		return recipient;
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#addRecipient(org.esupportail.smsu.dao.beans.Recipient)
	 */
	public void addRecipient(final Recipient recipient) {
		addObject(recipient);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#updateRecipient(org.esupportail.smsu.dao.beans.Recipient)
	 */
	public void updateRecipient(final Recipient recipient) {
		updateObject(recipient);
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanRecipient()
	 */
	public void deleteOrphanRecipient() {
		final Session currentSession = getCurrentSession();
		
		final String deleteRecipientRequest =
		    "delete from Recipient " + 
		    " where Id not in " 
		    + "( select Rcp.Id from ToRecipient where Rcp.Id is not null)";
		
		final Query queryRecipient = currentSession.createQuery(deleteRecipientRequest);
		
		final int deletedRecipient = queryRecipient.executeUpdate();
		
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
		addObject(customizedGroup);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getCustomizedGroupById(java.lang.Integer)
	 */
	public CustomizedGroup getCustomizedGroupById(final Integer id) {
		CustomizedGroup customizedGroup = new CustomizedGroup();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(CustomizedGroup.class);
		criteria.add(Restrictions.eq(CustomizedGroup.PROP_ID, id));
		customizedGroup = (CustomizedGroup) criteria.uniqueResult();
		return customizedGroup;
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getCustomizedGroupByLabel(java.lang.String)
	 */
	public CustomizedGroup getCustomizedGroupByLabel(final String label) {
		CustomizedGroup customizedGroup = new CustomizedGroup();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(CustomizedGroup.class);
		criteria.add(Restrictions.eq(CustomizedGroup.PROP_LABEL, label));
		customizedGroup = (CustomizedGroup) criteria.uniqueResult();
		return customizedGroup;
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getCustomizedGroupByLabelWithOtherId
	 */
	public CustomizedGroup getCustomizedGroupByLabelWithOtherId(final String label, final Integer id) {
		CustomizedGroup customizedGroup = new CustomizedGroup();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(CustomizedGroup.class);
		criteria.add(Restrictions.eq(CustomizedGroup.PROP_LABEL, label));
		criteria.add(Restrictions.ne(CustomizedGroup.PROP_ID, id));
		customizedGroup = (CustomizedGroup) criteria.uniqueResult();
		return customizedGroup;
	}

	public boolean isRoleInUse(final Role role) {
		CustomizedGroup customizedGroup = new CustomizedGroup();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(CustomizedGroup.class);
		criteria.add(Restrictions.eq(CustomizedGroup.PROP_ROLE, role));
		customizedGroup = (CustomizedGroup) getFirstResult(criteria);
		return customizedGroup != null;
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getFirstCustomizedGroup()
	 */
	public CustomizedGroup getFirstCustomizedGroup() {
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(CustomizedGroup.class);
		criteria.addOrder(Order.asc(CustomizedGroup.PROP_ID));
		CustomizedGroup customizedGroup = (CustomizedGroup) getFirstResult(criteria);
		return customizedGroup;
	}

	private Object getFirstResult(Criteria criteria) {
		criteria.setMaxResults(1);
		return criteria.uniqueResult();
	}

	
	/**
	 * @see org.esupportail.example.dao.DaoService#getAllCustomizedGroups()
	 */
	@SuppressWarnings("unchecked")
	public List<CustomizedGroup> getAllCustomizedGroups() {
		DetachedCriteria criteria = DetachedCriteria.forClass(CustomizedGroup.class);
		criteria.addOrder(Order.asc(CustomizedGroup.PROP_LABEL));
		return  getHibernateTemplate().findByCriteria(criteria);
    }
	
	/**
	 * @see org.esupportail.example.dao.DaoService#saveCustomizedGroup
	 */
	public void saveCustomizedGroup(final CustomizedGroup group) {
		addObject(group);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#deleteCustomizedGroup
	 */
	public void deleteCustomizedGroup(final CustomizedGroup group) {
		deleteObject(group);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#updateCustomizedGroup
	 */
	public void updateCustomizedGroup(final CustomizedGroup group) {
		updateObject(group);
	}
	
	//////////////////////////////////////////////////////////////
	// Pending member
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#isPendingMember(java.lang.String)
	 */
	public boolean isPendingMember(final String userIdentifier) {
		final PendingMember pendingMember = (PendingMember) 
		                getHibernateTemplate().get(PendingMember.class, userIdentifier);		
		boolean result = pendingMember != null;
		return result;
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#getPendingMember(java.lang.String)
	 */
	public PendingMember getPendingMember(final String login) {
		final PendingMember pendingMember = (PendingMember) 
        getHibernateTemplate().get(PendingMember.class, login);		
		return pendingMember;
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#deletePendingMember(java.lang.String)
	 */
	public void deletePendingMember(final String login) {
		final PendingMember pendingMember = (PendingMember) 
        getHibernateTemplate().get(PendingMember.class, login);		
		getHibernateTemplate().delete(pendingMember);
	}

	/**
	 * @see org.esupportail.smsu.dao.DaoService#saveOrUpdatePendingMember(java.lang.String, java.lang.String)
	 */
	public void saveOrUpdatePendingMember(final String login, final String code) {
		// retrieve the pendingMember if exists
		final PendingMember pendingMember = (PendingMember) 
        getHibernateTemplate().get(PendingMember.class, login);
		// update or add the pending member
		if (pendingMember == null) { 
			final PendingMember newPendingMember = new PendingMember(login, code);
			addObject(newPendingMember);
		} else {
			pendingMember.setValidationCode(code);
			updateObject(pendingMember);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deletePendingMemberOlderThan(java.util.Date)
	 */
	public int deletePendingMemberOlderThan(final Date date) {
		final Session currentSession = getCurrentSession();
		
		final String hql = "delete from PendingMember as pm where pm.DateSubscription < :date";
		
		final Query query = currentSession.createQuery(hql);
		query.setTimestamp("date", date);
		
		final int nbPendingMemberDeleted = query.executeUpdate();
		
		return nbPendingMemberDeleted;
	}


	//////////////////////////////////////////////////////////////
	// Role
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getRoles()
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRoles() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
		criteria.addOrder(Order.asc(Role.PROP_NAME));
		return  getHibernateTemplate().findByCriteria(criteria);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#saveRole()
	 */
	public void saveRole(final Role role) {
		addObject(role);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#deleteRole()
	 */
	public void deleteRole(final Role role) {
		deleteObject(role);
	}
	
	/**
	 * @see org.esupportail.example.dao.DaoService#updateRole()
	 */
	public void updateRole(final Role role) {
		updateObject(role);
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getRoleById(java.lang.Integer)
	 */
	public Role getRoleById(final Integer id) {
		return (Role) getHibernateTemplate().get(Role.class, id);
	}
	

	public Role getRoleByName(final String name) {
		Role role = new Role();
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Role.class);
		criteria.add(Restrictions.eq(Role.PROP_NAME, name));

		role = (Role) criteria.uniqueResult();
		
		return role;
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getFctsByRole
	 */
	public Set<Fonction> getFctsByRole(final Role role) {
		Role roleTemp = (Role) getHibernateTemplate().get(Role.class, role.getId());
		Set<Fonction> fonctions = roleTemp.getFonctions();
		return fonctions;
	}
	
	//////////////////////////////////////////////////////////////
	// Fonction
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.example.dao.DaoService#getFonctions()
	 */
	@SuppressWarnings("unchecked")
	public List<Fonction> getFonctions() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Fonction.class);
		criteria.addOrder(Order.asc(Fonction.PROP_ID));
		return  getHibernateTemplate().findByCriteria(criteria);
	}

	//////////////////////////////////////////////////////////////
	// Mails
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMailsByTemplate(org.esupportail.smsu.dao.beans.Template)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> getMailsByTemplate(final Template template) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Mail.class);
		criteria.add(Restrictions.eq(Mail.PROP_TEMPLATE, template));
		
		return getHibernateTemplate().findByCriteria(criteria);
	}

	public Fonction getFonctionById(final Integer id) {
		return (Fonction) getHibernateTemplate().get(Fonction.class, id);
	}

	public Fonction getFonctionByName(final String name) {
		Criteria criteria = getCurrentSession().createCriteria(Fonction.class);
		criteria.add(Restrictions.eq(Fonction.PROP_NAME, name));
		return (Fonction) criteria.uniqueResult();
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsu.dao.DaoService#deleteOrphanMail()
	 */
	public void deleteOrphanMail() {
		final Session currentSession = getCurrentSession();
		
		// delete To mail recipient
		final String deleteToMailRecipientRequest = 
		    "delete from ToMailRecipient " + 
		    " where Mail.Id not in " + 
		    "( select Mail.Id from Message where Mail.Id is not null)";
		
		final Query queryToMailRecipient = currentSession.createQuery(deleteToMailRecipientRequest);
		
		final int deletedToMailRecipient = queryToMailRecipient.executeUpdate();
		
		
		// delete Supervisor sender
		final String deleteMailRequest = 
		    "delete from Mail " + 
		    " where Id not in ( select Mail.Id from Message where Mail.Id is not null)";
		
		final Query querySupervisorSender = currentSession.createQuery(deleteMailRequest);
		
		final int deletedMail = querySupervisorSender.executeUpdate();
		
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
	public void deleteOrphanMailRecipient() {
		final Session currentSession = getCurrentSession();
		
		final String deleteMailRecipientRequest =
		    "delete from MailRecipient " + 
		    " where Id not in " + 
		    "( select MailRecipient.Id from ToMailRecipient where MailRecipient.Id is not null)";
		
		final Query queryMailRecipient = currentSession.createQuery(deleteMailRecipientRequest);
		
		final int deletedMailRecipient = queryMailRecipient.executeUpdate();
		
		if (logger.isTraceEnabled()) {
			logger.trace("Deleted items from deleteOrphanMailRecipient : \n" + 
				     " - Mail Recipient : " + deletedMailRecipient);
		}
	}
	
	/**
	 * @see org.esupportail.smsu.dao.DaoService#getMailRecipientByAddress(java.lang.String)
	 */
	public MailRecipient getMailRecipientByAddress(final String addresse) {
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(MailRecipient.class);
		criteria.add(Restrictions.eq(MailRecipient.PROP_ADDRESS, addresse));
		MailRecipient mailRecipient = (MailRecipient) criteria.uniqueResult();
		return mailRecipient;
	}

	
	public boolean isSupervisor(final Person person) {
		Criteria criteria = getCurrentSession().createCriteria(Supervisor.class);
		criteria.add(Restrictions.eq("person", person));		
		return getFirstResult(criteria) != null;
	}

	public static String join(Iterable<?> elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object s : elements) {
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}

	protected int getQueryIntResult(final String countQuery) {
		return DataAccessUtils.intResult(getHibernateTemplate().find(countQuery));
	}

	protected void addObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("adding " + object + "...");
		}
		getCurrentSession().beginTransaction();
		getHibernateTemplate().save(object);
		getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

	/**
	 * Update an object in the database.
	 * @param object
	 */
	protected void updateObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("merging " + object + "...");
		}
		getCurrentSession().beginTransaction();
		Object merged = getHibernateTemplate().merge(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done, updating " + merged + "...");
		}
		getHibernateTemplate().update(merged);
		getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

	/**
	 * Delete an object from the database.
	 * @param object
	 */
	protected void deleteObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("merging " + object + "...");
		}
		getCurrentSession().beginTransaction();
		Object merged = getHibernateTemplate().merge(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done, deleting " + merged + "...");
		}
		getHibernateTemplate().delete(merged);
                getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

}
