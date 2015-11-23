package org.esupportail.smsu.web.ws;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.MemberManager;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.SmsuForbiddenException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.smsu.web.controllers.MessagesController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

@Path("/")
public class WsController {

	private final Logger logger = Logger.getLogger(getClass());

	@Autowired private MessagesController messagesController;
	@Autowired private MessageManager messageManager;
	@Autowired private SendSmsManager sendSmsManager;
    @Autowired private MemberManager memberManager;
    

	private List<String> authorizedClientNames;

	/**
	 * curl \
	 * -i \
	 * -X POST \
	 * -H "Content-Type: application/json" \
	 * -d '{"login":"loginTestSmsu","senderGroup":"loginTestSmsu","content":"yop","recipientLogins":["loginTestSmsu"],"recipientPhoneNumbers":null}' \
	 * http://localhost:8080/ws/sms
	 */
	@POST
	@Path("/sms")
	@Produces("application/json")
	public UIMessage sendSMSAction(UINewMessage msg, @Context HttpServletRequest request) throws CreateMessageException {		
		if(checkClient(request)) {
			messagesController.contentValidation(msg.content);
			if (msg.mailToSend != null) {
				messagesController.mailsValidation(msg.mailToSend);
			}

			int messageId = sendSmsManager.sendMessage(msg, request);
			return messageManager.getUIMessage(messageId, null);
		} else {
			throw new SmsuForbiddenException("You can't call this WS from this remote address");
		}
	}



	/**
	 * curl \
	 * -i \
	 * -X GET \
	 * -H "Content-Type: application/json" \
	 * http://localhost:8080/ws/sms/member/loginTestSmsu
	 */
	@GET
	@Path("/member/{login}")
	@Produces("application/json")
	public Member getMember(@PathParam("login") String login, @Context HttpServletRequest request) {		
		if(checkClient(request)) {
			Member member = null;
			try {
				member = memberManager.getMember(login);
			} catch (LdapUserNotFoundException e) {
				logger.warn("getValidCgMember on " + login + " failed - user not found", e);
			}
			return member;
		} else {
			throw new SmsuForbiddenException("You can't call this WS from this remote address");
		}
	}
	
	/**                                                                                                                                                                                                                                   
	 * Check if the client is authorized.                                                                                                                                                                                                                                                                                                                                                                                                           
	 */
	protected boolean checkClient(HttpServletRequest request) {
		InetAddress client = getClient(request);
		if (client == null) {
			throw new RuntimeException("could not resolve the client of the web service");
		}
		for (String authorizedClientName : authorizedClientNames) {
			try {
				if (client.equals(InetAddress.getByName(authorizedClientName))) {
					return true;
				}
			} catch (UnknownHostException e) {
				logger.warn("could not resolve authorized client [" + authorizedClientName + "]", e);
			}
		}
		logger.warn("client [" + client.getHostName() + "] is not authorized");
		return false;
	}

	/**                                                                                                                                                                                                                                   
	 * @return the client.                                                                                                                                                                                                                
	 */
	protected InetAddress getClient(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		try {
			return InetAddress.getByName(remoteAddr);
		} catch (UnknownHostException e) {
			logger.info("could not resolve remote address : " + remoteAddr, e);
			return null;
		}
	}


	@Required
	public void setAuthorizedClientNames(String authorizedClientNamesWithComa) {
		authorizedClientNamesWithComa = authorizedClientNamesWithComa.replaceAll(" ", "");
		authorizedClientNames = Arrays.asList(StringUtils.split(authorizedClientNamesWithComa, ","));
		logger.info("WS authorizedClientNames : " + authorizedClientNames);
	}

}
