package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.TemplateManager;
import org.esupportail.smsu.configuration.SmsuApplication;
import org.esupportail.smsu.web.beans.UITemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RolesAllowed("FCTN_GESTION_MODELES")
@RequestMapping(value = SmsuApplication.REST_ROOT_URI + "/templates")
public class TemplateManagerController {

	private static final int LENGHTBODY = 160;
	
	@Inject private TemplateManager templateManager;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@GetMapping
	@PermitAll
	public List<UITemplate> getTemplates() {
		return templateManager.getUITemplates();
	}
	 
	@PostMapping
	public void create(@RequestBody UITemplate uiTemplate) {
		createOrModify(uiTemplate, true);
	}

	@PutMapping("/{id:\\d+}")
	public void modify(@RequestBody UITemplate uiTemplate, @PathVariable("id") int id) {
		uiTemplate.id = id;
		createOrModify(uiTemplate, false);
	}

	@DeleteMapping("/{id:\\d+}")
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
