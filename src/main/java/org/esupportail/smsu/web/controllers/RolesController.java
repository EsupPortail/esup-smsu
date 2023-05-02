package org.esupportail.smsu.web.controllers;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.FonctionManager;
import org.esupportail.smsu.business.RoleManager;
import org.esupportail.smsu.configuration.SmsuApplication;
import org.esupportail.smsu.web.beans.UIRole;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = SmsuApplication.REST_ROOT_URI + "/roles")
@RolesAllowed("FCTN_GESTION_ROLES_CRUD")
public class RolesController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());

	@Inject private FonctionManager fonctionManager;
	@Inject private RoleManager roleManager;

	@GetMapping
	@RolesAllowed({"FCTN_GESTION_ROLES_CRUD","FCTN_GESTION_ROLES_AFFECT"})
	public List<UIRole> getAllRoles() {
		return roleManager.getAllRoles();
	}
	
	@PostMapping
	public void save(@RequestBody UIRole role) {
		roleManager.saveRole(role);
	}
	
	@PutMapping("/{id:\\d+}")
	public void update(@PathVariable("id") int id, @RequestBody UIRole role) {
		role.id = id;
		roleManager.updateRole(role);
	}

	@DeleteMapping("/{id:\\d+}")
	public void delete(@PathVariable("id") int id)  {
		roleManager.deleteRole(id);
	}
	
	@GetMapping("/fonctions")
	public Set<String> allFonctions() {
		return fonctionManager.getAllFonctions();
	}
		
}
