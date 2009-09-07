package org.esupportail.smsu.business.beans;

import java.util.List;


/**
 * The class that represent members.
 */
public class Member {

	/**
	 * user first name.
	 */
	private String firstName;

	/**
	 * user last name.
	 */
	private String lastName;

	/**
	 * user phone number.
	 */
	private String phoneNumber;

	/**
	 * validation code for membership activation.
	 */
	private String phoneNumberValidationCode;
	
	/**
	 * flag that indicates if the membership agrees with the general conditions.
	 */
	private Boolean validCG;
	
	/**
	 * List of specific conditions.
	 */
	private List<String> validCP;

	/**
	 * user login.
	 */
	private String login;

	/**
	 * flag that indicates if the member is in progress.
	 */
	private Boolean flagPending;


	private int hashCode = Integer.MIN_VALUE;

	
	
	/////////////////////
	// Constructors
	/////////////////////
	/**
	 * Bean constructor.
	 */
	/**
	 * constructor with required attributes.
	 * @param login
	 * @param firstName
	 * @param lastName
	 * @param phoneNumber
	 * @param validCG
	 * @param validCP
	 * @param phoneNumberValidationCode
	 * @param isPending 
	 */
	public Member(final String login, final String firstName, final String lastName,
			final String phoneNumber, final Boolean validCG, final List<String> validCP,
			final Boolean isPending, final String phoneNumberValidationCode) {
		super();
		this.setFirstName(firstName);
		this.setLogin(login);
		this.setLastName(lastName);
		this.setPhoneNumber(phoneNumber);
		this.setPhoneNumberValidationCode(phoneNumberValidationCode);
		this.setValidCG(validCG);
		this.setValidCP(validCP);
		this.setFlagPending(isPending);
	}



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
	private void setFlagPending(final Boolean isPending) {
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


	
}