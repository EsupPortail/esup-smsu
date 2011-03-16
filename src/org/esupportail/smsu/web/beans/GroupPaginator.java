package org.esupportail.smsu.web.beans;

/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */

import java.util.LinkedList;
import java.util.List;


import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;


/** 
 * A paginator for roles.
 */ 
public class GroupPaginator extends ListPaginator<DisplayedGroup> {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5351945226908445094L;

	/**
	 * The domain service.
	 */
	private DomainService domainService;
	
	private LdapUtils ldapUtils;
	
	 //////////////////////////////////////////////////////////////
	 // Constructors
	 //////////////////////////////////////////////////////////////
	 /**
	 * Constructor.
	 * @param domainService 
	 */
	@SuppressWarnings("deprecation")
	public GroupPaginator(final DomainService domainService, final LdapUtils ldapUtils) {
		super(null, 0);
		this.domainService = domainService;
		this.ldapUtils = ldapUtils;
	}

	//////////////////////////////////////////////////////////////
	// Principal method getData()
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.commons.web.beans.ListPaginator#getData()
	 */
	@Override
	protected List<DisplayedGroup> getData() {
		/*
		List<CustomizedGroup> list = new ArrayList<CustomizedGroup>();
		Account acc = new Account(1,"acc1");
		Role role = new Role(1,"role1");
		CustomizedGroup ctm = new CustomizedGroup(1, acc, role, "grp1", (long)20, (long)10, (long)5);
		list.add(ctm);
		return list;
		*/
		List<CustomizedGroup> listCg = domainService.getAllGroups();
		List<DisplayedGroup> listGroup = new LinkedList<DisplayedGroup>();
		for (CustomizedGroup cg : listCg) {
			DisplayedGroup dg = new DisplayedGroup();
			dg.setCustomizedGroup(cg);
			String displayName;
			try {
				displayName = ldapUtils.getUserDisplayNameByUserUid(cg.getLabel());
			} catch (LdapUserNotFoundException e) {
				
				displayName = ldapUtils.getGroupNameByUid(cg.getLabel());
				if (displayName == null) {
					displayName = cg.getLabel();
				}	
			}
			dg.setDisplayName(displayName);
			listGroup.add(dg);
		}
		//return domainService.getAllGroups();
		return listGroup;
	} 
	
}

