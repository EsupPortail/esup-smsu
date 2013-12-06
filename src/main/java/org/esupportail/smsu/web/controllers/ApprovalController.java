package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.esupportail.smsu.business.ApprovalManager;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/approvals")
@RolesAllowed("FCTN_GESTIONS_RESPONSABLES")
public class ApprovalController {

	private final Logger logger = new LoggerImpl(getClass());
	
    @Autowired private DomainService domainService;
    @Autowired private ApprovalManager approvalManager;

    @GET
	@Produces("application/json")
    public List<UIMessage> getApprovalUIMessages(@Context HttpServletRequest request) {
    	return approvalManager.getApprovalUIMessages(request);
    }
    
	@PUT
	@Path("/{id:\\d+}")
	public void modify(@PathParam("id") int id, @Context HttpServletRequest request, MessageStatus status) throws CreateMessageException {
		User currentUser = domainService.getUser(request.getRemoteUser());
		if (!MessageStatus.CANCEL.equals(status) && !MessageStatus.IN_PROGRESS.equals(status)) {
			throw new InvalidParameterException("unknown status " + status);
		}
		logger.info("" + currentUser + " " + (MessageStatus.CANCEL.equals(status) ? "cancel" : "approve") + " message " + id);
		approvalManager.cancelOrApproveMessage(id, currentUser, status);
	}

	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}

	public void setApprovalManager(ApprovalManager approvalManager) {
		this.approvalManager = approvalManager;
	}
}
