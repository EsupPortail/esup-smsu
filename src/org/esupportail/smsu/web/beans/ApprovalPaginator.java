package org.esupportail.smsu.web.beans;

/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */

import java.util.List;


import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.domain.DomainService;


/** 
 * A paginator for messages.
 */ 
public class ApprovalPaginator extends ListPaginator<UIMessage> {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5351945226908445094L;

	/**
	 * The domain service.
	 */
	private DomainService domainService;

	/**
	 * The user id.
	 */
	private String idUser;
	
	 
	 //////////////////////////////////////////////////////////////
	 // Constructors
	 //////////////////////////////////////////////////////////////
	 /**
	 * Constructor.
	 * @param domainService 
	 */
	@SuppressWarnings("deprecation")
	public ApprovalPaginator(final DomainService domainService) {
		super(null, 0);
		this.domainService = domainService;
	}

	 /**
	 * Constructor.
	 * @param domainService 
	 */
	@SuppressWarnings("deprecation")
	public ApprovalPaginator(final DomainService domainService, final String idUser) {
		super(null, 0);
		this.domainService = domainService;
		this.setIdUser(idUser);
	}
	//////////////////////////////////////////////////////////////
	// Principal method getData()
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.commons.web.beans.ListPaginator#getData()
	 */
	@Override
	protected List<UIMessage> getData() {
		return domainService.getApprovalUIMessages(this.idUser);
		
	}

	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(final String idUser) {
		this.idUser = idUser;
	}

	/**
	 * @return the idUser
	 */
	public String getIdUser() {
		return idUser;
	} 
	
	
	
}

