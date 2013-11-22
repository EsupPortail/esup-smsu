package org.esupportail.smsu.groups;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.jasig.portal.services.persondir.support.MultivaluedPersonAttributeUtils;


@SuppressWarnings("unchecked")
public class SmsuLdapPersonAttributeDaoImpl {

    /**
     * Map from LDAP attribute names to uPortal attribute names.
     */
    private Map attributeMappings = Collections.EMPTY_MAP;
    
    /**
     * reverse map.
     */
    private Map reverseAttributeMappings = Collections.EMPTY_MAP;

    /**
     * {@link Set} of attributes this DAO may provide when queried.
     */
    private Set userAttributes = Collections.EMPTY_SET;

	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());
    /*
     * @see org.jasig.portal.services.persondir.support.IPersonAttributeDao#getPossibleUserAttributeNames()
     */
    public Set getPossibleUserAttributeNames() {
        return this.userAttributes;
    }

    /**
     * Get the mapping from LDAP attribute names to uPortal attribute names.
     * Mapping type is from String to [String | Set of String].
     * @return Returns the ldapAttributesToPortalAttributes.
     */
    public Map getLdapAttributesToPortalAttributes() {
        return this.attributeMappings;
    }

    public Map getReverseAttributeMappings() {
    	return this.reverseAttributeMappings;
    }
    
    /**
     * Set the {@link Map} to use for mapping from a ldap attribute name to a
     * portal attribute name or {@link Set} of portal attribute names. Ldap
     * attribute names that are specified but have null mappings will use the
     * ldap attribute name for the portal attribute name.
     * Ldap attribute names that are not specified as keys in this {@link Map}
     * will be ignored.
     * <br>
     * The passed {@link Map} must have keys of type {@link String} and values
     * of type {@link String} or a {@link Set} of {@link String}.
     *
     * @param ldapAttributesToPortalAttributesArg {@link Map} from ldap attribute names to portal attribute names.
     * @throws IllegalArgumentException If the {@link Map} doesn't follow the rules stated above.
     * @see MultivaluedPersonAttributeUtils#parseAttributeToAttributeMapping(Map)
     */
    public void setLdapAttributesToPortalAttributes(final Map ldapAttributesToPortalAttributesArg) {
        this.attributeMappings = MultivaluedPersonAttributeUtils.parseAttributeToAttributeMapping(ldapAttributesToPortalAttributesArg);
        
        this.reverseAttributeMappings = new HashMap();
        for (final Iterator ldapAttrIter = this.attributeMappings.keySet().iterator(); ldapAttrIter.hasNext();) {
            final String ldapAttributeName = (String)ldapAttrIter.next();
            Set values = (Set) this.attributeMappings.get(ldapAttributeName);
            for (final Iterator valuesIter = values.iterator(); valuesIter.hasNext();) {
            	final String value = (String)valuesIter.next();
            	this.reverseAttributeMappings.put(value, ldapAttributeName);
            }
		}
        
        final Collection userAttributeCol = MultivaluedPersonAttributeUtils.flattenCollection(this.attributeMappings.values());

        this.userAttributes = Collections.unmodifiableSet(new HashSet(userAttributeCol));
    }

    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getClass().getName());
    	sb.append(" attributeMappings=").append(this.attributeMappings);

    	return sb.toString();
    }
}
