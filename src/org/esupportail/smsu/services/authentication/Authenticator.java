package org.esupportail.smsu.services.authentication;

import org.esupportail.smsu.domain.beans.User;

/**
 * The interface of authenticators.
 */
public interface Authenticator {

	/**
	 * @return the authenticated user.
	 */
	User getUser();

}