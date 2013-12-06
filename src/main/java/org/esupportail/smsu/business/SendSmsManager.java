package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.text.DateFormat;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.urlGeneration.UrlGenerator;
import org.esupportail.portal.ws.client.PortalGroup;
import org.esupportail.portal.ws.client.PortalGroupHierarchy;
import org.esupportail.smsu.business.beans.CustomizedMessage;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.MailRecipient;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.dao.beans.Template;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.mail.MailStatus;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.BackOfficeUnrichableException;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.InsufficientQuotaException;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.CreateMessageException.EmptyGroup;
import org.esupportail.smsu.exceptions.CreateMessageException.PAGSGroupStoreConfigNotSynchronizedException;
import org.esupportail.smsu.groups.pags.SmsuPersonAttributesGroupStore;
import org.esupportail.smsu.groups.pags.SmsuPersonAttributesGroupStore.GroupDefinition;
import org.esupportail.smsu.services.client.SendSmsClient;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.scheduler.SchedulerUtils;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.GroupRecipient;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UiRecipient;



/**
 * @author xphp8691
 *
 */
public class SendSmsManager  {


	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link I18nService}.
	 */
	private I18nService i18nService;

	/**
	 * link to access to the sms client layer.
	 */
	private SendSmsClient sendSmsClient;
	
	/**
	 * wether sendSmsClient is using basic auth or certificate auth
	 */
	private boolean usingBasicAuth;

	/**
	 * the default Supervisor login when the max SMS number is reach.
	 */
	private String defaultSupervisorLogin;

	/**
	 * {@link SmtpServiceUtils}.
	 */
	private SmtpServiceUtils smtpServiceUtils;

	/**
	 *  {@link LdapUtils}.
	 */
	private LdapUtils ldapUtils;

	/**
	 * Used to launch task.
	 */
	private SchedulerUtils schedulerUtils;

	/**
	 * The URL generator.
	 */
	private UrlGenerator urlGenerator;

	/**
	 * The SMS max size.
	 */
	private Integer smsMaxSize;

	/**
	 * The default account.
	 */
	private String defaultAccount;

	/**
	 * used to customize the content.
	 */
	private ContentCustomizationManager customizer;

	/**
	 * the phone number validation pattern.
	 */
	private String phoneNumberPattern;

	/**
	 * the LDAP Email attribute.
	 */
	private String userEmailAttribute;

	private SmsuPersonAttributesGroupStore smsuPersonAttributesGroupStore;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public SendSmsManager() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Pricipal methods
	//////////////////////////////////////////////////////////////
	/**
	 * @param uiRecipients 
	 * @param login 
	 * @param content 
	 * @param smsTemplate 
	 * @param userGroup 
	 * @param serviceId 
	 * @param mailToSend 
	 * @return a message.
	 * @throws CreateMessageException 
	 */
	public Message createMessage(final List<UiRecipient> uiRecipients,
			final String login, final String content, final String smsTemplate,
			final String userGroup, final Integer serviceId,
			final MailToSend mailToSend) throws CreateMessageException {
		Service service = getService(serviceId);
		Set<Recipient> recipients = getRecipients(uiRecipients, service);
		BasicGroup groupRecipient = getGroupRecipient(uiRecipients);
		BasicGroup groupSender = getGroupSender(userGroup);
		MessageStatus messageStatus = getWorkflowState(recipients.size(), groupSender, groupRecipient);
		Person sender = getSender(login);

		// test if customizeExpContent raises a CreateMessageException
		customizer.customizeExpContent(content, groupSender.getLabel(), sender.getLogin());
				
		Message message = new Message();
		message.setContent(content);
		if (smsTemplate != null) message.setTemplate(getMessageTemplate(smsTemplate));
		message.setSender(sender);
		message.setAccount(getAccount(userGroup));
		message.setService(service);
		message.setGroupSender(groupSender);
		message.setRecipients(recipients);
		message.setGroupRecipient(groupRecipient);			
		message.setStateAsEnum(messageStatus);				
		message.setSupervisors(mayGetSupervisorsOrNull(message));				
		if (mailToSend != null) message.setMail(getMail(message, mailToSend));
		return message;
	}

	private Set<Person> mayGetSupervisorsOrNull(Message message) {
		if (MessageStatus.WAITING_FOR_APPROVAL.equals(message.getStateAsEnum())) {
			logger.debug("Supervisors needed");
			return getSupervisors(getSupervisorCustomizedGroup(message));
		} else {
			logger.debug("No supervisors needed");
			return null;
		}
	}

	/**
	 * @param message
	 * @return null or an error message (key into i18n properties)
	 * @throws CreateMessageException.WebService
	 */
	public void treatMessage(final Message message) throws CreateMessageException.WebService {
		try {
			if (message.getStateAsEnum().equals(MessageStatus.NO_RECIPIENT_FOUND))
				;
			else if (message.getStateAsEnum().equals(MessageStatus.WAITING_FOR_APPROVAL))
				// envoi du mail
				sendApprovalMailToSupervisors(message);
			else 
				maySendMessageInBackground(message);
		} catch (UnknownIdentifierApplicationException e) {
			message.setStateAsEnum(MessageStatus.WS_ERROR);
			daoService.updateMessage(message);
			logger.error("Application unknown", e);
			throw new CreateMessageException.WebServiceUnknownApplication(e);
		} catch (InsufficientQuotaException e) {
			message.setStateAsEnum(MessageStatus.WS_QUOTA_ERROR);
			daoService.updateMessage(message);
			logger.error("Quota error", e);
			throw new CreateMessageException.WebServiceInsufficientQuota(e);
		} catch (BackOfficeUnrichableException e) {
			message.setStateAsEnum(MessageStatus.WS_ERROR);
			daoService.updateMessage(message);
			throw new CreateMessageException.BackOfficeUnreachable(e);
		}
	}

	/**
	 * Used to send message in state waiting_for_sending.
	 */
	public void sendWaitingForSendingMessage() {
		// get all message ready to be sent
		final List<Message> messageList = daoService.getMessagesByState(MessageStatus.WAITING_FOR_SENDING);

		if (logger.isDebugEnabled()) {
			logger.debug("Found " + messageList.size() + " message(s) to send to the back office");
		}

		for (Message message : messageList) {
			if (logger.isDebugEnabled()) {
				logger.debug("Start managment of message with id : " + message.getId());
			}
			// get the associated customized group
			final String groupLabel = message.getGroupSender().getLabel();
			final CustomizedGroup cGroup = getRecurciveCustomizedGroupByLabel(groupLabel);

			// send the customized messages
			for (CustomizedMessage customizedMessage : getCustomizedMessages(message)) {
				sendCustomizedMessages(customizedMessage);
				cGroup.setConsumedSms(cGroup.getConsumedSms() + 1);
				daoService.updateCustomizedGroup(cGroup);
			}

			// update the message status in DB
			message.setStateAsEnum(MessageStatus.SENT);

			// force commit to database. do not allow rollback otherwise the message will be sent again!
			daoService.updateMessage(message);

			//Deal with the emails
			if (message.getMail() != null) {
				sendMails(message);
			}

			daoService.updateMessage(message);

			if (logger.isDebugEnabled()) {
				logger.debug("End of managment of message with id : " + message.getId());
			}

		}
	}

	/**
	 * send mail based on supervisors.
	 * @return
	 */
	private void sendApprovalMailToSupervisors(final Message message) {
		sendMailToSupervisors(message, MessageStatus.WAITING_FOR_APPROVAL, null);
	}

	public void sendMailMessageApprovedOrCanceled(Message message, MessageStatus status, User currentUser) {
		sendMailToSupervisors(message, status, currentUser);
		sendMailToSenderMessageApprovedOrCanceled(message, status, currentUser);
	}

	private void sendMailToSupervisors(final Message message, MessageStatus status, User currentUser) {
		CustomizedGroup cGroup = getSupervisorCustomizedGroup(message);
		List<String> toList = getSupervisorsMails(getSupervisors(cGroup));
		if (toList == null) {
			logger.error("no supervisors??");
			return;
		}

		String subjectKey;
		String textBodyKey;
		String textBodyParam3;
		if (status == MessageStatus.CANCEL) {
			subjectKey = "MSG.SUBJECT.MAIL.TO.CANCELED";
			textBodyKey = "MSG.TEXTBOX.MAIL.TO.CANCELED";
			textBodyParam3 = currentUser.getDisplayName();
		} else if (status == MessageStatus.IN_PROGRESS) {
			subjectKey = "MSG.SUBJECT.MAIL.TO.APPROVED";
			textBodyKey = "MSG.TEXTBOX.MAIL.TO.APPROVED";
			textBodyParam3 = currentUser.getDisplayName();
		} else {
			subjectKey = "MSG.SUBJECT.MAIL.TO.APPROVAL";
			textBodyKey = "MSG.TEXTBOX.MAIL.TO.APPROVAL";
			textBodyParam3 = urlGenerator.casUrl(Collections.singletonMap("approvalSMS", ""));
		}
		String senderName = ldapUtils.getUserDisplayName(message.getSender());
		String cGroupName = ldapUtils.getGroupDisplayName(cGroup);
		String subject = getI18nString(subjectKey, senderName);
		String textBody = getI18nString(textBodyKey, cGroupName,
						i18nMsgDate(message), i18nMsgTime(message), textBodyParam3);
		smtpServiceUtils.sendHTMLMessage(toList, null, subject, textBody);
	}

	private void sendMailToSenderMessageApprovedOrCanceled(final Message message, MessageStatus status, User currentUser) {
		List<String> toList = ldapUtils.getUserEmailsAdressByUids(Collections.singleton(message.getSender().getLogin()));

		String subjectKey;
		String textBodyKey;
		if (status == MessageStatus.CANCEL) {
			subjectKey = "MSG.SUBJECT.MAIL.TO.SENDER.CANCELED";
			textBodyKey = "MSG.TEXTBOX.MAIL.TO.SENDER.CANCELED";
		} else {
			subjectKey = "MSG.SUBJECT.MAIL.TO.SENDER.APPROVED";
			textBodyKey = "MSG.TEXTBOX.MAIL.TO.SENDER.APPROVED";
		}
		String subject = getI18nString(subjectKey, i18nMsgDate(message), i18nMsgTime(message));
		String textBody = getI18nString(textBodyKey, 
						i18nMsgDate(message), i18nMsgTime(message), currentUser.getDisplayName());
		smtpServiceUtils.sendHTMLMessage(toList, null, subject, textBody);
	}

	private String i18nMsgDate(Message msg) {
		return DateFormat.getDateInstance(DateFormat.MEDIUM, i18nService.getDefaultLocale()).format(msg.getDate());
	}
	private String i18nMsgTime(Message msg) {
		return DateFormat.getTimeInstance(DateFormat.MEDIUM, i18nService.getDefaultLocale()).format(msg.getDate());
	}

	private Set<Person> getSupervisors(CustomizedGroup cGroup) {
		return new HashSet<Person>(cGroup.getSupervisors()); // nb: we need to copy the set to avoid "Found shared references to a collection" Hibernate exception
	}

	private List<String> getSupervisorsMails(final Set<Person> supervisors) {
		if (supervisors == null) return null;
		logger.debug("supervisors not null");

		final List<String> uids = new LinkedList<String>();
		for (Person supervisor : supervisors) {
			uids.add(supervisor.getLogin());
		}
		logger.info("message waiting for supervision. supervisors uids: " + uids);

		return ldapUtils.getUserEmailsAdressByUids(uids);
	}


	private void maySendMessageInBackground(final Message message) throws BackOfficeUnrichableException, UnknownIdentifierApplicationException, InsufficientQuotaException {
		checkBackOfficeQuotas(message);

		// message is ready to be sent to the back office
		if (logger.isDebugEnabled()) {
			logger.debug("Setting to state WAINTING_FOR_SENDING message with ID : " + message.getId());
		}
		message.setStateAsEnum(MessageStatus.WAITING_FOR_SENDING);
		daoService.updateMessage(message);

		// launch ASAP the task witch manage the sms sending
		schedulerUtils.launchSuperviseSmsSending();
	}


	/**
	 * @param message
	 * @param mailToSend
	 * @return the mail to apply to a message
	 */
	private Mail getMail(final Message message, final MailToSend mailToSend) {

		String subject = mailToSend.getMailSubject();
		logger.debug("create the mail to store SUBJECT : " + subject);

		String content = mailToSend.getMailContent();
		logger.debug("create the mail to store CONTENT : " + content);

		Template template = null;
		String idTemplate = mailToSend.getMailTemplate();
		if (idTemplate != null) {
			logger.debug("create the mail to store TEMPLATE : " + idTemplate);
			template = daoService.getTemplateById(Integer.parseInt(idTemplate));
		}

		Set<MailRecipient> mailRecipients = getMailRecipients(message, mailToSend);

		if (mailRecipients.size() == 0) {
			return null;
		} else {
			Mail mail = new Mail();
			mail.setSubject(subject);
			mail.setContent(content);
			mail.setTemplate(template);
			mail.setStateAsEnum(MailStatus.WAITING);		
			mail.setMailRecipients(mailRecipients);
			return mail;
		}
	}

	private Set<MailRecipient> getMailRecipients(final Message message, final MailToSend mailToSend) {
		final Set<MailRecipient> mailRecipients = new HashSet<MailRecipient>();

		if (mailToSend.getIsMailToRecipients()) {
			final List<String> uids = new LinkedList<String>();
			for (Recipient recipient : message.getRecipients()) {
				uids.add(recipient.getLogin());
			}
			// get all the ldap information in one request 
			List <LdapUser> ldapUsers = ldapUtils.getUsersByUids(uids);

			for (LdapUser ldapUser : ldapUsers) {

				String login = ldapUser.getId();
				String addresse = ldapUser.getAttribute(userEmailAttribute);
				MailRecipient mailRecipient = daoService.getMailRecipientByAddress(addresse);
				if (mailRecipient == null) {
					mailRecipient = new MailRecipient(null, addresse, login);
				} else {
					// cas tordu d'un destinataire sans login
					if (mailRecipient.getLogin() == null) {
						mailRecipient.setLogin(login);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Add mail recipient from sms recipients: " + login + " [" + addresse + "]");
				}
				mailRecipients.add(mailRecipient);
			}
		}

		for (String otherAdresse : mailToSend.getMailOtherRecipientsList()) {
				MailRecipient mailRecipient = daoService.getMailRecipientByAddress(otherAdresse);
				if (mailRecipient == null) {
					mailRecipient = new MailRecipient(null, otherAdresse, null);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Add mail recipient from other recipients: [" + otherAdresse + "]");
				}
				mailRecipients.add(mailRecipient);
		}
		return mailRecipients;
	}

	/**
	 * @param message
	 */
	public void sendMails(final Message message) {

		final Mail mail = message.getMail();

		if (logger.isDebugEnabled()) {
			logger.debug("sendMails");
		}
		if (mail == null) return;

			if (logger.isDebugEnabled()) {
				logger.debug("sendMails mail not null");
			}
			//retrieve all informations from message (EXP_NOM, ...)
			final Set<MailRecipient> recipients = mail.getMailRecipients();
			final String expGroupName = message.getGroupSender().getLabel();
			final String expUid = message.getSender().getLogin();
			final String mailSubject = mail.getSubject();
			//the original content
			final String originalContent = mail.getContent();
			try {
				final String contentWithoutExpTags = customizer.customizeExpContent(
						originalContent, expGroupName, expUid );
				if (logger.isDebugEnabled()) {
					logger.debug("sendMails contentWithoutExpTags: " 
							+ contentWithoutExpTags);
				}
				for (MailRecipient recipient : recipients) {
					//the recipient uid
					final String destUid = recipient.getLogin();
					//the message is customized with user informations
					String customizedContentMail = customizer.customizeDestContent(
							contentWithoutExpTags, destUid);
					if (logger.isDebugEnabled()) {
						logger.debug("sendMails customizedContentMail: " 
								+ customizedContentMail);
					}

					String mailAddress = recipient.getAddress();
					if (mailAddress != null) {
						logger.debug("Mail sent to : " + mailAddress);
						smtpServiceUtils.sendOneMessage(mailAddress, mailSubject, customizedContentMail);
					}
				}
				message.getMail().setStateAsEnum(MailStatus.SENT);
			} catch (CreateMessageException e) {
				logger.error("discarding message with " + e + " (this should not happen, the message should have been checked first!)");
				message.getMail().setStateAsEnum(MailStatus.ERROR);
			} 
	}

	private CustomizedGroup getSupervisorCustomizedGroup(final Message message) {
		CustomizedGroup r;

		r = getSupervisorCustomizedGroupByGroup(message.getGroupRecipient(), "destination");
		if (r != null) return r;

		r = getSupervisorCustomizedGroupByGroup(message.getGroupSender(), "sender");
		if (r != null) return r;

		logger.debug("Supervisor needed without a group. Using the default supervisor : [" + defaultSupervisorLogin + "]");
		r = new CustomizedGroup();
		r.setLabel("defaultSupervisor");
		r.setSupervisors(Collections.singleton(defaultSupervisor()));
		return r;
	}

	private Person defaultSupervisor() {
		Person admin = daoService.getPersonByLogin(defaultSupervisorLogin);
		if (admin == null) {
			admin = new Person(null, defaultSupervisorLogin);
		}
		return admin;
	}

	private CustomizedGroup getSupervisorCustomizedGroupByGroup(final BasicGroup group, String groupKind) {
		if (group == null) return null;

		CustomizedGroup cGroup = getSupervisorCustomizedGroupByLabel(group.getLabel());

		if (logger.isDebugEnabled()) {
			if (cGroup != null)
				logger.info("Supervisor found from " + groupKind + " group : [" + cGroup.getLabel() + "]");
			else
				logger.debug("No supervisor found from " + groupKind + " group.");
		}
		
		return cGroup;
	}

	/**
	 * @param groupLabel
	 * @return the current customized group if it has supervisors or the first parent with supervisors.
	 */
	private CustomizedGroup getSupervisorCustomizedGroupByLabel(final String groupLabel) {
		if (logger.isDebugEnabled()) {
			logger.debug("getSupervisorCustomizedGroupByLabel for group [" + groupLabel + "]");
		}
		CustomizedGroup cGroup = getRecurciveCustomizedGroupByLabel(groupLabel);	
		if (cGroup == null) 
			return null;
		else if (!cGroup.getSupervisors().isEmpty())
			return cGroup;
		else {
			if (logger.isDebugEnabled())
				logger.debug("Customized group without supervisor found : [" + cGroup.getLabel() + "]");
			
			String parentGroup = getSafeParentGroupIdByGroupId(cGroup.getLabel());
			if (parentGroup == null) return null;
			return getSupervisorCustomizedGroupByLabel(parentGroup);
		}
	}

	/**
	 * @param uiRecipients
	 * @return the recipients list.
	 * @throws EmptyGroup 
	 */
	private Set<Recipient> getRecipients(final List<UiRecipient> uiRecipients, final Service service) throws EmptyGroup, PAGSGroupStoreConfigNotSynchronizedException {

		Set<Recipient> recipients = new HashSet<Recipient>();

		// determines all the recipients.
		for (UiRecipient uiRecipient : uiRecipients) {

			// single users and phone numbers can be directly added to the message.
			if (!uiRecipient.getClass().equals(GroupRecipient.class)) {
				mayAdd(recipients, uiRecipient.getPhone(), uiRecipient.getLogin());
			} else {
				String serviceKey = service != null ? service.getKey() : null;

				// Group users are search from the portal.
				String groupName = uiRecipient.getDisplayName();
				List<LdapUser> groupUsers = getUsersByGroup(groupName,serviceKey);
				// users are filtered to keep only service compliant ones.
				List<LdapUser> filteredUsers = filterUsers(groupUsers, serviceKey);
				if (filteredUsers.isEmpty())
					throw new CreateMessageException.EmptyGroup(groupName);
					
				//users are added to the recipient list.
				for (LdapUser ldapUser : filteredUsers) {
					String phone = ldapUtils.getUserPagerByUser(ldapUser);
					String login = ldapUser.getId();
					mayAdd(recipients, phone, login);
				}

			}
		}

		return recipients;
	}

	private void mayAdd(Set<Recipient> recipients, String phone, String login) {
		if (StringUtils.isEmpty(this.phoneNumberPattern) || 
		    phone.matches(this.phoneNumberPattern)) {
			recipients.add(getOrCreateRecipient(phone, login));
		} else {
			logger.debug("skipping weird phone number " + phone);
		}
	}

	private Recipient getOrCreateRecipient(String phone, String login) {
		// check if the recipient is already in the database. 
		Recipient recipient = daoService.getRecipientByPhone(phone);

		if (recipient == null) {	
			recipient = new Recipient(null, phone, login);
			daoService.addRecipient(recipient);
		} else {			
			// the phone number may already exist, but the associated login may be NULL (or maybe old?)
			// we must ensure current login is associated to the phone number
			if (login != null) {
				recipient.setLogin(login);
				daoService.updateRecipient(recipient);
			}
		}
		return recipient;
	}

	/**
	 * @param groupName
	 * @param serviceKey 
	 * @return the user list.
	 */
	public List<LdapUser> getUsersByGroup(final String groupName, String serviceKey) throws PAGSGroupStoreConfigNotSynchronizedException {
		if (logger.isDebugEnabled()) {
			logger.debug("Search users for group [" + groupName + "]");
		}
		//get the recipient group hierarchy
		PortalGroupHierarchy groupHierarchy = ldapUtils.getPortalGroupHierarchyByGroupName(groupName);
		//get all users from the group hierarchy
		List<LdapUser> members = getMembers(groupHierarchy, serviceKey);

		logger.info("Found " + members.size() + " ldap users in group " + groupName + " (including duplicates)");

		return members;
	}


	/**
	 * @param groupHierarchy
	 * @param serviceKey 
	 * @return the list of the unique sub-members of a group (recursive)
	 */
	private List<LdapUser> getMembers(final PortalGroupHierarchy groupHierarchy, String serviceKey) throws PAGSGroupStoreConfigNotSynchronizedException {
		final PortalGroup currentGroup = groupHierarchy.getGroup();
		if (logger.isDebugEnabled()) {
			logger.debug("Search users for subgroup [" + currentGroup.getName()
				     + "] [" + currentGroup.getId() + "]");
		}

		List<LdapUser> members = getMembersNonRecursive(currentGroup, serviceKey);
		if (!members.isEmpty()) return members;

		// hum, the group is empty, it may mean that the group is using RegexTester.
		// so recurse on sub-groups, hoping that we do not miss members doing so /o\

		List<PortalGroupHierarchy> childs = groupHierarchy.getSubHierarchies();
		if (childs != null) {
			for (PortalGroupHierarchy child : childs)
				members = mergeUserLists(members, getMembers(child, serviceKey));
		}

		return members;
	}

	private List<LdapUser> getMembersNonRecursive(final PortalGroup currentGroup, String serviceKey) throws PAGSGroupStoreConfigNotSynchronizedException {
		List<LdapUser> members = new LinkedList<LdapUser>();
			String idFromPortal = currentGroup.getId();
			String groupStoreId = StringUtils.split(idFromPortal,".")[1];
			GroupDefinition gd = smsuPersonAttributesGroupStore.getGroupDefinition(groupStoreId);
			if (gd != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("search members");
				}
				members = ldapUtils.getMembers(gd, serviceKey);
			} else {
				logger.error("could not find group " + groupStoreId + " in PAGSGroupStoreConfig.xml in smsu");
				logger.error("smsu needs a PAGSGroupStoreConfig.xml synchronized with the version used by esup-portail");
				throw new CreateMessageException.PAGSGroupStoreConfigNotSynchronizedException();
			}
		return members;
	}


	/**
	 * @param source
	 * @param toMerge
	 * @return the merged list
	 */
	private List<LdapUser> mergeUserLists(final List<LdapUser> source, final List<LdapUser> toMerge) {
		final List<LdapUser> finalList = source;
		for (LdapUser sToMerge : toMerge) {
			if (!finalList.contains(sToMerge)) {
				finalList.add(sToMerge);
				if (logger.isDebugEnabled()) {
					logger.debug("Element [" + sToMerge + "] merged to the source list");
				}
			}
		}
		return finalList;
	}

	/**
	 * @param service
	 * @return the filtered list of users
	 */
	private List<LdapUser> filterUsers(final List<LdapUser> users, final String service) {
		if (logger.isDebugEnabled()) logger.debug("Filtering users for service [" + service + "]");
		List<LdapUser> filteredUsers = new ArrayList<LdapUser>();

		for (LdapUser user : users) {
			if (ldapUtils.isGeneralAndSpecificConditionValidate(user, service)) filteredUsers.add(user);
		}
		if (logger.isDebugEnabled()) logger.debug("Number of filtered users : " + filteredUsers.size());
		return filteredUsers;
	}

	/**
	 * get the message template.
	 */
	private Template getMessageTemplate(final String strTemplate) {
		Integer iTemplate = Integer.parseInt(strTemplate);
		return daoService.getTemplateById(iTemplate);		 
	}

	/**
	 * get the message sender.
	 */
	private Person getSender(final String strLogin) {
		Person sender = daoService.getPersonByLogin(strLogin);
		if (sender == null) {
			sender = new Person();
			sender.setLogin(strLogin);
		}
		return sender;
	}

	/**
	 * @return the message workflow state.
	 * @throws CreateMessageException 
	 */
	private MessageStatus getWorkflowState(final Integer nbRecipients, BasicGroup groupSender, BasicGroup groupRecipient) throws CreateMessageException {
		if (logger.isDebugEnabled()) logger.debug("get workflow state");
		if (logger.isDebugEnabled()) logger.debug("nbRecipients: " + nbRecipients);

		final CustomizedGroup cGroup = getRecurciveCustomizedGroupByLabel(groupSender.getLabel());

		if (nbRecipients.equals(0)) {
			return MessageStatus.NO_RECIPIENT_FOUND;
		}
		checkFrontOfficeQuota(nbRecipients, cGroup, groupSender);
		if (groupRecipient != null || !checkMaxSmsGroupQuota(nbRecipients, cGroup, groupSender)) {
			return MessageStatus.WAITING_FOR_APPROVAL;
		} else {
			return MessageStatus.IN_PROGRESS;
		}
	}

	/**
	 * @param uiRecipients
	 * @return the recipient group, or null.
	 */
	private BasicGroup getGroupRecipient(final List<UiRecipient> uiRecipients) {
		if (logger.isDebugEnabled()) logger.debug("get recipient group");

		for (UiRecipient uiRecipient : uiRecipients) {
			if (uiRecipient.getClass().equals(GroupRecipient.class)) {
				String label = uiRecipient.getDisplayName();
				PortalGroup pGroup = ldapUtils.getPortalGroupByName(label);
				String portalId = pGroup.getId();
				BasicGroup groupRecipient = daoService.getGroupByLabel(portalId);
				if (groupRecipient == null) {
					groupRecipient = new BasicGroup();
					groupRecipient.setLabel(portalId);
				}
				return groupRecipient;
			}
		}
		return null;
	}

	/**
	 * Customized the messages.
	 * @param message
	 * @return
	 */
	private List<CustomizedMessage> getCustomizedMessages(final Message message) {
		final Set<Recipient> recipients = message.getRecipients();
		final String senderUid = message.getSender().getLogin();

		String contentWithoutExpTags = null;
		try {
			contentWithoutExpTags = customizer.customizeExpContent(message.getContent(), 
					message.getGroupSender().getLabel(), senderUid);
			if (logger.isDebugEnabled()) logger.debug("contentWithoutExpTags: " + contentWithoutExpTags);
		} catch (CreateMessageException e) {
			logger.error("discarding message with error " + e + " (this should not happen, the message should have been checked first!)");
		}

		final List<CustomizedMessage> customizedMessageList = new ArrayList<CustomizedMessage>();
		if (contentWithoutExpTags != null) {
		    for (Recipient recipient : recipients) {
			CustomizedMessage c = getCustomizedMessage(message, contentWithoutExpTags, recipient);
			customizedMessageList.add(c);
		    }
		}
		return customizedMessageList;
	}

	private CustomizedMessage getCustomizedMessage(final Message message,
			final String contentWithoutExpTags, Recipient recipient) {
		//the message is customized with user informations
		String msgContent = customizer.customizeDestContent(contentWithoutExpTags, recipient.getLogin());
		if (msgContent.length() > smsMaxSize) {
			msgContent = msgContent.substring(0, smsMaxSize);
		}
		// create the final message with all data needed to send it
		final CustomizedMessage customizedMessage = new CustomizedMessage();
		customizedMessage.setMessageId(message.getId());
		customizedMessage.setSenderId(message.getSender().getId());
		customizedMessage.setGroupSenderId(message.getGroupSender().getId());
		customizedMessage.setServiceId(message.getService() != null ? message.getService().getId() : null);
		customizedMessage.setUserAccountLabel(message.getAccount().getLabel());

		customizedMessage.setRecipiendPhoneNumber(recipient.getPhone());
		customizedMessage.setMessage(msgContent);
		return customizedMessage;
	}

	/**
	 * Send the customized message to the back office.
	 * @param customizedMessage
	 */
	private void sendCustomizedMessages(final CustomizedMessage customizedMessage) {
		final Integer messageId = customizedMessage.getMessageId();
		final Integer senderId = customizedMessage.getSenderId();
		final Integer groupSenderId = customizedMessage.getGroupSenderId();
		final Integer serviceId = customizedMessage.getServiceId();
		final String recipiendPhoneNumber = customizedMessage.getRecipiendPhoneNumber();
		final String userLabelAccount = customizedMessage.getUserAccountLabel();
		final String message = customizedMessage.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug("Sending to back office message with : " + 
				     " - message id = " + messageId + 
				     " - sender id = " + senderId + 
				     " - group sender id = " + groupSenderId + 
				     " - service id = " + serviceId + 
				     " - recipient phone number = " + recipiendPhoneNumber + 
				     " - user label account = " + userLabelAccount + 
				     " - message = " + message);
		}
		// send the message to the back office

		sendSmsClient.sendSMS(messageId, senderId, groupSenderId, serviceId, 
				recipiendPhoneNumber,	userLabelAccount, message);

	}

	private Boolean checkMaxSmsGroupQuota(final Integer nbToSend, final CustomizedGroup cGroup, final BasicGroup groupSender) {
		final Long quotaOrder = cGroup.getQuotaOrder();
		if (nbToSend <= quotaOrder) {
			return true;
		} else {
			final String mess = 
			    "Message necessite approbation : nombre maximum de sms par envoi pour le groupe d'envoi [" + 
			    groupSender.getLabel() + 
			    "] et groupe associated [" + cGroup.getLabel() + 
			    "]. Essai d'envoi de " + nbToSend + " message(s), nombre max par envoi = " + quotaOrder;
			logger.info(mess);
			return false;
		}
	}

	private void checkFrontOfficeQuota(final Integer nbToSend, final CustomizedGroup cGroup, final BasicGroup groupSender)
	    throws CreateMessageException.GroupQuotaException {

		if (logger.isDebugEnabled()) {
			final String mess = 
			    "Verification du quota front office pour le groupe d'envoi [" + 
			    groupSender.getLabel() + 
			    "] et groupe associated [" + cGroup.getLabel() + 
			    "]. Essai d'envoi de " + nbToSend + " message(s), quota = " + cGroup.getQuotaSms() + 
			    " , consomme = " + cGroup.getConsumedSms();
			logger.warn(mess);
		}
		if (cGroup.checkQuotaSms(nbToSend)) {
			logger.debug("checkFrontOfficeQuota : ok");
		} else {
			final String mess = 
			    "Erreur de quota pour le groupe d'envoi [" + groupSender.getLabel() + 
			    "] et groupe associated [" + cGroup.getLabel() + "]. Essai d'envoi de " + nbToSend + 
			    " message(s), quota = " + cGroup.getQuotaSms() + " , consomme = " + cGroup.getConsumedSms();
			logger.warn(mess);
			throw new CreateMessageException.GroupQuotaException(cGroup.getLabel());
		}
	}
	/**
	 * @return quotasOk
	 */ 
	private void checkBackOfficeQuotas(final Message message)
	throws BackOfficeUnrichableException, UnknownIdentifierApplicationException,
	InsufficientQuotaException {
		/////check the quotas with the back office/////
		Integer nbToSend = message.getRecipients().size();
		String accountLabel = message.getAccount().getLabel();
		checkBackOfficeQuotas(nbToSend, accountLabel);
	}

	private void checkBackOfficeQuotas(final Integer nbToSend, final String accountLabel) 
	throws BackOfficeUnrichableException, UnknownIdentifierApplicationException,
	InsufficientQuotaException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Request for WS SendSms method mayCreateAccountCheckQuotaOk with parameters \n" 
						+ "nbToSend = " + nbToSend + "\n" 
						+ "accountLabel = " + accountLabel);
			}

			sendSmsClient.mayCreateAccountCheckQuotaOk(nbToSend, accountLabel);

			if (logger.isDebugEnabled()) {
				logger.debug("checkQuotaOk: quota is ok to send all our messages"); 
			}

		} catch (UnknownIdentifierApplicationException e) {
			throw new UnknownIdentifierApplicationException(e.getMessage());
		} catch (InsufficientQuotaException e) {
			throw new InsufficientQuotaException(e.getMessage());
		} catch (UndeclaredThrowableException e) {
			String msg = checkWhySmsuapiFailed(e.getCause());
			throw new BackOfficeUnrichableException(msg);
		}
	}

	public String checkWhySmsuapiFailed(Throwable cause) {
		org.esupportail.smsu.services.ssl.InspectKeyStore inspect = new org.esupportail.smsu.services.ssl.InspectKeyStore();
		inspect.inspectTrustStore();
		logger.error("Unable to connect to smsuapi back office : " + cause);
		return null;
	}

	/**
	 * @param userGroup
	 * @return the sender group.
	 */
	private BasicGroup getGroupSender(final String userGroup) {
		// the sender group is set
		BasicGroup basicGroupSender = daoService.getGroupByLabel(userGroup);
		if (basicGroupSender == null) {
			basicGroupSender = new BasicGroup();
			basicGroupSender.setLabel(userGroup);
		}
		return basicGroupSender;
	}
	/**
	 * @param userGroup
	 * @return an account.
	 */
	private Account getAccount(final String userGroup) {
		CustomizedGroup groupSender = getRecurciveCustomizedGroupByLabel(userGroup);
		Account count;
		if (groupSender == null) {
			//Default account
			logger.warn("No account found. The default account is used : " + defaultAccount);
			count = daoService.getAccountByLabel(defaultAccount); 
		} else {
			count = groupSender.getAccount();
		}
		// the account is set
		return count;
	}

	/**
	 * @param groupId
	 * @return the customized group corresponding to a group
	 */
	private CustomizedGroup getRecurciveCustomizedGroupByLabel(String portalGroupId) {
	    return getRecurciveCustomizedGroupAndPathByLabel(portalGroupId, null);
	}

	/**
	 * @param portalGroupId
	 * @return the path corresponding to a group if a customized group exists, otherwise return null
	 */
	public String getRecursiveGroupPathByLabel(String portalGroupId) {
		StringBuilder path = new StringBuilder();
		if (getRecurciveCustomizedGroupAndPathByLabel(portalGroupId, path) == null)
			return null;
		else
			return path.toString();
	}
	
	private CustomizedGroup getRecurciveCustomizedGroupAndPathByLabel(String portalGroupId, StringBuilder path) {
		if (logger.isDebugEnabled()) {
			logger.debug("Search the customised group associated to the group : [" + portalGroupId + "]");
		}

		CustomizedGroup groupSender = null;

		while (true) {
		    if (path != null) path.insert(0, ".." + portalGroupId);
		    if (groupSender == null)
			    //search the customized group from the data base
			    groupSender = daoService.getCustomizedGroupByLabel(portalGroupId);

		    if (groupSender != null && path == null) return groupSender;
		    // if path != null, we must continue to compute the full path. 

		    if (logger.isDebugEnabled() && groupSender == null)
			logger.debug("Customized group not found : " + portalGroupId);

		    // if a parent group is found, search the corresponding customized group
		    String parentGroup = getSafeParentGroupIdByGroupId(portalGroupId);
		    if (parentGroup == null) return groupSender;
		    portalGroupId = parentGroup;
		}
	}

	private String getSafeParentGroupIdByGroupId(String portalGroupId) {
		String parentGroup = ldapUtils.getParentGroupIdByGroupId(portalGroupId);
		if (parentGroup == null || parentGroup.equals(portalGroupId))
			return null;
		else
			return parentGroup;
	}

	/**
	 * @param id
	 * @return the service.
	 */
	private Service getService(final Integer id) {
		if (id != null)
			return daoService.getServiceById(id);
		else
			return null;
	}

	private String getI18nString(String key) {
		return i18nService.getString(key, i18nService.getDefaultLocale());
	}
	private String getI18nString(String key, String arg1) {
		return i18nService.getString(key, i18nService.getDefaultLocale(), arg1);
	}
	private String getI18nString(String key, String arg1, String arg2) {
		return i18nService.getString(key, i18nService.getDefaultLocale(), arg1, arg2);
	}
	private String getI18nString(String key, String arg1, String arg2, String arg3) {
		return i18nService.getString(key, i18nService.getDefaultLocale(), arg1, arg2, arg3);
	}
	private String getI18nString(String key, String arg1, String arg2, String arg3, String arg4) {
		return i18nService.getString(key, i18nService.getDefaultLocale(), arg1, arg2, arg3, arg4);
	}

	///////////////////////////////////
	// Getter and Setter of smsMaxSize
	///////////////////////////////////
	/**
	 * @param smsMaxSize
	 */
	public void setSmsMaxSize(final Integer smsMaxSize) {
		this.smsMaxSize = smsMaxSize;
	}

	/**
	 * @return smsMaxSize
	 */
	public Integer getSmsMaxSize() {
		return smsMaxSize;
	}

	//////////////////////////////
	//  setters for spring objects
	//////////////////////////////
	/**
	 * @param sendSmsClient
	 */
	public void setSendSmsClient(final SendSmsClient sendSmsClient) {
		this.sendSmsClient = sendSmsClient;
	}

	/**
	 * @param usingBasicAuth
	 */
	public void setUsingBasicAuth(final String basicAuthUsername) {
		this.usingBasicAuth = !StringUtils.isEmpty(basicAuthUsername);
	}

	/**
	 * Standard setter used by spring.
	 * @param schedulerUtils
	 */
	public void setSchedulerUtils(final SchedulerUtils schedulerUtils) {
		this.schedulerUtils = schedulerUtils;
	}

	/**
	 * Standard setter used by spring.
	 * @param smtpServiceUtils
	 */
	public void setSmtpServiceUtils(final SmtpServiceUtils smtpServiceUtils) {
		this.smtpServiceUtils = smtpServiceUtils;
	}

	/**
	 * Standard setter used by spring.
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	///////////////////////////////////
	// Getter and Setter of i18nService
	///////////////////////////////////
	/**
	 * Set the i18nService.
	 * @param i18nService
	 */
	public void setI18nService(final I18nService i18nService) {
		this.i18nService = i18nService;
	}

	/**
	 * @return the i18nService.
	 */
	public I18nService getI18nService() {
		return i18nService;
	}

	///////////////////////////////////////////////
	// Getter and Setter of defaultSupervisorLogin
	///////////////////////////////////////////////
	/**
	 * @return the defaultSupervisorLogin.
	 */
	public String getDefaultSupervisorLogin() {
		return defaultSupervisorLogin;
	}

	/**
	 * @param defaultSupervisorLogin
	 */
	public void setDefaultSupervisorLogin(final String defaultSupervisorLogin) {
		this.defaultSupervisorLogin = defaultSupervisorLogin;
	}

	//////////////////////////////////
	// Getter and Setter of daoService
	//////////////////////////////////
	/**
	 * @return the daoService.
	 */
	public DaoService getDaoService() {
		return daoService;
	}

	/**
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of customizer
	//////////////////////////////////////////////////////////////

	/**
	 * @param customizer
	 */
	public void setCustomizer(final ContentCustomizationManager customizer) {
		this.customizer = customizer;
	}

	/**
	 * @return the ContentCustomizationManager
	 */
	public ContentCustomizationManager getCustomizer() {
		return customizer;
	}

	//////////////////////////////////////////////////////////////
	// Setter of defaultAccount
	//////////////////////////////////////////////////////////////
	/**
	 * @param defaultAccount
	 */
	public void setDefaultAccount(final String defaultAccount) {
		this.defaultAccount = defaultAccount;
	}

	//////////////////////////////////////////////////////////////
	// Setter of phoneNumberPattern
	//////////////////////////////////////////////////////////////
	/**
	 * @param phoneNumberPattern
	 */
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

	/**
	 * @return userEmailAttribute
	 */
	public String getUserEmailAttribute() {
		return userEmailAttribute;
	}

	/**
	 * @param userEmailAttribute
	 */
	public void setUserEmailAttribute(final String userEmailAttribute) {
		this.userEmailAttribute = userEmailAttribute;
	}

	public void setSmsuPersonAttributesGroupStore(
			final SmsuPersonAttributesGroupStore smsuPersonAttributesGroupStore) {
		this.smsuPersonAttributesGroupStore = smsuPersonAttributesGroupStore;
	}

	public SmsuPersonAttributesGroupStore getSmsuPersonAttributesGroupStore() {
		return smsuPersonAttributesGroupStore;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(final UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}