package org.esupportail.smsu.web.beans;

import java.util.List;

import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.domain.DomainService;

/**
 * @author xphp8691
 *
 */
public class ServicesPaginator extends ListPaginator<UIService> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5929348481008113376L;
	
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
	public ServicesPaginator(final DomainService domainService) {
		super(null, 0);
		this.setDomainService(domainService);
	}

	//////////////////////////////////////////////////////////////
	// Principal method getData()
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.commons.web.beans.ListPaginator#getData()
	 */
	@Override
	protected List<UIService> getData() {
		return domainService.getAllUIServices();
	}

	/**
	 * @param domainService
	 */
	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}

	/**
	 * @return domainService
	 */
	public DomainService getDomainService() {
		return domainService;
	} 
}
