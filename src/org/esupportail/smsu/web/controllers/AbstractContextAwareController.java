/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import org.esupportail.commons.utils.Assert;
import org.esupportail.smsu.domain.beans.User;


/**
 * An abstract class inherited by all the beans for them to get:
 * - the context of the application (sessionController).
 * - the domain service (domainService).
 * - the application service (applicationService).
 * - the i18n service (i18nService).
 */
public abstract class AbstractContextAwareController extends AbstractDomainAwareBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The SessionController.
	 */
	private SessionController sessionController;

	/**
	 * Constructor.
	 */
	protected AbstractContextAwareController() {
		super();
	}

	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractDomainAwareBean#afterPropertiesSetInternal()
	 */
	@Override
	public void afterPropertiesSetInternal() {
		Assert.notNull(this.sessionController, "property sessionController of class " 
				+ this.getClass().getName() + " can not be null");
	}

	/**
	 * @param sessionController the sessionController to set
	 */
	public void setSessionController(final SessionController sessionController) {
		this.sessionController = sessionController;
	}

	/**
	 * @return the sessionController
	 */
	public SessionController getSessionController() {
		return sessionController;
	}

	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractDomainAwareBean#getCurrentUser()
	 */
	@Override
	protected User getCurrentUser() {
		return sessionController.getCurrentUser();
	}

}
