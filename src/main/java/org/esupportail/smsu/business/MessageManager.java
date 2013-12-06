package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.domain.beans.mail.MailStatus;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIMessage;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Business layer concerning smsu service.
 *
 */
public class MessageManager {

	@Autowired private DaoService daoService;
	@Autowired private I18nService i18nService;
	@Autowired private LdapUtils ldapUtils;
	
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public MessageManager() {
		super();
	}



	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	
	/**
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 * @return the UI messages.
	 */
	public List<UIMessage> getMessages(final Integer userGroupId, final Integer userAccountId, 
			final Integer userServiceId, final Integer userTemplateId, final Integer userUserId, 
			final Date beginDate, final Date endDate) {

		java.sql.Date beginDateSQL = 
			beginDate == null ? null : new java.sql.Date(beginDate.getTime()); // get rid of HH:MM:SS
		java.sql.Date endDateSQL =
			endDate == null ? null : new java.sql.Date(addOneDay(endDate).getTime());
	
		List<Message> messages = daoService.getMessages(userGroupId, userAccountId, userServiceId, 
								 userTemplateId, userUserId, beginDateSQL, endDateSQL);
		return convertToUI(messages);
	}

	public List<String> getUIRecipients(int messageId) {
		Message message = getMessage(messageId);
		Set<Recipient> recipients = message.getRecipients();	
		if (recipients == null) return null;
		
		List<String> result = new LinkedList<String>();
		for (Recipient r : recipients) {
		    result.add(r.getLogin() != null ? r.getLogin() : r.getPhone());
		}
		return result;
	}

	/**
	 * @param messageId
	 * @return a message
	 */
	public Message getMessage(final Integer messageId) {
		return daoService.getMessageById(messageId);
	}

	public UIMessage getUIMessage(final Integer messageId) {
		return convertToUI(Collections.singletonList(getMessage(messageId))).get(0);
	}
	
	public List<UIMessage> convertToUI(List<Message> messages) {
		Map<String, String> id2displayName = getIdToDisplayName(senderLogins(messages));

		List<UIMessage> uimessages = new ArrayList<UIMessage>();
		for (Message mess : messages) {
			uimessages.add(convertToUI(mess, id2displayName));
		}
		return uimessages;
	}

	private UIMessage convertToUI(Message mess,
			Map<String, String> id2displayName) {
		UIMessage r = new UIMessage();
		r.id = mess.getId();
		r.nbRecipients = mess.getRecipients().size();
		r.supervisors= convertToUI(mess.getSupervisors());
		r.senderName = retreiveNiceDisplayName(id2displayName, mess.getSender().getLogin());
		r.groupSenderName = retreiveNiceGroupName(mess.getGroupSender());
		r.groupRecipientName = retreiveNiceGroupName(mess.getGroupRecipient());
		r.stateMessage = messageStatusI18nMessage(mess.getStateAsEnum());
		r.stateMail = mailStatusI18nMessage(mess.getMail());
		return r;
	}
	
	private List<String> convertToUI(Set<Person> supervisors) {
		if (supervisors == null) return null;
		List<String> t = new LinkedList<String>();
		for (Person p : supervisors)
			t.add(p.getLogin());
		return t;
	}

	private Date addOneDay(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	private LinkedHashSet<String> senderLogins(List<Message> messages) {
		LinkedHashSet<String> l = new LinkedHashSet<String>();	       		
		for (Message mess : messages)
			l.add(mess.getSender().getLogin());
		return l;
	}

	private Map<String, String> getIdToDisplayName(Iterable<String> uids) {
		Map<String, String> result = new TreeMap<String, String>();
		for (LdapUser u : ldapUtils.getUsersByUids(uids))
		    result.put(u.getId(), ldapUtils.getUserDisplayName(u));
		return result;
	}

	private String retreiveNiceDisplayName(Map<String, String> id2displayName, String senderLogin) {
		logger.debug("mess.getSender.getLogin is: " + senderLogin);
		
		String displayName = id2displayName.get(senderLogin);
		if (displayName != null) {
			logger.debug("displayName is: " + displayName);
			return displayName + "  (" + senderLogin + ")"; 
		} else {
			return senderLogin;
		}
	}

	private String retreiveNiceGroupName(BasicGroup recipientGroup) {
		return recipientGroup != null ?
			ldapUtils.getGroupDisplayName(recipientGroup.getLabel()) : "";
	}

	private String i18nMessageKeyToMessage(String i18nKey) {
		return i18nService.getString(i18nKey, i18nService.getDefaultLocale());
	}

	private String messageStatusI18nMessage(MessageStatus messageStatus) {
		if (logger.isDebugEnabled()) {
			logger.debug("mess.getStateAsEnum : " + messageStatus);
		}				
		String i18nKey = messageStatusI18nMessageKey(messageStatus);
		return i18nKey != null ? i18nMessageKeyToMessage(i18nKey) : "";
	}

	private String mailStatusI18nMessage(Mail mail) {
		return mail != null ? mailStatusI18nMessage(mail.getStateAsEnum()) : "";
	}

	private String mailStatusI18nMessage(MailStatus mailStatus) {
		if (logger.isDebugEnabled()) {
			logger.debug("mess.getMail.getStateAsEnum : " + mailStatus);
		}
		String i18nKey = mailStatusI18nMessageKey(mailStatus);
		return i18nKey != null ? i18nMessageKeyToMessage(i18nKey) : "";
	}

	private String messageStatusI18nMessageKey(MessageStatus messageStatus) {
		switch (messageStatus) {
		case IN_PROGRESS:
			return "MSG.STATE.IN.PROGRESS";
		case WAITING_FOR_APPROVAL:
			return "MSG.STATE.IN.APPROVAL";
		case WAITING_FOR_SENDING:
			return "MSG.STATE.IN.SENDING";
		case SENT:
			return "MSG.STATE.SENT";
		case WS_ERROR:
			return "MSG.STATE.WS.ERROR";
		case LDAP_ERROR:
			return "MSG.STATE.LDAP.ERROR";
		case WS_QUOTA_ERROR:
			return "MSG.STATE.WS.QUOTA.ERROR";
		case CANCEL:
			return "MSG.STATE.CANCEL";
		case NO_RECIPIENT_FOUND:
			return "MSG.STATE.NO.RECIPIENT.FOUND";
		}
		return null;
	}

	private String mailStatusI18nMessageKey(MailStatus mailStatus) {
		switch (mailStatus) {
		case SENT:
		    return "MSG.STATE.MAIL.SENT";
		case WAITING:
		    return "MSG.STATE.MAIL.WAITING";
		case ERROR:
		    return "MSG.STATE.MAIL.ERROR";
		}
		return null;
	}
	
	/**
	 * @param messageId
	 * @return a message
	 */
	public Message getMessage(final Integer messageId) {
		return daoService.getMessageById(messageId);
	}

	///////////////////////////////////////
	//  setter for spring object daoService
	//////////////////////////////////////	
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	//////////////////////////////////////////////////////////////
	// Setter of spring object ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @param displayNameAttributeAsString the displayNameAttributeAsString to set
	 */
	public void setDisplayNameAttributeAsString(
			final String displayNameAttributeAsString) {
		this.displayNameAttributeAsString = displayNameAttributeAsString;
	}

	/**
	 * @return the displayNameAttributeAsString
	 */
	public String getDisplayNameAttributeAsString() {
		return displayNameAttributeAsString;
	}

	/**
	 * @param i18nService the i18nService to set
	 */
	public void setI18nService(final I18nService i18nService) {
		this.i18nService = i18nService;
	}

	/**
	 * @return the i18nService
	 */
	public I18nService getI18nService() {
		return i18nService;
	}

}
