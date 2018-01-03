/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsu.web.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.business.ServiceManager;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.CreateMessageException.EmptyGroup;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.smsu.web.beans.UIService;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

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
	@Autowired private ServiceManager serviceManager;

	private Integer smsMaxSize;

	private final Logger logger = Logger.getLogger(getClass());

	
	@GET
	@Produces("application/json")
	public List<UIMessage> getMessages(
			@QueryParam("sender") String senderLogin,
			@QueryParam("maxResults") @DefaultValue("0" /* no limit */) int maxResults,
			@Context HttpServletRequest request) {
		senderLogin = allowedSender(request, senderLogin);
		Date beginDate = null;
		Date endDate = null;
		return messageManager.getMessages(null, null, null, null, senderLogin, beginDate, endDate, maxResults);
	}
		
	@GET
	@Produces("application/json")
	@Path("/{id:\\d+}")
	public UIMessage getMessage(
			@PathParam("id") int messageId,
			@Context HttpServletRequest request) {			
		return messageManager.getUIMessage(messageId, allowedSender(request));
	}
	
	@GET
	@Produces("application/json")
	@Path("/{id:\\d+}/statuses")
	public TrackInfos getMessageStatuses(
			@PathParam("id") int messageId,
			@Context HttpServletRequest request) throws HttpException, UnknownMessageIdException {			
		Message message = messageManager.getMessage(messageId, allowedSender(request));
		if (message == null) {
			throw new InvalidParameterException("unknow message " + messageId);
		}
		if (MessageStatus.SENT.name().equals(message.getStateAsEnum().name())) {
			return domainService.getMessageStatuses(messageId);
		}
		return null;
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
		
		serviceKeyValidation(msg.serviceKey, login);
		if(ServiceManager.SERVICE_SEND_FUNCTION_CG.equals(msg.serviceKey)) msg.serviceKey = null;

		recipientsValidation(msg, request, login);
		userGroupValidation(msg.senderGroup, login);
		contentValidation(msg.content);
		if (msg.mailToSend != null) {
			if (!request.isUserInRole("FCTN_SMS_AJOUT_MAIL"))
				throw new InvalidParameterException("user " + login + " is not allowed to send mails");
			mailsValidation(msg.mailToSend);
		}

		int messageId = sendSmsManager.sendMessage(msg, request);
		return messageManager.getUIMessage(messageId, null);
	}
	

	@RolesAllowed(
			   {"FCTN_SMS_ENVOI_ADH",
				"FCTN_SMS_ENVOI_GROUPES",
				"FCTN_SMS_ENVOI_NUM_TEL",
				"FCTN_SMS_ENVOI_LISTE_NUM_TEL",
				"FCTN_SMS_REQ_LDAP_ADH"})
	@POST
	@Produces("application/json")
	@Path("/nbRecipients")
	public int nbRecipients(UINewMessage msg, @Context HttpServletRequest request) {	
		String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		msg.login = login;
		
		serviceKeyValidation(msg.serviceKey, login);
		if (ServiceManager.SERVICE_SEND_FUNCTION_CG.equals(msg.serviceKey)) msg.serviceKey = null;
		
		try {
			return sendSmsManager.getRecipients(msg, msg.serviceKey).size();
		} catch (EmptyGroup e) {
			logger.debug("Empty group here - we return -1 : " + e.getMessage());
			return -1;
		}
	}

	@PermitAll
	@GET
	@Produces("application/json")
	@Path("/groupLeaves")
	public List<UserGroup> getUserGroupLeaves(@Context HttpServletRequest request) {
		return sendSmsManager.getUserGroupLeaves(request.getRemoteUser());
	}

	@GET
	@Produces("application/json")
	@Path("/senders")
	public Map<String,String> getUsersHavingSentASms(@Context HttpServletRequest request) {
		if (request.isUserInRole("FCTN_SUIVI_ENVOIS_ETABL")) {
			return domainService.getPersons();
		} else {
			return domainService.fakePersonsWithCurrentUser(request.getRemoteUser());
		}
	}

	private String allowedSender(HttpServletRequest request) {
		if (request.isUserInRole("FCTN_SUIVI_ENVOIS_ETABL")) {
			return null;
		} else {
			// only return the messages sent by this user
			return request.getRemoteUser();				
		} 
	}
	
	private String allowedSender(HttpServletRequest request, String wanted) {
		String allowedSender = allowedSender(request);
		
		if (allowedSender == null) {
			return wanted;
		} else if (wanted == null || wanted.equals(allowedSender)) {
			return allowedSender;
		} else {
			throw new InvalidParameterException(allowedSender + " is not allowed to view messages sent by " + wanted);
		}
	}
	
	private void serviceKeyValidation(String serviceKey, String login) {
		 List<UIService> services = serviceManager.getUIServicesSendFctn(login);
		 for(UIService service : services) {
			 if(service.key.equals(serviceKey)) 
				 return; // OK
		 }	 
		 throw new InvalidParameterException("user " + login + " has not the function to send to the service " + serviceKey);	 
	}

	private void recipientsValidation(UINewMessage msg, HttpServletRequest request, String login) {
		if (msg.recipientLogins != null)
			if (!request.isUserInRole("FCTN_SMS_ENVOI_ADH"))
				throw new InvalidParameterException("user " + login + " is not allowed to send SMS");
		if (msg.recipientPhoneNumbers != null)
			if (!request.isUserInRole("FCTN_SMS_ENVOI_NUM_TEL"))
				throw new InvalidParameterException("user " + login + " is not allowed to send to phone numbers");
		if (!StringUtils.isEmpty(msg.recipientGroup))
			if (!request.isUserInRole("FCTN_SMS_ENVOI_GROUPES"))
				throw new InvalidParameterException("user " + login + " is not allowed to send SMS to groups");
	}

	private void userGroupValidation(String userGroup, String login) {
		for (UserGroup g : sendSmsManager.getUserGroupLeaves(login))
 			if (g.id.equals(userGroup)) return; // OK

		// not found
		throw new InvalidParameterException("user " + login + " is not in group " + userGroup);
	}

	/**
	 * Content validation.
	 */
	public void contentValidation(final String content) {
		Integer contentSize = content == null ? 0: content.length();
		logger.debug("taille de message : " + contentSize.toString());
		logger.debug("message : " + content);
		if (contentSize == 0) {
			throw new InvalidParameterException("SENDSMS.MESSAGE.EMPTYMESSAGE");
		} else if (contentSize > smsMaxSize) {
			throw new InvalidParameterException("SENDSMS.MESSAGE.MESSAGETOOLONG");
		}		
	}

	public void mailsValidation(MailToSend mailToSend) {
		List<String> mails = mailToSend.getMailOtherRecipients();
		if (mails.isEmpty() && !mailToSend.getIsMailToRecipients()) {
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

