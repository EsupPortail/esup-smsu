package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.esupportail.smsu.business.ApprovalManager;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.apache.log4j.Logger;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/approvals")
@RolesAllowed("APPROBATION_ENVOI")
public class ApprovalController {

	private final Logger logger = Logger.getLogger(getClass());
	
	@Inject private DomainService domainService;
	@Inject private ApprovalManager approvalManager;

    @RequestMapping(method = RequestMethod.GET)
    public List<UIMessage> getApprovalUIMessages(HttpServletRequest request) {
    	return approvalManager.getApprovalUIMessages(request);
    }
    
	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}")
	public void modify(@PathVariable("id") int id, HttpServletRequest request, @RequestBody UIMessage msg) throws CreateMessageException {
		String status = msg.stateMessage;
		if (status.equals("CANCEL") && status.equals("IN_PROGRESS")) {
			throw new InvalidParameterException("unknown status " + status);
		}
		User currentUser = domainService.getUser(request);
		logger.info("" + currentUser + " " + (status.equals("CANCEL") ? "cancel" : "approve") + " message " + id);
		approvalManager.cancelOrApproveMessage(id, currentUser, MessageStatus.valueOf(status), request);
	}

}
