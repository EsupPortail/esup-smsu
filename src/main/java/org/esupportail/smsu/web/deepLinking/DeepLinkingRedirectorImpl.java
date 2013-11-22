/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.web.deepLinking;

import java.util.Map;

import org.esupportail.smsu.web.controllers.ApprovalController;
import org.esupportail.smsu.web.controllers.SessionController;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.web.deepLinking.AbstractDeepLinkingRedirector;

/**
 * The esup-helpdesk implementation of the page redirector (for deep linking).
 */
public class DeepLinkingRedirectorImpl extends AbstractDeepLinkingRedirector {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -4484064189163071618L;
	
	/**
	 * The session controller.
	 */
	private SessionController sessionController;

	/**
	 * The approval controller.
	 */
	private ApprovalController approvalController;
	
	/**
	 * Bean constructor.
	 */
	public DeepLinkingRedirectorImpl() {
		super();
	}

	/**
	 * @see org.esupportail.commons.beans.AbstractI18nAwareBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		Assert.notNull(this.sessionController, "property sessionController of class " 
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(approvalController, "property approvalController of class "
				+ this.getClass().getName() + " can not be null");
	}

	/**
	 * @see org.esupportail.commons.web.deepLinking.DeepLinkingRedirector#redirect(java.util.Map)
	 */
	public String redirect(
			final Map<String, String> params) {

		sessionController.resetSessionLocale();

		if (params != null && params.containsKey("approvalSMS")) {
			if (approvalController.enter() != null)
				// nb: can not use handleNavigation since there is no FacesContext instance yet (??)
				return "/stylesheets/approvalSMS/list_sms.jsp";
		};
		return null;
	}

	/**
	 * @param sessionController the sessionController to set
	 */
	public void setSessionController(final SessionController sessionController) {
		this.sessionController = sessionController;
	}

	/**
	 * @param approvalController the approvalController to set
	 */
	public void setApprovalController(final ApprovalController approvalController) {
		this.approvalController = approvalController;
	}

}
