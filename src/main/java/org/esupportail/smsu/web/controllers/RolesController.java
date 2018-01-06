package org.esupportail.smsu.web.controllers;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.FonctionManager;
import org.esupportail.smsu.business.RoleManager;
import org.esupportail.smsu.web.beans.UIRole;
import org.springframework.beans.factory.annotation.Autowired;


@RequestMapping(value = "/roles")
@RolesAllowed("FCTN_GESTION_ROLES_CRUD")
public class RolesController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());

	@Autowired private FonctionManager fonctionManager;
	@Autowired private RoleManager roleManager;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@RolesAllowed({"FCTN_GESTION_ROLES_CRUD","FCTN_GESTION_ROLES_AFFECT"})
	public List<UIRole> getAllRoles() {
		return roleManager.getAllRoles();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void save(@RequestBody UIRole role) {
		roleManager.saveRole(role);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}")
	public void update(@PathVariable("id") int id, @RequestBody UIRole role) {
		role.id = id;
		roleManager.updateRole(role);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id:\\d+}")
	public void delete(@PathVariable("id") int id)  {
		roleManager.deleteRole(id);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fonctions")
	@ResponseBody
	public Set<String> allFonctions() {
		return fonctionManager.getAllFonctions();
	}
		
}
