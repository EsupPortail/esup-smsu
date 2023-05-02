package org.esupportail.smsu.web.controllers;

import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.ApprovalManager;
import org.esupportail.smsu.configuration.SmsuApplication;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = SmsuApplication.REST_ROOT_URI + "/approvals")
@RolesAllowed("APPROBATION_ENVOI")
public class ApprovalController {

	private final Logger logger = Logger.getLogger(getClass());
	
	@Inject private DomainService domainService;
	@Inject private ApprovalManager approvalManager;

    @GetMapping
    public List<UIMessage> getApprovalUIMessages(HttpServletRequest request) {
    	return approvalManager.getApprovalUIMessages(request);
    }
    
	private static final Collection<MessageStatus> CANCEL_OR_IN_PROGRESS = List.of(MessageStatus.CANCEL, MessageStatus.IN_PROGRESS);

	@PutMapping("/{id:\\d+}")
	public void modify(@PathVariable("id") int id, HttpServletRequest request, @RequestBody UIMessage msg) throws CreateMessageException {
		String statusStr = msg.getStateMessage();
		MessageStatus status = EnumUtils.getEnum(MessageStatus.class, statusStr);
		
		if((status == null) || (!CANCEL_OR_IN_PROGRESS.contains(status))) {
			throw new InvalidParameterException("unknown status " + statusStr);
		}
		
		User currentUser = domainService.getUser(request);
		logger.info(currentUser + " " + (MessageStatus.CANCEL.equals(status) ? "cancel" : "approve") + " message " + id);
		approvalManager.cancelOrApproveMessage(id, currentUser, status, request);
	}
}
