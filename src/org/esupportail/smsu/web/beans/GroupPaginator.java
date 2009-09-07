package org.esupportail.smsu.web.beans;

/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */

import java.util.ArrayList;
import java.util.List;


import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.domain.DomainService;


/** 
 * A paginator for roles.
 */ 
public class GroupPaginator extends ListPaginator<CustomizedGroup> {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5351945226908445094L;

	/**
	 * The domain service.
	 */
	private DomainService domainService;
	
	
	 //////////////////////////////////////////////////////////////
	 // Constructors
	 //////////////////////////////////////////////////////////////
	 /**
	 * Constructor.
	 * @param domainService 
	 */
	@SuppressWarnings("deprecation")
	public GroupPaginator(final DomainService domainService) {
		super(null, 0);
		this.domainService = domainService;
	}

	//////////////////////////////////////////////////////////////
	// Principal method getData()
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.commons.web.beans.ListPaginator#getData()
	 */
	@Override
	protected List<CustomizedGroup> getData() {
		/*
		List<CustomizedGroup> list = new ArrayList<CustomizedGroup>();
		Account acc = new Account(1,"acc1");
		Role role = new Role(1,"role1");
		CustomizedGroup ctm = new CustomizedGroup(1, acc, role, "grp1", (long)20, (long)10, (long)5);
		list.add(ctm);
		return list;
		*/
		return domainService.getAllGroups();
	} 
	
}

