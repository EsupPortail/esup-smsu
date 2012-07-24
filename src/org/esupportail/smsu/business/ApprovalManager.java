package org.esupportail.smsu.business;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIMessage;


/**
 * Business layer concerning smsu service.
 *
 */
public class ApprovalManager {

	/**
	 * const.
	 */
	private static final String NONE = "Aucun";
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link LdapUtils}.
	 */
	private LdapUtils ldapUtils;

	/**
	 * {@link DaoService}.
	 */
	private SendSmsManager sendSmsManager;

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
	public ApprovalManager() {
		super();
	}



	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	
	/**
	 * @param idUser
	 * @return the UI messages.
	 */
	public List<UIMessage> getApprovalUIMessages(final User user) {
		List<Message> messages = getApprovalMessagesASupervisorCanApprove(user);

		Map<String, LdapUser> ldapUserByUid = getLdapUserByUid(senderLogins(messages));

		List<UIMessage> uimessages = new ArrayList<UIMessage>();
		for (Message mess : messages) {
			String displayName = retreiveNiceDisplayName(ldapUserByUid, mess.getSender().getLogin());
			String groupName = retreiveNiceGroupName(mess.getGroupRecipient());					
			uimessages.add(new UIMessage(displayName, groupName, mess));
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

	private List<Message> getApprovalMessagesASupervisorCanApprove(User user) {
		Person p = daoService.getPersonByLogin(user.getId());
		List<Message> messages = new ArrayList<Message>();
		for (Message mess : daoService.getApprovalMessages())
			if (mess.getSupervisors().contains(p))
				messages.add(mess);
		return messages;
	}

	private String retreiveNiceGroupName(BasicGroup recipientGroup) {
		return recipientGroup != null ?
			retreiveNiceGroupName(recipientGroup.getLabel()) : NONE;
	}

	private String retreiveNiceGroupName(String groupLabel) {
		String groupName = NONE;

			try {
				groupName = ldapUtils.getUserDisplayNameByUserUid(groupLabel);
			} catch (LdapUserNotFoundException e) {

				groupName = ldapUtils.getGroupNameByUid(groupLabel);
			} 

		return groupName;
	}

	/**
	 * Update the State of the message.
	 * @param uiMessage
	 */
	public void updateUIMessage(final UIMessage uiMessage) {
		Message message = daoService.getMessageById(uiMessage.getId());
		message.setStateAsEnum(MessageStatus.CANCEL);
		daoService.updateMessage(message);

	}

	/**
	 * Treat the UI message.
	 * @param uiMessage
	 * @throws CreateMessageException.WebService
	 */
	public void treatUIMessage(final UIMessage uiMessage) throws CreateMessageException.WebService {
		Message message = daoService.getMessageById(uiMessage.getId());
		logger.debug("Message approved");
		message.setStateAsEnum(MessageStatus.IN_PROGRESS);
		sendSmsManager.treatMessage(message);

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

	//////////////////////////////////////////////////////////////
	// Setter of spring object sendSmsManager
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setSendSmsManager(final SendSmsManager sendSmsManager) {
		this.sendSmsManager = sendSmsManager;
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


}
