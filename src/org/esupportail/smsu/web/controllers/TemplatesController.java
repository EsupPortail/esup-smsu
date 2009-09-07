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
import org.esupportail.smsu.dao.beans.Template;

/**
 * A bean to manage user preferences.
 */
public class TemplatesController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2503649603430502319L;

	/**
	 * A logger.
	 */
	//private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * The id of the selected Template in "search_sms.jsp" page.
	 */	
	private String userTemplateId;
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public TemplatesController() {
		super();
	}	
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of userTemplateId
	//////////////////////////////////////////////////////////////
	/**
	 * A Getter method for userTemplateId parameter. 
	 */
	public String getUserTemplateId() {
		return this.userTemplateId;
	}
	
	/**
	 * @param String the templateId to setter
	 */
	public void setUserTemplateId(final String templateId) {
		this.userTemplateId = templateId;
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
	 * @return the userTemplateItems
	 */
	public List<SelectItem> getUserTemplateItems() {
		List<SelectItem> templateItems = new ArrayList<SelectItem>();
		templateItems.clear();
		templateItems.add(new SelectItem("0", ""));
		List<Template> templates = getDomainService().getTemplates();
		if (templates != null) {
		for (Template tpl : templates) {
			templateItems.add(new SelectItem(tpl.getId().toString(), tpl.getLabel()));
		}
		}
		return templateItems;
		
	}


	
}
