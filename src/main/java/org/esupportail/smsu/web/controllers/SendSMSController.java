package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;


@RolesAllowed(
   {"FCTN_SMS_ENVOI_ADH",
	"FCTN_SMS_ENVOI_GROUPES",
	"FCTN_SMS_ENVOI_NUM_TEL",
	"FCTN_SMS_ENVOI_LISTE_NUM_TEL",
	"FCTN_SMS_REQ_LDAP_ADH"})
@Path("/send")
public class SendSMSController {

	@Autowired private SendSmsManager sendSmsManager;
	@Autowired private SmtpServiceUtils smtpServiceUtils;
	@Autowired private MessageManager messageManager;

	private Integer smsMaxSize;

	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * @return perform the sending action.
	 * @throws CreateMessageException 
	 */
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

	public List<UserGroup> getUserGroupLeaves(String uid) {
		return sendSmsManager.getUserGroupLeaves(uid);
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
	
	@Required
	public void setSmsMaxSize(final Integer smsMaxSize) {
		this.smsMaxSize = smsMaxSize;
	}

}
