package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

/**
 * Business layer concerning smsu member.
 *
 */
public class MemberManager {

	@Autowired private LdapUtils ldapUtils;
	@Autowired private DaoService daoService;
	@Autowired private SmsuapiWS smsuapiWS;

	/**
	 * list of the ldap phone attributes.
	 */
	private List<String> phoneAttribute;

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
	 * title to used in the sms message.
	 */
	private String titleSmsValidation;

	/**
	 * expression used to match a mobile number.
	 */
	private String phoneNumberPattern;

	/**
	 * a prefix to remove.
	 */
	private String phoneNumberPrefixToRemove = "";

	private final Logger logger = new LoggerImpl(getClass());

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
	 */
	private void sendCodeBySMS(final String code, final String memberPhoneNumber) {
			logger.debug("Send the sms to " + memberPhoneNumber
					+ " containing the validation code " + code);

		String smsCode = titleSmsValidation + "\n" + code;
		smsuapiWS.sendSMS(null, null, memberPhoneNumber, accountValidation, smsCode);

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

	private void sendCodeBySMSAndAddPendingMember(final Member member) {
		String code = generateValidationCode();
		// send the code to this member
		sendCodeBySMS(code, member.getPhoneNumber());
		// store the new member in the database
		daoService.saveOrUpdatePendingMember(member.getLogin(), code);
	}

	private boolean savePhoneNumber(final Member member) throws LdapUserNotFoundException, LdapWriteException {
		String login = member.getLogin();
		String wanted = member.getPhoneNumber();
		String previous = ldapUtils.getUserPagerByUid(login);
		boolean hasChanged = previous == null || !previous.equals(wanted);

		if (hasChanged) {
			// save the new phone number
			logger.info("replacing " + login + " phone number in LDAP: new:" + wanted + " old:" + previous);
			ldapUtils.setUserPagerByUid(login, wanted.equals("") ? null : wanted);
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
		member.availablePhoneNumbers = getAvailablePhoneNumbers(userIdentifier);
		member.validCP = ldapUtils.getSpecificConditionsValidateByUid(userIdentifier);
		member.flagPending = activateValidation && daoService.isPendingMember(userIdentifier);
		member.validCG = member.flagPending || ldapUtils.isGeneralConditionValidateByUid(userIdentifier);
		member.phoneNumberValidationCode = null;
		return member;
	}

	/**
	 * try to find mobile phone numbers in various LDAP attributes
	 * @param userIdentifier
	 * @return the list of available phone numbers
	 * @throws LdapUserNotFoundException
	 */
	private List<String> getAvailablePhoneNumbers(final String userIdentifier) throws LdapUserNotFoundException {
		List<String> phoneNumbers = new ArrayList<String>();
		for (String attribute : this.phoneAttribute) {
			getAvailablePhoneNumbers(phoneNumbers, userIdentifier, attribute);
		}	
		return phoneNumbers;
	}

	private void getAvailablePhoneNumbers(List<String> phoneNumbers, String userIdentifier, String attribute)
			throws LdapUserNotFoundException {
		logger.debug("Search phone number with attribute " + attribute);
		List<String> values = ldapUtils.getLdapAttributesByUidAndName(userIdentifier, attribute);
		for (String value : values) {
			String nValue = normalizePhoneNumber(value);
			logger.debug("test pattern with value " + nValue);
			if (nValue.matches(this.phoneNumberPattern)) {
				phoneNumbers.add(nValue);
				logger.debug("phone number found from attribute " + attribute);
			}
		}
	}

	private String normalizePhoneNumber(String phoneNumber) {
		String s = phoneNumber.replaceAll(" ", "");
		if (!phoneNumberPrefixToRemove.equals("")) {
				logger.debug("phone Number Prefix To Remove " + phoneNumberPrefixToRemove);
			s = s.replaceAll(phoneNumberPrefixToRemove, "0");
		}
		return s;
	}

	/**
	 * save the member.
	 * @param member
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	public void saveOrUpdateMember(final Member member) throws LdapUserNotFoundException, LdapWriteException {
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
			final String dbCode = pendingMember.getValidationCode().toString();
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
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) {
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
	public void setPhoneAttributesAsString(final String attributes) {
		final List<String> list = new LinkedList<String>();
		for (String attribute : attributes.split(",")) {
			if (StringUtils.hasText(attribute)) {
				if (!list.contains(attribute)) {
					list.add(attribute);
				}
			}
		}
		setPhoneAttribute(list);
	}

	public void setPhoneAttribute(final List<String> phoneAttribute) {
		this.phoneAttribute = phoneAttribute;
	}

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
	public void setPhoneNumberPrefixToRemove(final String phoneNumberPrefixToRemove) {
		this.phoneNumberPrefixToRemove = phoneNumberPrefixToRemove;
	}

	@Required
	public void setValidationRoleName(final String validationRoleName) {
		this.validationRoleName = validationRoleName;
	}

}
