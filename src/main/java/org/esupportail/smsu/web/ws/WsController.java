package org.esupportail.smsu.web.ws;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.MemberManager;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.business.ServiceManager;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.exceptions.SmsuForbiddenException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.smsu.web.beans.UIService;
import org.esupportail.smsu.web.controllers.InvalidParameterException;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

@RequestMapping(value = "/")
public class WsController {

	private final Logger logger = Logger.getLogger(getClass());
	
	protected static enum MembershipStatus {PENDING, OK};

	@Autowired private MessageManager messageManager;
	@Autowired private SendSmsManager sendSmsManager;
    @Autowired private MemberManager memberManager;
	@Autowired private ServiceManager serviceManager;	
    

	private List<String> authorizedClientNames;

	/**
	 * curl \
	 * -i \
	 * -X POST \
	 * -H "Content-Type: application/json" \
	 * -d '{"login":"loginTestSmsu","senderGroup":"loginTestSmsu","content":"yop","recipientLogins":["loginTestSmsu"],"recipientPhoneNumbers":null}' \
	 * http://localhost:8080/ws/sms
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/sms")
	@ResponseBody
	public UIMessage sendSMSAction(UINewMessage msg, HttpServletRequest request) throws CreateMessageException {		
		if(checkClient(request)) {
			sendSmsManager.contentValidation(msg.content);
			if (msg.mailToSend != null) {
				sendSmsManager.mailsValidation(msg.mailToSend);
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
	 * http://localhost:8080/ws/member/loginTestSmsu
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/member/{login}")
	@ResponseBody
	public Member getMember(@PathVariable("login") String login, HttpServletRequest request) {		
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
	 * curl \
	 * -i \
	 * -X POST \
	 * -H "Content-Type: application/json" \
	 * -d '{"login": "loginTestSmsu", "phoneNumber": "0612345678", "validCG": true, "validCP": ["cas"]}}' \
	 * http://localhost:8080/ws/member
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/member")
	@ResponseBody
	public MembershipStatus saveMember(Member member, HttpServletRequest request) throws LdapUserNotFoundException, LdapWriteException, HttpException, InsufficientQuotaException {
		if(checkClient(request)) {
			logger.debug("Save data of a member");		
			if (StringUtils.isEmpty(member.getPhoneNumber())) {
				if (member.getValidCG()) throw new InvalidParameterException("ADHESION.ERROR.PHONEREQUIRED");
			} else { 
				memberManager.validatePhoneNumber(member.getPhoneNumber());
			}
	
			// save datas into LDAP
			boolean pending = memberManager.saveOrUpdateMember(member);
	
			return pending ? MembershipStatus.PENDING : MembershipStatus.OK;
		} else {
			throw new SmsuForbiddenException("You can't call this WS from this remote address");
		}
	}
	
	
	/**
	 * curl \
	 * -i \
	 * -X POST \
	 * -H "Content-Type: application/json" \
	 * -d '{"login": "loginTestSmsu", "phoneNumberValidationCode" : "12345"}' \
	 * http://localhost:8080/ws/validCode
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/validCode")
	@ResponseBody
	public Boolean validCode(Member member, HttpServletRequest request) throws LdapUserNotFoundException, LdapWriteException, HttpException, InsufficientQuotaException {
		if(checkClient(request)) {
			logger.debug("Valid code of a member");		
			
			// check if the code is correct
			// and accept definitely the user inscription if the code is correct
			final boolean valid = memberManager.valid(member);
			
			return valid;		
		} else {
			throw new SmsuForbiddenException("You can't call this WS from this remote address");
		}
	}
	
	/**
	 * curl \
	 * -i \
	 * -X GET \
	 * -H "Content-Type: application/json" \
	 * http://localhost:8080/ws/services
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/services")
	@ResponseBody
	public List<UIService> getUIServices(HttpServletRequest request) {
		if(checkClient(request)) {
			return serviceManager.getAllUIServices();
		} else {
			throw new SmsuForbiddenException("You can't call this WS from this remote address");
		}
	}
	
	/**
	 * curl \
	 * -i \
	 * -X GET \
	 * -H "Content-Type: application/json" \
	 * http://localhost:8080/ws/sms/member/loginTestSmsu/adhServicesAvailable
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/member/{login}/adhServicesAvailable")
	@ResponseBody
	public List<UIService> getUIServicesAdh(@PathVariable("login") String login, HttpServletRequest request) {
		if(checkClient(request)) {
			return serviceManager.getUIServicesAdhFctn(login);
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
