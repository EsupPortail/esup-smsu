package org.esupportail.smsu.groups;


/**
 * xphp8691
 */

public class SmsuLdapGroupPersonAttributeDaoImpl {

    
    /**
     * String from LDAP attribute name (default Distinguished Name).
     */
    private String ldapAttribute;
    
    /**
     * String uPortal attribute name.
     */
    private String portalAttribute;
    
    
    /**
     * a constructor.
     */
    public SmsuLdapGroupPersonAttributeDaoImpl() {
		super();
	}

	/**
     * @return Returns LDAP attribute name
     */
    public String getLdapAttribute() {
		return ldapAttribute;
	}

    /**
     * @param ldapAttribute The LDAP attribute to set
     */
	public void setLdapAttribute(final String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}

	/**
	 * @return Returns portal attribute name
	 */
	public String getPortalAttribute() {
		return portalAttribute;
	}

	/**
	 * @param portalAttribute The portal attribute to set
	 */
	public void setPortalAttribute(final String portalAttribute) {
		this.portalAttribute = portalAttribute;
	}
    
    
    /**
     * To string
     */
    @Override
	public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getClass().getName());
    	sb.append(" portalAttribute=").append(this.portalAttribute);
    	sb.append(" ldapAttribute=").append(this.ldapAttribute);
    	
    	return sb.toString();
    }

}
