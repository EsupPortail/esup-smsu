package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represent recipients.
 */
public class Recipient implements Serializable {

	/**
	 * Hibernate reference for person.
	 */
	public static final String REF = "Recipient";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * Hibernate property for the phone.
	 */
	public static final String PROP_PHONE = "Phone";
	
	/**
	 * Hibernate property for the login.
	 */
	public static final String PROP_LOGIN = "Login";


	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -2722215388225845211L;

	/**
	 * recipient identifier.
	 */
	private java.lang.Integer id;

	/**
	 * recipient phone.
	 */
	private java.lang.String phone;

	/**
	 * recipient login.
	 */
	private java.lang.String login;
	
	/**
	 * Bean constructor.
	 */
	public Recipient() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public Recipient(
		final java.lang.Integer id,
		final String phone, final String login) {
		this.setId(id);
		this.setPhone(phone);
		this.setLogin(login);
	}




	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="RCP_ID"
     */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * Return the value associated with the column: RCP_PHONE.
	 */
	public java.lang.String getPhone() {
		return phone;
	}

	/**
	 * Set the value related to the column: RCP_PHONE.
	 * @param phone the RCP_PHONE value
	 */
	public void setPhone(final java.lang.String phone) {
		this.phone = phone;
	}

	/**
	 * Set the value related to the column: RCP_LOGIN.
	 * @param login the RCP_LOGIN value
	 */
	public void setLogin(final java.lang.String login) {
		this.login = login;
	}

	/**
	 * Return the value associated with the column: RCP_LOGIN.
	 */
	public java.lang.String getLogin() {
		return login;
	}
	

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Recipient)) {
			return false;
		} else {
			Recipient recipient = (Recipient) obj;
			if (null == this.getId() || null == recipient.getId()) {
				return false;
			} else {
				return this.getId().equals(recipient.getId());
			}
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Recipient#" + hashCode() + "[id=[" + id + "], phone=[" + phone 
		+ "], login=[" + login + "]]";
	}


}