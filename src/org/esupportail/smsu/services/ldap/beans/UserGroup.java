package org.esupportail.smsu.services.ldap.beans;

/**
 * This class represents group of user.
 */
public class UserGroup {
	
	/**
	 * identifier from the LDAP.
	 */
	private String ldapId;
	
	/**
	 * name from the LDAP.
	 */
	private String ldapName;

	private int hashCode = Integer.MIN_VALUE;

	/**
	 * basic constructor.
	 */
	public UserGroup() {
		super();
	}
	
	/**
	 * constructor with required attributes.
	 * @param ldapId : identifier from the LDAP
	 * @param ldapName : name from the LDAP
	 */
	public UserGroup(final String ldapId, final String ldapName) {
		super();
		this.setLdapId(ldapId);
		this.setLdapName(ldapName);
	}

	/**
	 * retrieve the LDAP identifier.
	 * @return
	 */
	public String getLdapId() {
		return ldapId;
	}

	/**
	 * setter for the LDAP identifier.
	 * @param ldapId
	 */
	public void setLdapId(final String ldapId) {
		this.ldapId = ldapId;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * retrieve the LDAP name.
	 * @return
	 */
	public String getLdapName() {
		return ldapName;
	}

	/**
	 * setter for the LDAP name.
	 * @param ldapName
	 */
	public void setLdapName(final String ldapName) {
		this.ldapName = ldapName;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof UserGroup)) {
			return false;
		} else {
			UserGroup groupUser = (UserGroup) obj;
			if (null == this.getLdapId() || null == groupUser.getLdapId()) {
				return false;
			} else {
				return this.getLdapId().equals(groupUser.getLdapId());
			}
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getLdapId()) {
				return super.hashCode();
			} else {
				String hashStr = this.getClass().getName() + ":" + this.getLdapId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


}
