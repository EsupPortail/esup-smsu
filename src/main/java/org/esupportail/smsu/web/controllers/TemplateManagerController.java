package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.TemplateManager;
import org.esupportail.smsu.web.beans.UITemplate;
import org.springframework.beans.factory.annotation.Autowired;

@RolesAllowed("FCTN_GESTION_MODELES")
@Path("/templates")
public class TemplateManagerController {

	private static final int LENGHTBODY = 160;
	
	@Autowired private TemplateManager templateManager;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@GET
	@Produces("application/json")
	@PermitAll
	public List<UITemplate> getTemplates() {
		return templateManager.getUITemplates();
	}
	 
	@POST
	public void create(UITemplate uiTemplate) {
		createOrModify(uiTemplate, true);
	}

	@PUT
	@Path("/{id:\\d+}")
	public void modify(UITemplate uiTemplate, @PathParam("id") int id) {
		uiTemplate.id = id;
		createOrModify(uiTemplate, false);
	}

	@DELETE
	@Path("/{id:\\d+}")
	public void delete(@PathParam("id") int id) {
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
