package org.esupportail.smsu.web.beans;

/**
 * @author xphp8691
 *
 */
public class UIPerson {

	/**
	 * id.
	 */
	private String id;
	
	/**
	 * displayName.
	 */
	private String displayName;
	
	/**
	 * the login.
	 */
	private String login;
	
	/**
	 * constructor.
	 */
	public UIPerson() {
		super();
	}

	/**
	 * @param displayName
	 * @param login
	 */
	public UIPerson(final String displayName, final String login) {
		super();
		this.displayName = displayName;
		this.login = login;
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
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		UIPerson other = (UIPerson) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
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
