package org.esupportail.smsu.web.beans;

import java.util.List;

import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.domain.DomainService;

/**
 * @author xphp8691
 *
 */
public class TemplatesPaginator extends ListPaginator<UITemplate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3357242275193903130L;

	/**
	 * The domain service.
	 */
	private DomainService domainService;
	
	
	
	/**
	 * @param domainService
	 */
	public TemplatesPaginator(final DomainService domainService) {
		super();
		this.setDomainService(domainService);
	}



	@Override
	protected List<UITemplate> getData() {
		return domainService.getUITemplates();
	}



	public DomainService getDomainService() {
		return domainService;
	}



	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}

}
