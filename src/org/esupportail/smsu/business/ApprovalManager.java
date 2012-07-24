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
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
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
	 * {@link MessageManager}.
	 */
	private MessageManager messageManager;

	/**
	 * {@link DaoService}.
	 */
	private SendSmsManager sendSmsManager;

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
		List<Message> messages = daoService.getApprovalMessages();
		if (!user.hasFonction(FonctionName.FCTN_GESTIONS_RESPONSABLES))
			messages = filterApprovalMessagesASupervisorCanApprove(messages, user);

		return messageManager.toUIMessages(messages);
	}

	private List<Message> filterApprovalMessagesASupervisorCanApprove(List<Message> msgs, User user) {
		Person p = daoService.getPersonByLogin(user.getId());
		List<Message> messages = new ArrayList<Message>();
		for (Message mess : msgs)
			if (mess.getSupervisors().contains(p))
				messages.add(mess);
		return messages;
	}

	/**
	 * Update the State of the message.
	 * @param uiMessage
	 */
	public void cancelMessage(final UIMessage uiMessage, User currentUser) {
		Message message = daoService.getMessageById(uiMessage.getId());
		sendSmsManager.sendMailMessageApprovedOrCanceled(message, MessageStatus.CANCEL, currentUser);
		message.setStateAsEnum(MessageStatus.CANCEL);
		daoService.updateMessage(message);

	}

	/**
	 * Treat the UI message.
	 * @param uiMessage
	 * @throws CreateMessageException.WebService
	 */
	public void approveMessage(final UIMessage uiMessage, User currentUser) throws CreateMessageException.WebService {
		Message message = daoService.getMessageById(uiMessage.getId());
		logger.debug("Message approved");
		sendSmsManager.sendMailMessageApprovedOrCanceled(message, MessageStatus.IN_PROGRESS, currentUser);
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
	// Setter of spring object messageManager
	//////////////////////////////////////////////////////////////
	/**
	 * @param messageManager
	 */
	public void setMessageManager(final MessageManager messageManager) {
		this.messageManager = messageManager;
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

}
