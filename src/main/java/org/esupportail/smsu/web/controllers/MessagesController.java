/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A bean to manage user preferences.
 */
@Path("/messages")
@RolesAllowed({"FCTN_SUIVI_ENVOIS_ETABL","FCTN_SUIVI_ENVOIS_UTIL"})
public class MessagesController {

	@Autowired private DomainService domainService;
	@Autowired private MessageManager messageManager;
	
	@GET
	@Produces("application/json")
	public List<UIMessage> getMessages() {
		return messageManager.getMessages(null, null, null, null, null, null, null);
	}
		
	@GET
	@Path("/{id:\\d+}")
	@Produces("application/json")
	public UIMessage getMessage(@PathParam("id") int messageId) {
		return messageManager.getUIMessage(messageId);
	}
	
	@GET
	@Path("/{id:\\d+}/statuses")
	@Produces("application/json")
	public TrackInfos getMessageStatuses(@PathParam("id") int messageId) {
		Message message = messageManager.getMessage(messageId);
		if (MessageStatus.SENT.name().equals(message.getStateAsEnum().name())) {
			return domainService.getMessageStatuses(messageId);
		}
		return null;
	}

	/* return a list of phones / logins */
	@GET
	@Path("/{id:\\d+}/recipients")
	@Produces("application/json")
	public List<String> getRecipients(@PathParam("id") int messageId) {
		return messageManager.getUIRecipients(messageId);
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


}

