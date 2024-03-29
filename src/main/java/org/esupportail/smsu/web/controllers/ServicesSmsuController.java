package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.esupportail.smsu.business.ServiceManager;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.web.beans.UIService;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/services")
@RolesAllowed("FCTN_GESTION_SERVICES_CP")
public class ServicesSmsuController {

	@Inject private ServiceManager serviceManager;
	
	@RequestMapping(method = RequestMethod.GET)
	@PermitAll
	public List<UIService> getUIServices() {
		return serviceManager.getAllUIServices();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/sendFctn")
	@PermitAll
	public List<UIService> getUIServicesSendFctn(HttpServletRequest request) {
		String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		return serviceManager.getUIServicesSendFctn(login, DomainService.getUserAllowedFonctions(request));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/adhFctn")
	@PermitAll
	public List<UIService> getUIServicesAdhFctn(HttpServletRequest request) {
		String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		return serviceManager.getUIServicesAdhFctn(login, DomainService.getUserAllowedFonctions(request));
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void create(@RequestBody UIService uiService) {
		createOrModify(uiService, true);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}")
	public void modify(@RequestBody UIService uiService, @PathVariable("id") int id) {
		uiService.id = id;
		createOrModify(uiService, false);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id:\\d+}")
	public void delete(@PathVariable("id") int id) {
		serviceManager.deleteUIService(id);
	}


	private void createOrModify(UIService uiService, boolean isAddMode) {
		Integer id = isAddMode ? null : uiService.id;
		if (!serviceManager.isKeyAvailable(uiService.key, id)) {
			throw new InvalidParameterException("SERVICE.KEY.ERROR");
		}

		if (!serviceManager.isNameAvailable(uiService.name, id)) {
			throw new InvalidParameterException("SERVICE.NAME.ERROR");
		}

			if (isAddMode) {
				serviceManager.addUIService(uiService);
			} else {
				serviceManager.updateUIService(uiService);
			}
	}

}
