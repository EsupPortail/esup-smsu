package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.text.DateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
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
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.CreateMessageException.EmptyGroup;
import org.esupportail.smsu.services.GroupUtils;
import org.esupportail.smsu.services.UrlGenerator;
import org.esupportail.smsu.services.client.SmsuapiWS;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.scheduler.SchedulerUtils;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.smsu.web.controllers.InvalidParameterException;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.services.client.SmsuapiWS.AuthenticationFailedException;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;


public class SendSmsManager  {

	@Autowired private DaoService daoService;
	@Autowired private I18nService i18nService;
	@Autowired private SmsuapiWS smsuapiWS;
	@Autowired private SmtpServiceUtils smtpServiceUtils;
	@Autowired private LdapUtils ldapUtils;
	@Autowired private GroupUtils groupUtils;
	@Autowired private SchedulerUtils schedulerUtils;
	@Autowired private UrlGenerator urlGenerator;
	@Autowired private ContentCustomizationManager customizer;

	
	/**
	 * the default Supervisor login when the max SMS number is reach.
	 */
	private String defaultSupervisorLogin;

	/**
	 * The SMS max size.
	 */
	private Integer smsMaxSize;

	/**
	 * The default account.
	 */
	private String defaultAccount;

	/**
	 * the phone number validation pattern.
	 */
	private String phoneNumberPattern;

	/**
	 * the LDAP Email attribute.
	 */
	private String userEmailAttribute;
	
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Pricipal methods
	//////////////////////////////////////////////////////////////
	public int sendMessage(UINewMessage msg, HttpServletRequest request) throws CreateMessageException {
		Message message = composeMessage(msg);

		//TODO verify unneeded 
		// by default, a SMS is considered as a sent one.
		//message.setStateAsEnum(MessageStatus.IN_PROGRESS);

		treatMessage(message, request);

		return message.getId();
	}

	public Message composeMessage(UINewMessage msg) throws CreateMessageException {
		Message message = createMessage(msg);
		daoService.addMessage(message);
		return message;
	}

	private Message createMessage(UINewMessage msg) throws CreateMessageException  {
		Service service = getService(msg.serviceKey);

		Set<Recipient> recipients = getRecipients(msg, msg.serviceKey);
		BasicGroup groupRecipient = getGroupRecipient(msg.recipientGroup);
		BasicGroup groupSender = getGroupSender(msg.senderGroup);
		MessageStatus messageStatus = getWorkflowState(recipients.size(), groupSender, groupRecipient);
		Person sender = getSender(msg.login);

		// test if customizeExpContent raises a CreateMessageException
		customizer.customizeExpContent(msg.content, groupSender, sender);
				
		Message message = new Message();
		message.setContent(msg.content);
		if (msg.smsTemplate != null) message.setTemplate(getMessageTemplate(msg.smsTemplate));
		message.setSender(sender);
		message.setAccount(getAccount(msg.senderGroup));
		message.setService(service);
		message.setGroupSender(groupSender);
		message.setRecipients(recipients);
		message.setGroupRecipient(groupRecipient);			
		message.setStateAsEnum(messageStatus);				
		message.setSupervisors(mayGetSupervisorsOrNull(message));				
		message.setDate(new Date());
		if (msg.mailToSend != null) message.setMail(getMail(message, msg.mailToSend));
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
	 * @param request 
	 * @return null or an error message (key into i18n properties)
	 * @throws CreateMessageException.WebService
	 */
	public void treatMessage(final Message message, HttpServletRequest request) throws CreateMessageException.WebService {
		try {
			if (message.getStateAsEnum().equals(MessageStatus.NO_RECIPIENT_FOUND))
				;
			else if (message.getStateAsEnum().equals(MessageStatus.WAITING_FOR_APPROVAL))
				// envoi du mail
				sendApprovalMailToSupervisors(message, request);
			else 
				maySendMessageInBackground(message);
		} catch (AuthenticationFailedException e) {
			message.setStateAsEnum(MessageStatus.WS_ERROR);
			daoService.updateMessage(message);
			logger.error("Application unknown", e);
			throw new CreateMessageException.WebServiceUnknownApplication(e);
		} catch (InsufficientQuotaException e) {
			message.setStateAsEnum(MessageStatus.WS_QUOTA_ERROR);
			daoService.updateMessage(message);
			logger.error("Quota error", e);
			throw new CreateMessageException.WebServiceInsufficientQuota(e);
		} catch (HttpException e) {
			message.setStateAsEnum(MessageStatus.WS_ERROR);
			daoService.updateMessage(message);
			throw new CreateMessageException.BackOfficeUnreachable(e);
		}
	}

	/**
	 * Used to send message in state waiting_for_sending.
	 * @throws InsufficientQuotaException 
	 * @throws HttpException 
	 */
	public void sendWaitingForSendingMessage() throws HttpException, InsufficientQuotaException {
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
	 * @param request 
	 * @return
	 */
	private void sendApprovalMailToSupervisors(final Message message, HttpServletRequest request) {
		sendMailToSupervisors(message, MessageStatus.WAITING_FOR_APPROVAL, null, request);
	}

	public void sendMailMessageApprovedOrCanceled(Message message, MessageStatus status, User currentUser, HttpServletRequest request) {
		sendMailToSupervisors(message, status, currentUser, request);
		sendMailToSenderMessageApprovedOrCanceled(message, status, currentUser);
	}

	private void sendMailToSupervisors(final Message message, MessageStatus status, User currentUser, HttpServletRequest request) {
		CustomizedGroup cGroup = getSupervisorCustomizedGroup(message);
		List<String> toList = getSupervisorsMails(getSupervisors(cGroup));
		if (toList == null || toList.isEmpty()) {
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
			textBodyParam3 = urlGenerator.goTo(request, "/approvals");
		}
		String senderName = ldapUtils.getUserDisplayName(message.getSender());
		String cGroupName = groupUtils.getGroupDisplayName(cGroup);
		String subject = getI18nString(subjectKey, senderName);
		String textBody = getI18nString(textBodyKey, cGroupName,
						i18nMsgDate(message), i18nMsgTime(message), textBodyParam3);
		smtpServiceUtils.sendHTMLMessage(toList, null, subject, textBody);
	}

	private void sendMailToSenderMessageApprovedOrCanceled(final Message message, MessageStatus status, User currentUser) {
		List<String> toList = ldapUtils.getUserEmailsAdressByUids(Collections.singleton(message.getSender().getLogin()));
		if (toList.isEmpty()) {
			logger.error("no way to notify sender that message has been approved");
			return;
		}

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


	private void maySendMessageInBackground(final Message message) throws HttpException, InsufficientQuotaException {
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

		for (String otherAdresse : mailToSend.getMailOtherRecipients()) {
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
			final String mailSubject = mail.getSubject();
			//the original content
			final String originalContent = mail.getContent();
			try {
				final String contentWithoutExpTags = customizer.customizeExpContent(originalContent, message.getGroupSender(), message.getSender());
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
	@SuppressWarnings("unused") //TODO
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
			
			String parentGroup = null;
			if (parentGroup == null) return null;
			return getSupervisorCustomizedGroupByLabel(parentGroup);
		}
	}

	private Set<Recipient> getRecipients(UINewMessage msg, String serviceKey) throws EmptyGroup {
		Set<Recipient> recipients = new HashSet<Recipient>();
		if (msg.recipientPhoneNumbers != null) addPhoneNumbersRecipients(recipients, msg.recipientPhoneNumbers);
		if (msg.recipientLogins != null) addLoginsRecipients(recipients, msg.recipientLogins, serviceKey);
		addGroupRecipients(recipients, msg.recipientGroup, serviceKey);
		return recipients;
	}

	private void addPhoneNumbersRecipients(Set<Recipient> recipients, List<String> phoneNumbers) {
		for (String phoneNumber : phoneNumbers)
			mayAdd(recipients, phoneNumber, null);
	}

	private void addLoginsRecipients(Set<Recipient> recipients, List<String> logins, String serviceKey) {
		List<LdapUser> users = ldapUtils.getConditionFriendlyLdapUsersFromUid(logins, serviceKey);
		for (LdapUser user : users) {
			String phoneNumber = ldapUtils.getUserPagerByUser(user);
			if (phoneNumber == null)
				throw new InvalidParameterException("user " + user.getId()+ " has no phone number to send SMS to");
			mayAdd(recipients, phoneNumber, user.getId());
		}
	}

	private void addGroupRecipients(Set<Recipient> recipients, final String groupId, String serviceKey) throws EmptyGroup {
		if (groupId == null) return;
		
				// Group users are search from the portal.
				List<LdapUser> groupUsers = getUsersByGroup(groupId,serviceKey);
				if (groupUsers.isEmpty())
					throw new CreateMessageException.EmptyGroup(groupId);
					
				//users are added to the recipient list.
				for (LdapUser ldapUser : groupUsers) {
					String phone = ldapUtils.getUserPagerByUser(ldapUser);
					String login = ldapUser.getId();
					mayAdd(recipients, phone, login);
				}
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
	 * @param groupId
	 * @param serviceKey 
	 * @return the user list.
	 */
	public List<LdapUser> getUsersByGroup(final String groupId, String serviceKey) {
		logger.debug("Search users for group [" + groupId + "]");

		List<String> uids = groupUtils.getMemberIds(groupId);

		List<LdapUser> users = ldapUtils.getConditionFriendlyLdapUsersFromUid(uids, serviceKey);
		logger.debug("found " + uids.size() + " users in group " + groupId + " and " + users.size() + " users having pager+CG");
		return users;
	}

	/**
	 * get the message template.
	 */
	private Template getMessageTemplate(final String templateLabel) {
		return daoService.getTemplateByLabel(templateLabel);		 
	}

	/**
	 * get the message sender.
	 */
	public Person getSender(final String strLogin) {
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
		if (cGroup == null)
			throw new InvalidParameterException("invalid sender group");

		if (nbRecipients.equals(0)) {
			throw new InvalidParameterException("NO_RECIPIENT_FOUND");
		}
		checkFrontOfficeQuota(nbRecipients, cGroup, groupSender);
		if (groupRecipient != null || !checkMaxSmsGroupQuota(nbRecipients, cGroup, groupSender)) {
			return MessageStatus.WAITING_FOR_APPROVAL;
		} else {
			return MessageStatus.IN_PROGRESS;
		}
	}

	/**
	 * @return the recipient group, or null.
	 */
	private BasicGroup getGroupRecipient(String label) {
		if (label == null) return null;
		if (logger.isDebugEnabled()) logger.debug("get recipient group");

				BasicGroup groupRecipient = daoService.getGroupByLabel(label);
				if (groupRecipient == null) {
					groupRecipient = new BasicGroup();
					groupRecipient.setLabel(label);
				}
				return groupRecipient;
	}

	/**
	 * Customized the messages.
	 * @param message
	 * @return
	 */
	private List<CustomizedMessage> getCustomizedMessages(final Message message) {
		final Set<Recipient> recipients = message.getRecipients();
		String contentWithoutExpTags = null;
		try {
			contentWithoutExpTags = customizer.customizeExpContent(message.getContent(), message.getGroupSender(), message.getSender());
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
	 * @throws InsufficientQuotaException 
	 * @throws HttpException 
	 */
	private void sendCustomizedMessages(final CustomizedMessage customizedMessage) throws HttpException, InsufficientQuotaException {
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

		smsuapiWS.sendSMS(messageId, senderId, recipiendPhoneNumber, userLabelAccount, message);

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
	 * @throws InsufficientQuotaException 
	 * @throws HttpException 
	 */ 
	private void checkBackOfficeQuotas(final Message message) throws HttpException, InsufficientQuotaException {
		/////check the quotas with the back office/////
		Integer nbToSend = message.getRecipients().size();
		String accountLabel = message.getAccount().getLabel();
		checkBackOfficeQuotas(nbToSend, accountLabel);
	}

	private void checkBackOfficeQuotas(final Integer nbToSend, final String accountLabel) throws HttpException, InsufficientQuotaException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Request for WS SendSms method mayCreateAccountCheckQuotaOk with parameters \n" 
						+ "nbToSend = " + nbToSend + "\n" 
						+ "accountLabel = " + accountLabel);
			}

			smsuapiWS.mayCreateAccountCheckQuotaOk(nbToSend, accountLabel);
			
			if (logger.isDebugEnabled()) {
				logger.debug("checkQuotaOk: quota is ok to send all our messages"); 
			}

		} catch (HttpException.Unreachable e) {
			checkWhySmsuapiFailed(e.getCause());
			throw e;
		}
	}

	public void checkWhySmsuapiFailed(Throwable cause) {
		org.esupportail.smsu.services.ssl.InspectKeyStore inspect = new org.esupportail.smsu.services.ssl.InspectKeyStore();
		inspect.inspectTrustStore();
		logger.error("Unable to connect to smsuapi back office : " + cause);
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

	
	public List<UserGroup> getUserGroupLeaves(String uid) {
		List<UserGroup> l = new LinkedList<UserGroup>();
		for (UserGroup group : groupUtils.getUserGroupsPlusSelfGroup(uid)) {
		    if (getCustomizedGroupByLabel(group.id) != null)
		    	l.add(group);
		}
		return l;
	}

	private List<String> keepGroupLeaves(Set<String> ids) {
		logger.debug("keepGroupLeaves: given ids " + ids);

		SortedMap<String, String> pathToId = new TreeMap<String, String>();
		for (String id : ids) {
			String path = getRecursiveGroupPathByLabel(id);
			if (path != null) pathToId.put(path, id);
		}
		logger.debug("keepGroupLeaves: pathToId: " + pathToId);

		List<String> keptIds = new LinkedList<String>();
		for (String path : keepLeaves(pathToId.keySet().iterator()))
			keptIds.add(pathToId.get(path));
		logger.debug("keepGroupLeaves: keptIds: " + keptIds);
		return keptIds;
	}

	/**
	 * @param it iterator on a sorted collection of strings 
	 * @return the strings without the prefix strings
	 * 
	 * example: { "a/b", "a", "c" } returns { "a/b", "c" }
	 */
	private List<String> keepLeaves(Iterator<String> it) {
		List<String> keptIds = new LinkedList<String>();
		String prev = null;
		while (it.hasNext()) {
			String current = it.next();
			if (prev != null && prev.startsWith(current)) 
				; // skip current
			else if (prev == null || current.startsWith(prev))					
				prev = current; // skip prev
			else {
				keptIds.add(prev);
				prev = current;
			}
		}
		if (prev != null) keptIds.add(prev);
		return keptIds;
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
		    String parentGroup = null;
		    if (parentGroup == null) return groupSender;
		}
	}

	private Service getService(final String key) {
		if (key != null)
			return daoService.getServiceByKey(key);
		else
			return null;
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
	// setters
	///////////////////////////////////
	@Required
	public void setSmsMaxSize(final Integer smsMaxSize) {
		this.smsMaxSize = smsMaxSize;
	}

	@Required
	public void setDefaultSupervisorLogin(final String defaultSupervisorLogin) {
		this.defaultSupervisorLogin = defaultSupervisorLogin;
	}

	@Required
	public void setDefaultAccount(final String defaultAccount) {
		this.defaultAccount = defaultAccount;
	}

	@Required
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

	@Required
	public void setUserEmailAttribute(final String userEmailAttribute) {
		this.userEmailAttribute = userEmailAttribute;
	}
}