/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import org.esupportail.smsu.domain.beans.User;

/**
 * A bean to manage files.
 */
public class AboutController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -522191275533736924L;

	/**
	 * Bean constructor.
	 */
	public AboutController() {
		super();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	/**
	 * @return true if the current user is allowed to test the exceptions.
	 */
	public boolean isExceptionAuthorized() {
		boolean result = false;

		User currentUser = getCurrentUser();
		if (currentUser != null) {
			
			result = currentUser.isSuperAdmin();
		} 

		return result;
	}
	
	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		return true;
	}
	
	/**
	 * JSF callback.
	 * @return A String.
	 */
	public String enter() {
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		return "navigationAbout";
	}

}
