package org.esupportail.smsu.web.controllers;

import javax.ws.rs.Path;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.MemberManager;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/membership")
public class MembershipController {
	
    @Autowired private MemberManager memberManager;

	/**
	 * The pattern used to validate a phone number.
	 */
	private String phoneNumberPattern;
	
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * save action.
	 * @return "pending" or "ok"
	 */
	public String save(Member member) throws LdapUserNotFoundException, LdapWriteException {
		if (logger.isDebugEnabled()) {
			logger.debug("Save data of a member");
		}
		if (member.getValidCG()) {
			if (member.getPhoneNumber().equals(""))
					throw new InvalidParameterException("ADHESION.ERROR.PHONEREQUIRED");
			validatePhoneNumber(member.getPhoneNumber());
			
			if (memberManager.isPhoneNumberInBlackList(member.getPhoneNumber()))
					throw new InvalidParameterException("ADHESION.MESSAGE.PHONEINBLACKLIST");
		}
		// save datas into LDAP
		memberManager.saveOrUpdateMember(member);

		return member.getFlagPending() ? "pending" : "ok";
	}
	
	/**
	 * valid the member thank to its code.
	 * @return A String
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	public String validCode(Member member) throws LdapUserNotFoundException, LdapWriteException  {
		if (logger.isDebugEnabled()) {
			logger.debug("Valid the code");
		}
		// check if the code is correct
		// and accept definitely the user inscription if the code is correct
		final boolean valid = memberManager.valid(member);
		// create a message to give a feedback to the member
		return valid ? "ADHESION.MESSAGE.MEMBERCODEOK" : "ADHESION.MESSAGE.MEMBERCODEKO"; 
	}

	
	public void validatePhoneNumber(String phoneNumber) {
		if (!this.phoneNumberPattern.trim().equals("")) {
			if (!phoneNumber.matches(this.phoneNumberPattern)) {
				throw new InvalidParameterException("ADHESION.ERROR.INVALIDPHONENUMBER");
			}
		}
	}
	
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

}
