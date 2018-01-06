package org.esupportail.smsu.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.smsu.business.MemberManager;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/membership")
public class MembershipController {
	
    @Autowired private MemberManager memberManager;
    
    @Autowired private I18nService i18nService;
    
	protected static enum MembershipStatus {PENDING, OK};

	private final Logger logger = Logger.getLogger(getClass());

	@GET
	@Produces("application/json")
	public Member getCurrentMember(@Context HttpServletRequest request) throws LdapUserNotFoundException {
		return memberManager.getMember(request.getRemoteUser());
	}

	@GET
	@Produces("application/json")
	@Path("/isPhoneNumberInBlackList")
	public boolean isPhoneNumberInBlackList(@Context HttpServletRequest request) throws LdapUserNotFoundException, HttpException {
		Member member = memberManager.getMember(request.getRemoteUser());
		return memberManager.isPhoneNumberInBlackList(member.getPhoneNumber());
	}

	@POST
	@Produces("application/json")
	public MembershipStatus save(Member member, @Context HttpServletRequest request) throws LdapUserNotFoundException, LdapWriteException, HttpException, InsufficientQuotaException {
		if (logger.isDebugEnabled()) {
			logger.debug("Save data of a member");
		}
		if (member.login == null)
			member.login = request.getRemoteUser();
		if (!member.login.equals(request.getRemoteUser()))
			throw new InvalidParameterException("ADHESION.ERROR.INVALIDLOGIN");		
		
		if (StringUtils.isEmpty(member.getPhoneNumber())) {
			if (member.getValidCG()) throw new InvalidParameterException("ADHESION.ERROR.PHONEREQUIRED");
                        // normalize
                        member.setPhoneNumber(null);
		} else { 
			memberManager.validatePhoneNumber(member.getPhoneNumber());
		}

		// save datas into LDAP
		boolean pending = memberManager.saveOrUpdateMember(member);

		return pending ? MembershipStatus.PENDING : MembershipStatus.OK;
	}
	
	/**
	 * valid the member thank to its code.
	 * @return A String
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	@POST
	@Produces("application/json")
	@Path("/validCode")
	public Boolean validCode(Member member, @Context HttpServletRequest request) throws LdapUserNotFoundException, LdapWriteException  {
		if (logger.isDebugEnabled()) {
			logger.debug("Valid the code");
		}
		// check if the code is correct
		// and accept definitely the user inscription if the code is correct
		final boolean valid = memberManager.valid(member);
		
		if (!valid) {
			String i18nMessage = i18nService.getString("ADHESION.MESSAGE.MEMBERCODEKO");
			throw new InvalidParameterException(i18nMessage);
		}
		
		// create a message to give a feedback to the member
		return valid; 
	}

	@Deprecated
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
	}

}
