package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.WriteableLdapUserServiceImpl;
import org.esupportail.commons.services.ldap.LdapAttributesModificationException;
import org.esupportail.commons.services.ldap.LdapUserService;
import org.esupportail.commons.services.ldap.LdapUserAndGroupService;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.springframework.ldap.support.DirContextAdapter;

/**
 * SMSU implementation of the WriteableLdapUserServiceImpl.
 * @author PRQD8824
 *
 */
public class WriteableLdapUserServiceSMSUImpl extends WriteableLdapUserServiceImpl {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	private CacheManager cacheManager;

	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 8671819078089269831L;
	public WriteableLdapUserServiceSMSUImpl() {
		super();
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		if (cacheManager == null) {
			logger.info("property cacheManager is not set. This is not a problem if you do not use a LDAP cache. Otherwise this cache will be incoherent after using updateLdapUser()");
		}

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
	
	/**
	 * Set or clear a user specified attribute.
	 * It handles the attribute etiquette: 
	 * - it keeps unmodified attribute values without this etiquette
	 * - it prefixes the values with this etiquette
	 * @param ldapUser
	 * @param attrName
	 * @param etiquette
	 * @param value
	 * @throws LdapAttributesModificationException 
	 */
	public void setOrClearUserAttribute(final LdapUser ldapUser, final String attrName, 
					    final String etiquette, final List<String> value) 
					throws LdapAttributesModificationException {
		Map<String, List<String>> attrs = ldapUser.getAttributes();
		List<String> allValues = computeAttributeValues(attrs.get(attrName), etiquette, value);
		attrs.put(attrName, allValues);

		// call updateLdapUser with only the attribute we want to write in LDAP
		ldapUser.setAttributes(singletonMap(attrName, allValues));
		updateLdapUser(ldapUser);
		ldapUser.setAttributes(attrs); // restore other attributes
	}

	public void setOrClearUserAttribute(final LdapUserService ldapService, final String id, 
					    final String attrName, final String etiquette, final List<String> value) 
					throws UserNotFoundException, LdapAttributesModificationException {
		// ensure we read straight from LDAP
		// it is especially important since the attribute values with a different etiquette may have changed since last read
		invalidateLdapCache();

		LdapUser ldapUser = ldapService.getLdapUser(id);
		setOrClearUserAttribute(ldapUser, attrName, etiquette, value);
		checkAttributeWriteSucceeded(ldapService, id, attrName, ldapUser);
	}

	public void setOrClearUserAttribute(final LdapUserAndGroupService ldapService, final String id, 
					    final String attrName, final String etiquette, final List<String> value) 
					throws UserNotFoundException, LdapAttributesModificationException {
		// ensure we read straight from LDAP
		// it is especially important since the attribute values with a different etiquette may have changed since last read
		invalidateLdapCache();

		LdapUser ldapUser = ldapService.getLdapUser(id);
		setOrClearUserAttribute(ldapUser, attrName, etiquette, value);
		checkAttributeWriteSucceeded(ldapService, id, attrName, ldapUser);
	}

	private <A, B> Map<A, B> singletonMap(A key, B value) {
		Map<A, B> r = new HashMap<A, B>();
		r.put(key, value);
		return r;
	}

	public static String join(Iterable<?> elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object s : elements) {
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}

	private List<String> computeAttributeValues(List<String> currentValues,	final String etiquette, final List<String> wantedValues) {
		if (StringUtils.isEmpty(etiquette))
			return wantedValues;

		Set<String> set = new TreeSet<String>();
		if (currentValues != null) {
		    for (String s : currentValues)
			if (!s.startsWith(etiquette)) set.add(s);
		}
		for (String v : wantedValues) 
			set.add(mayAddPrefix(etiquette, v));
		return new ArrayList<String>(set);
	}

	private String mayAddPrefix(String prefix, String s) {
		return prefix == null || s.startsWith(prefix) ? s : prefix + s;
	}

	/**
	 * Check wether setting or clearing attribute worked correctly
	 * @throws UserNotFoundException 
	 * @throws LdapAttributesModificationException
	 */
	private void checkAttributeWriteSucceeded(final LdapUserService ldapService, final String id, final String attrName, final LdapUser wantedLdapUser) throws UserNotFoundException, LdapAttributesModificationException {
		checkAttributeWriteSucceeded(ldapService.getLdapUser(id), attrName, wantedLdapUser);
	}
	private void checkAttributeWriteSucceeded(final LdapUserAndGroupService ldapService, final String id, final String attrName, final LdapUser wantedLdapUser) throws UserNotFoundException, LdapAttributesModificationException {
		checkAttributeWriteSucceeded(ldapService.getLdapUser(id), attrName, wantedLdapUser);
	}
	private void checkAttributeWriteSucceeded(final LdapUser storedLdapUser, final String attrName, final LdapUser wantedLdapUser) throws LdapAttributesModificationException {
		List<String> value = wantedLdapUser.getAttributes(attrName);
		List<String> storedValue = storedLdapUser.getAttributes(attrName);

		String error = null;
		if (value == null && (storedValue == null || storedValue.isEmpty()))
			;
		// nb: we can't check wether clearing attribute really removed the attribute or simply emptied it
		else if (value != null && storedValue == null)
			// this never happens, storedValue is never null afaik
			error = "could not create attribute '" + attrName + "' with value " + join(value, ", ");
		else if (!value.containsAll(storedValue) || !storedValue.containsAll(value))
			error = "could not modify attribute '" + attrName + "' with value " + join(value, ", ") + ", it's value is still " + join(storedValue, ", "); 

		if (error != null) {
			logger.error(error);
			throw new LdapAttributesModificationException(error);
		}
	}

	public void invalidateLdapCache() {
		if (cacheManager != null) {
			net.sf.ehcache.Cache cache = cacheManager.getCache(org.esupportail.commons.services.ldap.CachingLdapEntityServiceImpl.class.getName());
			if (cache != null)
				cache.removeAll();
			else
				logger.error("could not find cacheManager");
		} else {
			logger.debug("no LDAP cacheManager to warn");
		}
	}
	
	public void setCacheManager(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
