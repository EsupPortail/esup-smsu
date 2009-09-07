/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

//import org.esupportail.commons.services.logging.Logger;
//import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Account;
/**
 * A bean to manage user preferences.
 */
public class AccountsController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2503649603430502319L;

	/**
	 * A logger.
	 */
	//private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * The id of the selected account in "search_sms.jsp" page.
	 */	
	private String userAccountId;
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public AccountsController() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of userAccountId
	//////////////////////////////////////////////////////////////
	
	/**
	 * A Getter method for userAccountId parameter. 
	 */
	public String getUserAccountId() {
		return this.userAccountId;
	}
	
	/**
	 * @param String the accountId to setter
	 */
	public void setUserAccountId(final String accountId) {
		this.userAccountId = accountId;
	}
	
	
	//////////////////////////////////////////////////////////////
	// Others
	//////////////////////////////////////////////////////////////
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		return getCurrentUser() != null;
	}

	/**
	 * @return the userAccountItems
	 */
	public List<SelectItem> getUserAccountItems() {
		List<SelectItem> accountItems = new ArrayList<SelectItem>();
		accountItems.clear();
		accountItems.add(new SelectItem("0", ""));
		List<Account> accounts = getDomainService().getAccounts();
		if (accounts != null) {
		for (Account acc : accounts) {
			accountItems.add(new SelectItem(acc.getId().toString(), acc.getLabel()));
		}
		}
		return accountItems;
	}

	
}
