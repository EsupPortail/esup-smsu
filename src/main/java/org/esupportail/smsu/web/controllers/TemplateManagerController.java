package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.TemplateManager;
import org.esupportail.smsu.web.beans.UITemplate;
import org.springframework.beans.factory.annotation.Autowired;

@RolesAllowed("FCTN_GESTION_MODELES")
@RequestMapping(value = "/templates")
public class TemplateManagerController {

	private static final int LENGHTBODY = 160;
	
	@Autowired private TemplateManager templateManager;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@PermitAll
	public List<UITemplate> getTemplates() {
		return templateManager.getUITemplates();
	}
	 
	@RequestMapping(method = RequestMethod.POST)
	public void create(UITemplate uiTemplate) {
		createOrModify(uiTemplate, true);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}")
	public void modify(UITemplate uiTemplate, @PathVariable("id") int id) {
		uiTemplate.id = id;
		createOrModify(uiTemplate, false);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id:\\d+}")
	public void delete(@PathVariable("id") int id) {
		templateManager.deleteTemplate(id);
	}
	
	private void createOrModify(UITemplate uiTemplate, boolean isAddMode) {
		String body = uiTemplate.body;
		if (body.length() > LENGHTBODY) {
			logger.error("Error lenght Body: " + body.length() + " longer than: " + LENGHTBODY);
			throw new InvalidParameterException("TEMPLATE.BODY.ERROR");
		}
		if (!templateManager.isLabelAvailable(uiTemplate.label, uiTemplate.id)) {
			throw new InvalidParameterException("TEMPLATE.LABEL.ERROR");
		}
		
			if (isAddMode) {
				templateManager.addUITemplate(uiTemplate);
			} else {
				templateManager.updateUITemplate(uiTemplate);
			}
	}

}
