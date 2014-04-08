package org.esupportail.smsu.business;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.controllers.InvalidParameterException;
import org.springframework.beans.factory.annotation.Autowired;

public class ApprovalManager {
	
	@Autowired private DaoService daoService;
	@Autowired private MessageManager messageManager;
	@Autowired private SendSmsManager sendSmsManager;

	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());

	public List<UIMessage> getApprovalUIMessages(HttpServletRequest request) {
		List<Message> messages = daoService.getApprovalMessages();
		if (!request.isUserInRole("FCTN_GESTIONS_RESPONSABLES"))
			messages = filterApprovalMessagesASupervisorCanApprove(messages, request.getRemoteUser());
		return messageManager.convertToUI(messages);
	}

	public void cancelOrApproveMessage(int messageId, User currentUser, MessageStatus newStatus, HttpServletRequest request) throws CreateMessageException {
		Message message = daoService.getMessageById(messageId);
		if (message == null)
			throw new InvalidParameterException("unknown message " + messageId);
		if (!message.getStateAsEnum().equals(MessageStatus.WAITING_FOR_APPROVAL))
			throw new InvalidParameterException("message is not waiting for approval");
		checkCanApprove(message, currentUser);

		sendSmsManager.sendMailMessageApprovedOrCanceled(message, newStatus, currentUser, request);
		message.setStateAsEnum(newStatus);
		if (newStatus.equals(MessageStatus.CANCEL)) {
			daoService.updateMessage(message);
		} else {
			sendSmsManager.treatMessage(message, request);
		}
	}

	private List<Message> filterApprovalMessagesASupervisorCanApprove(List<Message> msgs, String user) {
		Person p = daoService.getPersonByLogin(user);
		List<Message> messages = new ArrayList<Message>();
		for (Message mess : msgs)
			if (mess.getSupervisors().contains(p))
				messages.add(mess);
		return messages;
	}
	
	private void checkCanApprove(Message msg, User user) {
		if (user.rights.contains("FCTN_GESTIONS_RESPONSABLES"))
			return;
		
		Person p = daoService.getPersonByLogin(user.getId());
		if (!msg.getSupervisors().contains(p))
			throw new InvalidParameterException("user " + user.getId() + " is not allowed to approve this message");
	}	

}
