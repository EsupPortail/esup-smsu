package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.CacheManager;

import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.WriteableLdapUserServiceImpl;
import org.springframework.ldap.support.DirContextAdapter;

/**
 * SMSU implementation of the WriteableLdapUserServiceImpl.
 * @author PRQD8824
 *
 */
public class WriteableLdapUserServiceSMSUImpl extends WriteableLdapUserServiceImpl {
	
	private CacheManager cacheManager;

	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 8671819078089269831L;
	public WriteableLdapUserServiceSMSUImpl() {
		super();
	}
	/**
	 * The WriteableLdapUserServiceImpl implementation is overwritted because 
	 * it doest not allow to remove attribute.
	 * @param ldapUser
	 * @param context
	 */
	protected void mapToContext(final LdapUser ldapUser, final DirContextAdapter context) {
		List<String> attributesNames = ldapUser.getAttributeNames();
		for (String ldapAttributeName : attributesNames) {
			List<String> listAttr = new ArrayList<String>();
			listAttr = ldapUser.getAttributes(ldapAttributeName);
			// The attribute exists
			if (listAttr != null && listAttr.size() != 0) {
				context.setAttributeValues(ldapAttributeName, listAttr.toArray());
			} else {
				// manage the attributes erasing
				context.setAttributeValues(ldapAttributeName, null);
			}
		}
	}

	public void updateLdapUser(final LdapUser ldapUser) throws org.esupportail.commons.services.ldap.LdapAttributesModificationException {
		super.updateLdapUser(ldapUser);
		invalidateLdapCache();
	}

	public void invalidateLdapCache() {
		net.sf.ehcache.Cache cache = cacheManager.getCache(org.esupportail.commons.services.ldap.CachingLdapEntityServiceImpl.class.getName());
		cache.removeAll();		
	}

	public void setCacheManager(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
