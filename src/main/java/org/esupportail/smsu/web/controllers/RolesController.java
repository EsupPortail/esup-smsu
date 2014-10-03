package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.FonctionManager;
import org.esupportail.smsu.business.RoleManager;
import org.esupportail.smsu.web.beans.UIRole;
import org.springframework.beans.factory.annotation.Autowired;


@Path("/roles")
@RolesAllowed("FCTN_GESTION_ROLES_CRUD")
public class RolesController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());

	@Autowired private FonctionManager fonctionManager;
	@Autowired private RoleManager roleManager;

	@GET
	@Produces("application/json")
	@RolesAllowed({"FCTN_GESTION_ROLES_CRUD","FCTN_GESTION_ROLES_AFFECT"})
	public List<UIRole> getAllRoles() {
		return roleManager.getAllRoles();
	}
	
	@POST
	public void save(UIRole role) {
		roleManager.saveRole(role);
	}
	
	@PUT
	@Path("/{id:\\d+}")
	public void update(@PathParam("id") int id, UIRole role) {
		role.id = id;
		roleManager.updateRole(role);
	}

	@DELETE
	@Path("/{id:\\d+}")
	public void delete(@PathParam("id") int id)  {
		roleManager.deleteRole(id);
	}
	
	@GET
	@Produces("application/json")
	@Path("/fonctions")
	public List<String> allFonctions() {
		return fonctionManager.getAllFonctions();
	}
		
}
