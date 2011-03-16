package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.beans.mail.MailStatus;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIMessage;


/**
 * Business layer concerning smsu service.
 *
 */
public class MessageManager {
	
	/**
	 * const.
	 */
	private static final String NONE = "";
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link i18nService}.
	 */
	private I18nService i18nService;
	
	/**
	 * {@link LdapUtils}.
	 */
	private LdapUtils ldapUtils;

	/**
	 * displayName.
	 */
	private String displayNameAttributeAsString;

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
		
		List<Message> messages = daoService.getMessages(userGroupId, userAccountId, userServiceId, 
								 userTemplateId, userUserId, beginDate, endDate);
		
		Map<String, LdapUser> ldapUserByUid = getLdapUserByUid(senderLogins(messages));

		List<UIMessage> uimessages = new ArrayList<UIMessage>();
		for (Message mess : messages) {
			String displayName = retreiveNiceDisplayName(ldapUserByUid, mess.getSender().getLogin());
			String groupName = retreiveNiceGroupName(mess.getGroupSender().getLabel());
			String stateMessage = messageStatusI18nMessage(mess.getStateAsEnum());
			String stateMail = mailStatusI18nMessage(mess.getMail());
			uimessages.add(new UIMessage(stateMessage, stateMail, displayName, groupName, mess));
		}
		return uimessages;
	}

	private LinkedHashSet<String> senderLogins(List<Message> messages) {
		LinkedHashSet<String> l = new LinkedHashSet<String>();	       		
		for (Message mess : messages)
			l.add(mess.getSender().getLogin());
		return l;
	}

	private Map<String, LdapUser> getLdapUserByUid(Iterable<String> uids) {
		Map<String, LdapUser> ldapUserByUid = new TreeMap<String, LdapUser>();
		for (LdapUser u : ldapUtils.getUsersByUids(uids))
		    ldapUserByUid.put(u.getId(), u);
		return ldapUserByUid;
	}

	private String retreiveNiceDisplayName(Map<String, LdapUser> ldapUserByUid, String senderLogin) {
		logger.debug("mess.getSender.getLogin is: " + senderLogin);
		
		LdapUser ldapUser = ldapUserByUid.get(senderLogin);
		if (ldapUser != null) {
			String displayName = ldapUser.getAttribute(displayNameAttributeAsString);
			logger.debug("displayName is: " + displayName);
			return displayName + "  (" + senderLogin + ")"; 
		} else {
			return senderLogin;
		}
	}

	private String retreiveNiceGroupName(String groupLabel) {
		String groupName = NONE;

			try {
				groupName = ldapUtils.getUserDisplayNameByUserUid(groupLabel);
			} catch (LdapUserNotFoundException e) {

				groupName = ldapUtils.getGroupNameByUid(groupLabel);
				if (groupName == null) {
					groupName = groupLabel;
				}	
			} 

		return groupName;
	}

	private String i18nMessageKeyToMessage(String i18nKey) {
		return getI18nService().getString(i18nKey, getI18nService().getDefaultLocale());
	}

	private String messageStatusI18nMessage(MessageStatus messageStatus) {
		if (logger.isDebugEnabled()) {
			logger.debug("mess.getStateAsEnum : " + messageStatus);
		}				
		String i18nKey = messageStatusI18nMessageKey(messageStatus);
		return i18nKey != null ? i18nMessageKeyToMessage(i18nKey) : NONE;
	}

	private String mailStatusI18nMessage(Mail mail) {
		return mail != null ? mailStatusI18nMessage(mail.getStateAsEnum()) : NONE;
	}

	private String mailStatusI18nMessage(MailStatus mailStatus) {
		if (logger.isDebugEnabled()) {
			logger.debug("mess.getMail.getStateAsEnum : " + mailStatus);
		}
		String i18nKey = mailStatusI18nMessageKey(mailStatus);
		return i18nKey != null ? i18nMessageKeyToMessage(i18nKey) : NONE;
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
