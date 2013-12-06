package org.esupportail.smsu.business.beans;

import java.util.List;


/**
 * The class that represent members.
 */
public class Member {

	/**
	 * user first name.
	 */
	public String firstName;

	/**
	 * user last name.
	 */
	public String lastName;

	/**
	 * user phone number.
	 */
	public String phoneNumber;

	/**
	 * available phone numbers.
	 */
	public List<String> availablePhoneNumbers;
	/**
	 * validation code for membership activation.
	 */
	public String phoneNumberValidationCode;
	
	/**
	 * flag that indicates if the membership agrees with the general conditions.
	 */
	public Boolean validCG;
	
	/**
	 * List of specific conditions.
	 */
	public List<String> validCP;

	/**
	 * user login.
	 */
	public String login;

	/**
	 * flag that indicates if the member is in progress.
	 */
	public Boolean flagPending;


	private int hashCode = Integer.MIN_VALUE;


	///////////////////////////////////
	// Getters and Setters
	///////////////////////////////////
	/**
	 * @return phone number validation code
	 */
	public String getPhoneNumberValidationCode() {
		return phoneNumberValidationCode;
	}

	/**
	 * @param phoneNumberValidationCode
	 */
	public void setPhoneNumberValidationCode(final String phoneNumberValidationCode) {
		this.phoneNumberValidationCode = phoneNumberValidationCode;
	}


	/**
	 * @return 
	 */
	public String getFirstName() {			
		return this.firstName;
	}

	/**
	 * @param firstName
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return phone number.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @param validCG
	 */
	public void setValidCG(final Boolean validCG) {
		this.validCG = validCG;
	}

	/**
	 * @return
	 */
	public Boolean getValidCG() {
		return validCG;
	}

	/**
	 * @param login
	 */
	public void setLogin(final String login) {
		this.login = login;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * @return
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param validCP
	 */
	public void setValidCP(final List<String> validCP) {
		this.validCP = validCP;
	}

	/**
	 * @return
	 */
	public List<String> getValidCP() {
		return validCP;
	}

	/**
	 * @param isPending
	 */
	public void setFlagPending(final Boolean isPending) {
		this.flagPending = isPending;
	}
	
	/**
	 * @return
	 */
	public Boolean getFlagPending() {
		return flagPending;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Member)) {
			return false;
		} else {
			Member member = (Member) obj;
			if (null == this.getLogin() || null == member.getLogin()) {
				return false;
			} else {
				return this.getLogin().equals(member.getLogin());
			}
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getLogin()) {
				return super.hashCode();
			} else {
				String hashStr = this.getClass().getName() + ":" + this.getLogin().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}



	/**
	 * @param availablePhoneNumbers
	 */
	public void setAvailablePhoneNumbers(final List<String> availablePhoneNumbers) {
		this.availablePhoneNumbers = availablePhoneNumbers;
	}



	/**
	 * @return the list of available phone numbers.
	 */
	public List<String> getAvailablePhoneNumbers() {
		return availablePhoneNumbers;
	}


	
}