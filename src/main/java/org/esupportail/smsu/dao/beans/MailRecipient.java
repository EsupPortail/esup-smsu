package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represents mail recipients.
 */
public class MailRecipient  implements Serializable {

	/**
	 * Hibernate reference for mail recipient.
	 */
	public static final String REF = "MailRecipient";

	/**
	 * Hibernate property for the address.
	 */
	public static final String PROP_ADDRESS = "Address";

	/**
	 * Hibernate property for the address.
	 */
	public static final String PROP_LOGIN = "Login";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -6480239557091230008L;


	/**
	 * mail recipient identifier.
	 */
	private java.lang.Integer id;

	/**
	 * mail recipient address.
	 */
	private java.lang.String address;
	
	/**
	 * mail recipient login.
	 */
	private java.lang.String login;

	/**
	 * Bean constructor.
	 */
	public MailRecipient() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public MailRecipient(
		final java.lang.Integer id,
		final java.lang.String address, final java.lang.String login) {

		this.setId(id);
		this.setAddress(address);
		this.setLogin(login);
	}




	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="MRC_ID"
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
	 * Return the value associated with the column: MRC_ADDRESS.
	 */
	public java.lang.String getAddress() {
		return address;
	}

	/**
	 * Set the value related to the column: MRC_LOGIN.
	 * @param address the MRC_ADDRESS value
	 */
	public void setLogin(final java.lang.String login) {
		this.login = login;
	}

	/**
	 * Return the value associated with the column: MRC_LOGIN.
	 */
	public java.lang.String getLogin() {
		return login;
	}

	/**
	 * Set the value related to the column: MRC_ADDRESS.
	 * @param address the MRC_ADDRESS value
	 */
	public void setAddress(final java.lang.String address) {
		this.address = address;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof MailRecipient)) {
			return false;
		} else {
			MailRecipient mailRecipient = (MailRecipient) obj;
			if (null == this.getId() || null == mailRecipient.getId()) {
				return false;
			} else {
				return this.getId().equals(mailRecipient.getId());
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
		return "MailRecipient#" + hashCode() + "[id=[" + id + "], address=[" + address 
		+ "], login[" + login + "]]";
	}


}