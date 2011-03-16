package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.beans.Member;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;

/**
 * A bean to manage files.
 */
public class MembershipController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1149078913806276304L;
	
	/**
	 * The pattern used to validate a phone number.
	 */
	private String phoneNumberPattern;
	
	/**
	 * uid used to get user informations.
	 */
	private String ldapUid;

	/**
	 * current member.
	 */
	private Member member;

	/**
	 * list of the service.
	 */
	private List<Service> allServices;
	
	private List<SelectItem> availablePhoneNumbers;
	
	private Boolean activateValidation;
	
	private Boolean isValidationOK;
		
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
    //////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public MembershipController() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Access control method 
	//////////////////////////////////////////////////////////////
	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		//no access control is set for the page.
		/*User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}*/
		return true;
	}
	
	//////////////////////////////////////////////////////////////
	// Enter method (for Initialazation)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 * @throws LdapUserNotFoundException 
	 */
	public String enter() throws LdapUserNotFoundException {
		// rights to enter
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		// User not found in Ldap
		User currentUser = getCurrentUser();
		// User not found in Ldap
		if (currentUser == null) {
			return "navigationMembershipNotInLDAP";
		} else {
		// initialize data in the page
		init();
		return "navigationMembership";
		}
		
	}
	
	//////////////////////////////////////////////////////////////
	// Init methods 
	//////////////////////////////////////////////////////////////
	/**
	 * initialize data in the page.
	 * @throws LdapUserNotFoundException 
	 */
	private void init() throws LdapUserNotFoundException {
		isValidationOK = false;
		User user = getCurrentUser();
		ldapUid = user.getId();
		this.member = getDomainService().getMember(ldapUid);
		this.allServices = getDomainService().getAllServices();
		if (this.member != null) {
		try {	
		Boolean retVal = getDomainService().isPhoneNumberInBlackList(this.member.getPhoneNumber());
				if (retVal) {
				addErrorMessage("formMembership:phoneNumber", "ADHESION.MESSAGE.PHONEINBLACKLIST");
				}
		} catch (Exception e){
				addErrorMessage("formMembership:phoneNumber", "ADHESION.MESSAGE.WSERROR");
		}
		}
	}

	/**
	 * save action.
	 * @return A String
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	public String save() throws LdapUserNotFoundException, LdapWriteException {
		if (logger.isDebugEnabled()) {
			logger.debug("Save data of a member");
		}
		// save datas into LDAP
		getDomainService().saveOrUpdateMember(member);
		// re-initialize 
		init();
		// create a message to give a feedback to the member
		if (!member.getFlagPending()) {
			addInfoMessage(null, "ADHESION.MESSAGE.MEMBEROK");
		}
		return null;
	}
	
	/**
	 * valid the member thank to its code.
	 * @return A String
	 * @throws LdapUserNotFoundException 
	 * @throws LdapWriteException 
	 */
	public String validCode() throws LdapUserNotFoundException, LdapWriteException  {
		if (logger.isDebugEnabled()) {
			logger.debug("Valid the code");
		}
		// check if the code is correct
		// and accept definitely the user inscription if the code is correct
		final boolean valid = getDomainService().validMember(member);
		// re-initialize 
		init();
		// create a message to give a feedback to the member
		if (valid) {
			isValidationOK = true;
		} else {
			addErrorMessage("formMembership:phoneNumberValidationCode", "ADHESION.MESSAGE.MEMBERCODEKO");
		}
		return null;
	}

	
	/**
	 * @param context
	 * @param componentToValidate
	 * @param value
	 * @throws ValidatorException
	 */
	public void validatePhoneNumber(
			final FacesContext context,
			final UIComponent componentToValidate,
			final Object value) throws ValidatorException {
		String strValue = (String) value;

		if (!this.phoneNumberPattern.trim().equals("")) {
			if (!strValue.matches(this.phoneNumberPattern)) {
				throw new ValidatorException(getFacesErrorMessage("ADHESION.ERROR.INVALIDPHONENUMBER"));
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	/**
	 * used to add a phone number validation pattern.
	 * @return the phone number validation pattern
	 */
	public List<Service> getAllServices() {
		return allServices;
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
	 * used to add a phone number validation pattern.
	 * @return the phone number validation pattern
	 */
	public String getPhoneNumberPattern() {
		return phoneNumberPattern;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of member
	//////////////////////////////////////////////////////////////
	/**
	 * Used to display details about the current user.
	 * @return user first name
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * @param member
	 */
	public void setMember(final Member member) {
		this.member = member;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUid
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUid
	 */
	public void setLdapUid(final String ldapUid) {
		this.ldapUid = ldapUid;
	}

	/**
	 * @return ldapUid
	 */
	public String getLdapUid() {
		return ldapUid;
	}

	public void setAvailablePhoneNumbers(final List<SelectItem> availablePhoneNumbers) {
		this.availablePhoneNumbers = availablePhoneNumbers;
	}

	/**
	 * @return
	 */
	public List<SelectItem> getAvailablePhoneNumbers() {
		availablePhoneNumbers = new ArrayList<SelectItem>();
		List<String> phoneNumbers = member.getAvailablePhoneNumbers();
		for (String phoneNumber : phoneNumbers) {
			availablePhoneNumbers.add(new SelectItem(phoneNumber));
		}
		return availablePhoneNumbers;
	}

	/**
	 * @param activateValidation
	 */
	public void setActivateValidation(final Boolean activateValidation) {
		this.activateValidation = activateValidation;
	}

	/**
	 * @return
	 */
	public Boolean getActivateValidation() {
		return activateValidation;
	}

	public void setIsValidationOK(final Boolean isValidationOK) {
		this.isValidationOK = isValidationOK;
	}

	public Boolean getIsValidationOK() {
		return isValidationOK;
	}
	
}
