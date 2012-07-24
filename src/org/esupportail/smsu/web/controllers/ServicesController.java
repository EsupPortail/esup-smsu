/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

//import org.esupportail.commons.services.logging.Logger;
//import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.smsu.dao.beans.Service;

/**
 * A bean to manage user preferences.
 */
public class ServicesController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2503649603430502319L;

	/**
	 * const.
	 */
	private static final String CS_NONE = "none";
	
	/**
	 * A logger.
	 */
	//private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * The id of the selected group in "search_sms.jsp" page.
	 */	
	private String userServiceId;
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public ServicesController() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of userServiceId
	//////////////////////////////////////////////////////////////
	/**
	 * A Getter method for userServiceId parameter. 
	 */
	public String getUserServiceId() {
		return this.userServiceId;
	}
	
	/**
	 * @param serviceId 
	 * @param String the serviceId to setter
	 */
	public void setUserServiceId(final String serviceId) {
		this.userServiceId = serviceId;
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
	 * @return the userServiceItems
	 */
	public List<SelectItem> getUserServiceItems() {
		List<SelectItem> serviceItems = new ArrayList<SelectItem>();
		serviceItems.clear();
		serviceItems.add(new SelectItem("0", ""));
		
		I18nService tradService = getI18nService();
		String noneLabel = tradService.getString("SENDSMS.LABEL.NONE");
		serviceItems.add(new SelectItem(CS_NONE, noneLabel));
		
		List<Service> services = getDomainService().getServices();
		if (services != null) {
		for (Service srv : services) {
			serviceItems.add(new SelectItem(srv.getId().toString(), srv.getName()));
		}
		}
		return serviceItems;
	}


	
}
