package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
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
	 * @return the messages.
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 */
	public List<UIMessage> getMessages(final Integer userGroupId, final Integer userAccountId, 
			final Integer userServiceId, final Integer userTemplateId, final Integer userUserId, 
			final Date beginDate, final Date endDate) {
		
		List<UIMessage> uimessages = new ArrayList<UIMessage>();
		List<Message> messages = daoService.getMessages(userGroupId, userAccountId, userServiceId, 
								 userTemplateId, userUserId, beginDate, endDate);
		
		List<String> displayNameList = new ArrayList<String>();
		List<LdapUser> ldapUserList = new ArrayList<LdapUser>();
		
		
		if (messages != null) {
			for (Message mess : messages) {
				if (!displayNameList.contains(mess.getUserUserLabel())) {
					displayNameList.add(mess.getUserUserLabel());
				}
			}

			if (!displayNameList.isEmpty()) {
				ldapUserList = ldapUtils.getUsersByUids(displayNameList);
			}

			for (Message mess : messages) {
				String displayName = NONE;
				String stateMessage;
				String stateMail = NONE;
				String groupName;
				String groupId;
				
				// 1 - Retrieve displayName
				Boolean testVal = true;
				LdapUser ldapUser;
				int i = 0;

				while ((i < ldapUserList.size()) && testVal) {
					ldapUser = ldapUserList.get(i);
					logger.debug("ldapUser.getId is: " + ldapUser.getId());
					logger.debug("mess.getUserUserLabel is: " + mess.getUserUserLabel());
					if (ldapUser.getId().equals(mess.getUserUserLabel())) {
						displayName = ldapUser.getAttribute(displayNameAttributeAsString);
						logger.debug("displayName is: " + displayName);
						testVal = false;
					}
					i++;
				}


				if (displayName.equals(NONE)) {
					displayName = mess.getUserUserLabel();
				} else {
					displayName = displayName + "  (" + mess.getUserUserLabel() + ")"; 
				}

				MessageStatus messageStatus = mess.getStateAsEnum();
				if (logger.isDebugEnabled()) {
					logger.debug("mess.getStateAsEnum : " + messageStatus);
				}				
				stateMessage = messageStatusI18nMessage(messageStatus);

				if (logger.isDebugEnabled()) {
					logger.debug("mess.getMail : " + mess.getMail());
				}				
				if (mess.getMail() != null) {
					MailStatus mailStatus = mess.getMail().getStateAsEnum();
					if (logger.isDebugEnabled()) {
						logger.debug("mess.getMail.getStateAsEnum : " + mailStatus);
					}
					stateMail = mailStatusI18nMessage(mailStatus);
				}
				
				
				groupId = mess.getUserGroupLabel();
				
				try {
					groupName = ldapUtils.getUserDisplayNameByUserUid(groupId);
				} catch (LdapUserNotFoundException e) {
					
					groupName = ldapUtils.getGroupNameByUid(groupId);
					if (groupName == null) {
						
						groupName = groupId;
					}	
				}
				uimessages.add(new UIMessage(stateMessage, stateMail, displayName, groupName, mess));
			}
		}

		 return uimessages;
	}

	private String i18nMessageKeyToMessage(String i18nKey) {
		return getI18nService().getString(i18nKey, getI18nService().getDefaultLocale());
	}

	private String messageStatusI18nMessage(MessageStatus messageStatus) {
		String i18nKey = messageStatusI18nMessageKey(messageStatus);
		return i18nKey != null ? i18nMessageKeyToMessage(i18nKey) : NONE;
	}

	private String mailStatusI18nMessage(MailStatus mailStatus) {
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
		case FO_QUOTA_ERROR:
			return "MSG.STATE.FO.QUOTA.ERROR";
		case FO_NB_MAX_CUSTOMIZED_GROUP_ERROR:
			return "MSG.STATE.CUSTOMIZED.GROUP.ERROR";
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
	////////////////////////////
	// Setter of daoService
	////////////////////////////
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	////////////////////////////
	// Setter of ldapUtils
	////////////////////////////
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
