package org.esupportail.smsu.web.beans;

/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */

import java.util.ArrayList;
import java.util.List;


import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.domain.DomainService;


/** 
 * A paginator for roles.
 */ 
public class RolePaginator extends ListPaginator<UIRole> {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5351945226908445094L;

	/**
	 * The domain service.
	 */
	private DomainService domainService;
	
	/**
	 * roles list.
	 */
	private List<Integer> idRoles = new ArrayList<Integer>();
	
	 //////////////////////////////////////////////////////////////
	 // Constructors
	 //////////////////////////////////////////////////////////////
	 /**
	 * Constructor.
	 * @param domainService 
	 */
	@SuppressWarnings("deprecation")
	public RolePaginator(final DomainService domainService, final List<Integer> roles) {
		super(null, 0);
		this.domainService = domainService;
		this.idRoles = roles;
	}

	 /**
	 * Constructor.
	 * @param domainService 
	 */
	@SuppressWarnings("deprecation")
	public RolePaginator(final DomainService domainService) {
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
	protected List<UIRole> getData() {
		return domainService.getAllRoles(this.idRoles);
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(final List<Integer> roles) {
		this.idRoles = roles;
	}

	/**
	 * @return the roles
	 */
	public List<Integer> getRoles() {
		return idRoles;
	} 
	
}

