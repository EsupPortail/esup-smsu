package org.esupportail.smsu.business;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.PendingMember;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.esupportail.smsu.services.client.SmsuapiWS;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.utils.HttpException;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Required;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * Business layer concerning smsu member.
 *
 */
public class MemberManager {

	@Inject private LdapUtils ldapUtils;
	@Inject private DaoService daoService;
	@Inject private SmsuapiWS smsuapiWS;

	/**
	 * flag that indicates if it is necessary to validate the phone number.
	 */
	private Boolean activateValidation;

	/**
	 * length maximum for the validation code.
	 */
	private Integer maxNumberCodeValidation;

	/**
	 * account to used for send the validation sms.
	 */
	private String accountValidation;

	/**
	 * the role name (usually a empty role)
	 */
	private String validationRoleName;

    /**
	 * The pattern used to validate a phone number.
	 */
	private String phoneNumberPattern;	

	/**
	 * title to used in the sms message.
	 */
	private String titleSmsValidation;

	private boolean displayFrenchPhoneNumber = true;
	private boolean storeFrenchPhoneNumber = true;
    
	private final Logger logger = Logger.getLogger(getClass());
	
	private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

	///////////////////////////////////////
	//  private method
	//////////////////////////////////////
	/**
	 * generate a random validation code.
	 * @return
	 */
	private String generateValidationCode() {
		if (logger.isDebugEnabled()) {
			logger.debug("Generate the validation code");
		}
		Random random = new Random();
		final Integer code = random.nextInt(maxNumberCodeValidation);
		final String result = code.toString();
		return result;
	}

	/**
	 * send a code by sms to the given phone number.
	 * @param code
	 * @param memberPhoneNumber
	 * @throws InsufficientQuotaException 
	 * @throws HttpException 
	 */
	private void sendCodeBySMS(final String code, final String memberPhoneNumber) throws HttpException, InsufficientQuotaException {
			logger.debug("Send the sms to " + memberPhoneNumber
					+ " containing the validation code " + code);

		String smsCode = titleSmsValidation + "\n" + code;
		smsuapiWS.sendSMS(null, null, Collections.singleton(memberPhoneNumber), accountValidation, smsCode);

		// the sms is sent, the group is consumption is updated.
		CustomizedGroup cg = getOrCreateValidationCustomizedGroup();
		cg.setConsumedSms(cg.getConsumedSms() + 1);
		daoService.updateCustomizedGroup(cg);

	}

	private CustomizedGroup getOrCreateValidationCustomizedGroup() {
		CustomizedGroup cg = daoService.getCustomizedGroupByLabel(accountValidation);
		if (cg == null) {
			cg = newValidationCustomizedGroup();
			daoService.addCustomizedGroup(cg);
		}
		return cg;
	}

	private CustomizedGroup newValidationCustomizedGroup() {
		logger.debug("Create the customized group corresponding to : " + accountValidation );
		CustomizedGroup cg = new CustomizedGroup();
		cg.setLabel(accountValidation);
		cg.setQuotaOrder(Long.parseLong("1"));
		cg.setQuotaSms(Long.parseLong("1")); // this value is actually not used, all validation sms are sent.
		cg.setConsumedSms(Long.parseLong("0"));
		cg.setAccount(getOrCreateValidationAccount());
		cg.setRole(getOrCreateValidationRole());
		return cg;
	}

	private Role getOrCreateValidationRole() {
		Role role = daoService.getRoleByName(validationRoleName);
		if (role == null) {
			logger.debug("Create the role : " + validationRoleName);
			role = new Role();
			role.setName(validationRoleName);
			daoService.saveRole(role);
		}
		return role;
	}

	private Account getOrCreateValidationAccount() {
		Account acc = daoService.getAccountByLabel(accountValidation);
		if (acc == null) {
			acc = new Account();
			acc.setLabel(accountValidation);
			daoService.saveAccount(acc);
		}
		return acc;
	}

	private void sendCodeBySMSAndAddPendingMember(final Member member) throws HttpException, InsufficientQuotaException {
		String code = generateValidationCode();
		// send the code to this member
		sendCodeBySMS(code, member.getPhoneNumber());
		// store the new member in the database
		daoService.saveOrUpdatePendingMember(member.getLogin(), code);
	}

	private boolean savePhoneNumber(final Member member) throws LdapUserNotFoundException, LdapWriteException, NumberParseException {
		String login = member.getLogin();
		String wanted = member.getPhoneNumber();
		if (StringUtils.isBlank(wanted)) {
			wanted = null;
		} else {
			validatePhoneNumber(wanted);
			wanted = storeFrenchPhoneNumber ? toFrenchPhoneNumber(wanted) : fromFrenchPhoneNumber(wanted);
		}
		String previous = ldapUtils.getUserPagerByUid(login);
		boolean hasChanged = !Objects.equals(previous, wanted);

		if (hasChanged) {
			// save the new phone number
			logger.info("replacing " + login + " phone number in LDAP: new:" + wanted + " old:" + previous);
			ldapUtils.setUserPagerByUid(login, wanted);
		} else {
			logger.info("keeping " + login + " phone number unchanged in LDAP (" + wanted + ")");
		}
		return hasChanged;
	}

	///////////////////////////////////////
	// Public method
	//////////////////////////////////////
	/**
	 * retrieve a member based on its identifier.
	 * @param userIdentifier
	 * @return
	 * @throws LdapUserNotFoundException 
	 */
	public Member getMember(final String userIdentifier) throws LdapUserNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get the member " + userIdentifier);
		}
		Member member = new Member();
		member.login = userIdentifier;
		member.firstName = ldapUtils.getUserFirstNameByUid(userIdentifier);
		member.lastName = ldapUtils.getUserLastNameByUid(userIdentifier);
		member.phoneNumber = ldapUtils.getUserPagerByUid(userIdentifier);
		if (displayFrenchPhoneNumber) {
			try {
				member.phoneNumber = toFrenchPhoneNumber(member.phoneNumber);
			} catch (NumberParseException e) {
				logger.error(e);
			}
		}
		member.validCP = ldapUtils.getSpecificConditionsValidateByUid(userIdentifier);
		member.flagPending = activateValidation && daoService.isPendingMember(userIdentifier);
		member.validCG = member.flagPending || ldapUtils.isGeneralConditionValidateByUid(userIdentifier);
		member.phoneNumberValidationCode = null;
		return member;
	}

	/**
	 * @return example : 0601020304 (or +34601020304 for non-French phone numbers)
	 */
	public String toFrenchPhoneNumber(String phoneNumber) throws NumberParseException {
	    if (phoneNumber == null) return null;
	    PhoneNumber phoneNumberObj = parsePhoneNumber(phoneNumber);
	    
	    if(phoneNumberObj.getCountryCode() == 33) {
	    	return "0" + phoneNumberObj.getNationalNumber();
	    }
	    
	    return phoneNumberUtil.format(phoneNumberObj, PhoneNumberFormat.E164);
	}

	/**
	 * 
	 * @return example : +33 6 01 02 03 04
	 */
	public String fromFrenchPhoneNumber(String phoneNumber) throws NumberParseException {
	    if (phoneNumber == null) return null;
	    PhoneNumber phoneNumberObj = parsePhoneNumber(phoneNumber);
	    return phoneNumberUtil.format(phoneNumberObj, PhoneNumberFormat.INTERNATIONAL);
	}
	
	/**
	 * @throws InvalidParameterException if phoneNumber doesn't match phoneNumberPattern, or if phoneNumber isn't possible mobile number
	 */
	public void validatePhoneNumber(String phoneNumber) throws InvalidParameterException {
		if (StringUtils.isEmpty(this.phoneNumberPattern) || phoneNumber.matches(this.phoneNumberPattern)) {
			try {
				PhoneNumber phoneNumberObj = parsePhoneNumber(phoneNumber);
				if (phoneNumberUtil.isPossibleNumberForType(phoneNumberObj, PhoneNumberType.MOBILE)) {
					return;
				}
			} catch (NumberParseException e) {
			}
		}
		throw new InvalidParameterException("ADHESION.ERROR.INVALIDPHONENUMBER");
	}

	private PhoneNumber parsePhoneNumber(String phoneNumber) throws NumberParseException {
		return phoneNumberUtil.parse(phoneNumber, "FR");
	}

	/**
	 * save the member.
	 * @param member
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 * @throws InsufficientQuotaException 
	 * @throws HttpException 
	 * @throws NumberParseException 
	 */
	public boolean saveOrUpdateMember(final Member member) throws LdapUserNotFoundException, LdapWriteException, HttpException, InsufficientQuotaException, NumberParseException {
		logger.debug("Save a member ");
		boolean numberPhoneChanged = savePhoneNumber(member);

		String memberLogin = member.getLogin();
		List<String> memberValidCP = member.getValidCP();
		boolean validCG = member.getValidCG();
		if (validCG) {
			if (numberPhoneChanged && activateValidation) {
				sendCodeBySMSAndAddPendingMember(member);
				// do not store the validated CG for now, it will be done once validated
				validCG = false;
			}
		} else {
			// also remove subscriptions to services
			memberValidCP = null;
			if (activateValidation && daoService.isPendingMember(memberLogin)) {
				// user asks to unsubscribe after asking to subscribe
				daoService.deletePendingMember(memberLogin);
			}
		}	
		// save in LDAP
		ldapUtils.setUserTermsOfUse(memberLogin, validCG, memberValidCP);
		
		return daoService.isPendingMember(memberLogin); 
	}

	/**
	 * test if the the code entered by the pending member is correct.
	 * If it is the case, accept definitely this member in the SMSU.
	 * i.e. remove the current member from the database
	 *      add the general condition in the LDAP
	 * @param member
	 * @return
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	public boolean valid(final Member member) throws LdapUserNotFoundException, LdapWriteException {
		final String login = member.getLogin();

		// retrieve the corresponding pending member
		PendingMember pendingMember = daoService.getPendingMember(login);
		// check if exists
		if (pendingMember == null) return false;
		
			// check if the code is correct
			final String dbCode = pendingMember.getValidationCode();
			final String code = member.getPhoneNumberValidationCode();
			if (dbCode.equals(code)) {
				// add smsu general condition for this member in the LDAP 
				ldapUtils.addGeneralConditionByUid(login);
				daoService.deletePendingMember(login);
				return true;
			} else {
				return false;
			}
	}

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 * @throws HttpException 
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) throws HttpException {
			logger.debug("Request in memberManager : " + phoneNumber);
		Boolean retVal = smsuapiWS.isPhoneNumberInBlackList(phoneNumber); 
			logger.debug("Response return in memberManager for : " 
					+ phoneNumber + " is : " + retVal);
		return retVal;
	}

	///////////////////////////////////////
	// setter for spring objets
	//////////////////////////////////////
	/**
	 * @param attributes 
	 */

	@Required
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

	@Required
	public void setActivateValidation(final Boolean activateValidation) {
		this.activateValidation = activateValidation;
	}

	@Required
	public void setMaxNumberCodeValidation(final Integer maxNumberCodeValidation) {
		this.maxNumberCodeValidation = maxNumberCodeValidation;
	}

	@Required
	public void setAccountValidation(final String accountValidation) {
		this.accountValidation = accountValidation;
	}

	@Required
	public void setTitleSmsValidation(final String titleSmsValidation) {
		this.titleSmsValidation = titleSmsValidation;
	}
	
	@Required
	public void setValidationRoleName(final String validationRoleName) {
		this.validationRoleName = validationRoleName;
	}

	public void setStoreFrenchPhoneNumber(final Boolean storeFrenchPhoneNumber) {
		this.storeFrenchPhoneNumber = storeFrenchPhoneNumber;
	}
	
	public void setDisplayFrenchPhoneNumber(final Boolean displayFrenchPhoneNumber) {
		this.displayFrenchPhoneNumber = displayFrenchPhoneNumber;
	}
}
