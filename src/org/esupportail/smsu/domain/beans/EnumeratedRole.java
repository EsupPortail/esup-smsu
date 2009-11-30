package org.esupportail.smsu.domain.beans;

/**
 * Enumeration of role.
 * 
 * @author XPHP8691
 * 
 */
public enum EnumeratedRole {
	
	// Super admin,
	SUPER_ADMIN("ROLE.SUPERADMIN.NAME");

	/**
	 * I18n key role for display name.
	 */
	private String i18nKey;

	/**
	 * Constructor with i18n key.
	 * 
	 * @param key
	 */
	private EnumeratedRole(final String key) {
		i18nKey = key;
	}

	/**
	 * Getter for 'i18nKey'.
	 * 
	 * @return
	 */
	public String getI18nKey() {
		return i18nKey;
	}
}
