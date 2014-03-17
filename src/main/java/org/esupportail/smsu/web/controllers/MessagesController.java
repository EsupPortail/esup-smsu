/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * A bean to manage user preferences.
 */
@Path("/messages")
@RolesAllowed({"FCTN_SUIVI_ENVOIS_ETABL","FCTN_SUIVI_ENVOIS_UTIL"})
public class MessagesController {

	@Autowired private DomainService domainService;
	@Autowired private MessageManager messageManager;
	@Autowired private SendSmsManager sendSmsManager;
	@Autowired private SmtpServiceUtils smtpServiceUtils;

	private Integer smsMaxSize;

	private final Logger logger = new LoggerImpl(getClass());

	
	@GET
	@Produces("application/json")
	public List<UIMessage> getMessages() {
		return messageManager.getMessages(null, null, null, null, null, null, null);
	}
		
	@GET
	@Produces("application/json")
	@Path("/{id:\\d+}")
	public UIMessage getMessage(@PathParam("id") int messageId) {
		return messageManager.getUIMessage(messageId);
	}
	
	@GET
	@Produces("application/json")
	@Path("/{id:\\d+}/statuses")
	public TrackInfos getMessageStatuses(@PathParam("id") int messageId) {
		Message message = messageManager.getMessage(messageId);
		if (MessageStatus.SENT.name().equals(message.getStateAsEnum().name())) {
			return domainService.getMessageStatuses(messageId);
		}
		return null;
	}

	/* return a list of phones / logins */
	@GET
	@Produces("application/json")
	@Path("/{id:\\d+}/recipients")
	public List<String> getRecipients(@PathParam("id") int messageId) {
		return messageManager.getUIRecipients(messageId);
	}

	@RolesAllowed(
			   {"FCTN_SMS_ENVOI_ADH",
				"FCTN_SMS_ENVOI_GROUPES",
				"FCTN_SMS_ENVOI_NUM_TEL",
				"FCTN_SMS_ENVOI_LISTE_NUM_TEL",
				"FCTN_SMS_REQ_LDAP_ADH"})
	@POST
	@Produces("application/json")
	public UIMessage sendSMSAction(UINewMessage msg, @Context HttpServletRequest request) throws CreateMessageException {		
		String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		msg.login = login;

		userGroupValidation(msg.senderGroup, login);
		contentValidation(msg.content);
		if (msg.mailToSend != null)
			mailsValidation(msg.mailToSend.getMailOtherRecipientsList());

		int messageId = sendSmsManager.sendMessage(msg);
		return messageManager.getUIMessage(messageId);
	}

	@GET
	@Produces("application/json")
	@Path("/groupLeaves")
	public List<UserGroup> getUserGroupLeaves(@Context HttpServletRequest request) {
		return sendSmsManager.getUserGroupLeaves(request.getRemoteUser());
	}

	private void userGroupValidation(String userGroup, String login) {
		//TODO
		//sendSmsManager.getUserGroupLeaves(login).contains(userGroup))
		//	throw new InvalidParameterException("invalid userGroup " + userGroup + " for user " + login);
	}

	/**
	 * Content validation.
	 */
	private void contentValidation(final String content) {
		Integer contentSize = content == null ? 0: content.length();
		logger.debug("taille de message : " + contentSize.toString());
		logger.debug("message : " + content);
		if (contentSize == 0) {
			throw new InvalidParameterException("SENDSMS.MESSAGE.EMPTYMESSAGE");
		} else if (contentSize > smsMaxSize) {
			throw new InvalidParameterException("SENDSMS.MESSAGE.MESSAGETOOLONG");
		}		
	}

	private void mailsValidation(String[] mails) {
		if (mails.length == 0) {
			throw new InvalidParameterException("SENDSMS.MESSAGE.RECIPIENTSMANDATORY");
		}
		for (String mail : mails) {
			if (logger.isDebugEnabled()) logger.debug("mail validateOthersMails is :" + mail);
			
			if (!smtpServiceUtils.checkInternetAdresses(mail)) {
				logger.info("validateOthersMails: " + mail + " is invalid");
				throw new InvalidParameterException("SERVICE.FORMATMAIL.WRONG:" + mail);
			}
		}
	}

	//////////////////////////////////////////////////////////////
	// Validation method
	//////////////////////////////////////////////////////////////
	@SuppressWarnings("unused") // TODO not useful ?
	private void validateDates(Date beginDate, Date endDate) {
		if (beginDate != null && endDate != null) {
			if (beginDate.getTime() > endDate.getTime()) {
				throw new InvalidParameterException("SEND.SEARCH.DATES.ERROR");
			}
		}

	}
	
	@Required
	public void setSmsMaxSize(final Integer smsMaxSize) {
		this.smsMaxSize = smsMaxSize;
	}

}

