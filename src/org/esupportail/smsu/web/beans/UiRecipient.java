package org.esupportail.smsu.web.beans;

/**
 * @author xphp8691
 *
 */
public abstract class UiRecipient {

	/**
	 * id.
	 */
	private String id;
	
	/**
	 * displayName.
	 */
	private String displayName;
	
	/**
	 * the phone number.
	 */
	private String phone;

	/**
	 * the login.
	 */
	private String login;
	
	/**
	 * constructor.
	 */
	public UiRecipient() {
		super();
	}

	

	/**
	 * @param displayName
	 * @param id
	 * @param login
	 * @param phone
	 */
	public UiRecipient(final String displayName, final String id, final String login, final String phone) {
		super();
		this.displayName = displayName;
		this.id = id;
		this.login = login;
		this.phone = phone;
	}

	
	


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}



	/* *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UiRecipient other = (UiRecipient) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}



	/**
	 * @param id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param displayName
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param phone
	 */
	public void setPhone(final String phone) {
		this.phone = phone;
	}

	/**
	 * @return phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param login
	 */
	public void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * @return login
	 */
	public String getLogin() {
		return login;
	}

}
