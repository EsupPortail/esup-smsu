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
import org.esupportail.smsu.services.client.NotificationPhoneNumberInBlackListClient;
import org.esupportail.smsu.services.client.SendSmsClient;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.springframework.util.StringUtils;

/**
 * Business layer concerning smsu member.
 *
 */
public class MemberManager {

	/**
	 * {@link LdapUtils}.
	 */
	private LdapUtils ldapUtils;

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link SendSmsClient}.
	 */
	private SendSmsClient sendSmsClient;

	/**
	 * {@link NotificationPhoneNumberInBlackListClient}.
	 */
	private NotificationPhoneNumberInBlackListClient notificationPhoneNumberInBlackListClient;

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

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	///////////////////////////////////////
	//  constructor
	//////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public MemberManager() {

	}

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
		if (logger.isDebugEnabled()) {
			logger.debug("Send the sms to " + memberPhoneNumber
					+ " containing the validation code " + code);
		}


		String smsCode = titleSmsValidation + "\n" + code;
		sendSmsClient.sendSMS(null, null, null, null, memberPhoneNumber,
				accountValidation , smsCode);

		CustomizedGroup cg = daoService.getCustomizedGroupByLabel(accountValidation);
		// the sms is sent, the goup is consumption is updated.
		if (cg == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Create the customized group corresponding to : " + accountValidation );
			}
			cg = new CustomizedGroup();
			cg.setLabel(accountValidation);

			Account acc = daoService.getAccountByLabel(accountValidation);
			if (acc == null) {
				acc = new Account();
				acc.setLabel(accountValidation);
				daoService.saveAccount(acc);
			}

			cg.setAccount(acc);

			cg.setQuotaOrder(Long.parseLong("1"));

			//this value is actually not used, all validation sms are sent.
			cg.setQuotaSms(Long.parseLong("1"));

			cg.setConsumedSms(Long.parseLong("0"));

			Role role = daoService.getRoleByName(validationRoleName);
			if (role == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Create the role : " + validationRoleName);
				}
				role = new Role();
				role.setName(validationRoleName);
				daoService.saveRole(role);
			}

			cg.setRole(role);

			daoService.addCustomizedGroup(cg);
		}

		cg.setConsumedSms(cg.getConsumedSms() + 1);
		daoService.updateCustomizedGroup(cg);

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
			ldapUtils.setUserPagerByUid(login, 
						    wanted.equals("") ? null : wanted);
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
		final String firstName = ldapUtils.getUserFirstNameByUid(userIdentifier);
		final String lastName = ldapUtils.getUserLastNameByUid(userIdentifier);
		final String phoneNumber = getPhoneNumber(userIdentifier);
		final List<String> availablePhoneNumbers = getAvailablePhoneNumbers(userIdentifier);
		boolean validCG = ldapUtils.isGeneralConditionValidateByUid(userIdentifier);
		final List<String> validCP = ldapUtils.getSpecificConditionsValidateByUid(userIdentifier);
		boolean isPending = false;
		if (isActivateValidation()) {
			isPending = daoService.isPendingMember(userIdentifier);
			if (isPending) {
				validCG = true;
			}
		} else {
			isPending = false;
		}
		String phoneNumberValidationCode = null;
		final Member member = new Member(userIdentifier, firstName, lastName, 
				phoneNumber, validCG, validCP, 
				isPending, phoneNumberValidationCode, availablePhoneNumbers);
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
		List<String> values;
		for (String attribute : this.phoneAttribute) {
			if (logger.isDebugEnabled()) {
				logger.debug("Search phone number with attribute " + attribute);
			}
			values = ldapUtils.getLdapAttributesByUidAndName(userIdentifier, attribute);
			for (String value : values) {
				String nValue = value.replaceAll(" ", "");
				if (!phoneNumberPrefixToRemove.equals("")) {
					if (logger.isDebugEnabled()) {
						logger.debug("phone Number Prefix To Remove " + phoneNumberPrefixToRemove);
					}
					nValue = nValue.replaceAll(phoneNumberPrefixToRemove, "0");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("test pattern with value " + nValue);
				}
				if (nValue.matches(this.phoneNumberPattern)) {
					phoneNumbers.add(nValue);
					if (logger.isDebugEnabled()) {
						logger.debug("phone number found from attribute " + attribute);
					}
				}
			}
		}	

		return phoneNumbers;
	}
	/**
	 * @param userIdentifier
	 * @return the phone number from the pager ldap field, or from a parametric ldap field if no pager is found
	 * @throws LdapUserNotFoundException
	 */
	private String getPhoneNumber(final String userIdentifier) throws LdapUserNotFoundException {
		logger.debug("Get phone number of member " + userIdentifier);
		String phoneNumber = ldapUtils.getUserPagerByUid(userIdentifier);
		logger.debug("Phone number of member " + userIdentifier + ": " + phoneNumber);
		return phoneNumber;
	}
	/**
	 * save the member.
	 * @param member
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	public void saveOrUpdateMember(final Member member) throws LdapUserNotFoundException, LdapWriteException {
		if (logger.isDebugEnabled()) {
			logger.debug("Save a member ");
		}

		boolean numberPhoneChanged = savePhoneNumber(member);

		String memberLogin = member.getLogin();
		List<String> memberValidCP = member.getValidCP();
		boolean validCG = member.getValidCG();
		if (validCG) {
			if (numberPhoneChanged && isActivateValidation()) {
				sendCodeBySMSAndAddPendingMember(member);
				// do not store the validated CG for now, it will be done once validated
				validCG = false;
			}
		} else {
			// also remove subscriptions to services
			memberValidCP = null;
			if (isActivateValidation() && daoService.isPendingMember(memberLogin)) {
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
		// retrieve the corresponding pending member
		final String login = member.getLogin();
		PendingMember pendingMember = daoService.getPendingMember(login);
		// check if exists
		boolean result = false;		
		if (pendingMember == null) {
			result = false;
		} else {
			// check if the code is correct
			final String dbCode = pendingMember.getValidationCode().toString();
			final String code = member.getPhoneNumberValidationCode();
			if (dbCode.equals(code)) {
				// add smsu general condition for this member in the LDAP 
				ldapUtils.addGeneralConditionByUid(login);
				// delete the pending member
				daoService.deletePendingMember(login);
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) {
		if (logger.isDebugEnabled()) {
			logger.debug("Request in memberManager : " + phoneNumber);
		}
		Boolean retVal = notificationPhoneNumberInBlackListClient.isPhoneNumberInBlackList(phoneNumber); 
		if (logger.isDebugEnabled()) {
			logger.debug("Response return in memberManager for : " 
					+ phoneNumber + " is : " + retVal);
		}

		return retVal;
	}

	/**
	 * @return true or false
	 */
	public Boolean isActivateValidation() {
		return activateValidation;
	}


	//////////////////////////////////////////////////////////////
	// Getter and Setter of phoneAttribute
	//////////////////////////////////////////////////////////////
	/**
	 * @param phoneAttribute
	 */
	public void setPhoneAttribute(final List<String> phoneAttribute) {
		this.phoneAttribute = phoneAttribute;
	}

	/**
	 * @return the list of phone attributes.
	 */
	public List<String> getPhoneAttribute() {
		return phoneAttribute;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of phoneNumberPattern
	//////////////////////////////////////////////////////////////
	/**
	 * @param phoneNumberPattern
	 */
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

	/**
	 * @return phoneNumberPattern.
	 */
	public String getPhoneNumberPattern() {
		return phoneNumberPattern;
	}

	///////////////////////////////////////
	// setter for spring objets
	//////////////////////////////////////

	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @param sendSmsClient
	 */
	public void setSendSmsClient(final SendSmsClient sendSmsClient) {
		this.sendSmsClient = sendSmsClient;
	}

	/**
	 * @param activateValidation
	 */
	public void setActivateValidation(final Boolean activateValidation) {
		this.activateValidation = activateValidation;
	}

	/**
	 * @param maxLengthCodeValidation
	 */
	public void setMaxNumberCodeValidation(final Integer maxNumberCodeValidation) {
		this.maxNumberCodeValidation = maxNumberCodeValidation;
	}

	/**
	 * @param accountValidation
	 */
	public void setAccountValidation(final String accountValidation) {
		this.accountValidation = accountValidation;
	}

	/**
	 * @param titleSmsValidation
	 */
	public void setTitleSmsValidation(final String titleSmsValidation) {
		this.titleSmsValidation = titleSmsValidation;
	}


	public void setNotificationPhoneNumberInBlackListClient(
			final NotificationPhoneNumberInBlackListClient notificationPhoneNumberInBlackListClient) {
		this.notificationPhoneNumberInBlackListClient = notificationPhoneNumberInBlackListClient;
	}

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

	/**
	 * @param phoneNumberPrefixToRemove
	 */
	public void setPhoneNumberPrefixToRemove(final String phoneNumberPrefixToRemove) {
		this.phoneNumberPrefixToRemove = phoneNumberPrefixToRemove;
	}

	/**
	 * @return
	 */
	public String getPhoneNumberPrefixToRemove() {
		return phoneNumberPrefixToRemove;
	}

	public String getValidationRoleName() {
		return validationRoleName;
	}

	public void setValidationRoleName(final String validationRoleName) {
		this.validationRoleName = validationRoleName;
	}

}
