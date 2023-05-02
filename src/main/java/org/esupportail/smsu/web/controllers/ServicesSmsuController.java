package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.smsu.business.ServiceManager;
import org.esupportail.smsu.configuration.SmsuApplication;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.web.beans.UIService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = SmsuApplication.REST_ROOT_URI + "/services")
@RolesAllowed("FCTN_GESTION_SERVICES_CP")
public class ServicesSmsuController {

	@Inject private ServiceManager serviceManager;
	
	@GetMapping
	@PermitAll
	public List<UIService> getUIServices() {
		return serviceManager.getAllUIServices();
	}
	
	@GetMapping("/sendFctn")
	@PermitAll
	public List<UIService> getUIServicesSendFctn(HttpServletRequest request) {
		String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		return serviceManager.getUIServicesSendFctn(login, DomainService.getUserAllowedFonctions(request));
	}
	
	@GetMapping("/adhFctn")
	@PermitAll
	public List<UIService> getUIServicesAdhFctn(HttpServletRequest request) {
		String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		return serviceManager.getUIServicesAdhFctn(login, DomainService.getUserAllowedFonctions(request));
	}
	
	@PostMapping
	public void create(@RequestBody UIService uiService) {
		createOrModify(uiService, true);
	}

	@PutMapping("/{id:\\d+}")
	public void modify(@RequestBody UIService uiService, @PathVariable("id") int id) {
		uiService.id = id;
		createOrModify(uiService, false);
	}

	@DeleteMapping("/{id:\\d+}")
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
