package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.domain.beans.role.RoleEnum;
import org.esupportail.smsu.web.beans.UIRole;

/**
 * Business layer concerning smsu service.
 *
 */
public class RoleManager {
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;
	
	/**
	 * isDeletable.
	 */
	private Boolean isDeletable;
	
	/**
	 * isUpdateable.
	 */
	private Boolean isUpdateable;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public RoleManager() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Principal Methods
	//////////////////////////////////////////////////////////////
	/**
	 * retrieve all the roles defined in smsu database.
	 * Called by RolePaginator
	 * @param idRoles 
	 * @return list of uiRoles
	 */
	public List<UIRole> getAllRoles(final List<Integer> idRoles) {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieve the smsu roles from the database");
		}
		
		List<UIRole> allUIRoles = new ArrayList<UIRole>();
		List<Role> allRoles = daoService.getRoles();
		
		for (Role role : allRoles) {
			isDeletable = false;
			isUpdateable = true;
			
			Boolean testCustomizedGroup = testCustomizedGroupBeforeDeleteRole(role);
			
			// if not attached to Customized Groups
			if (testCustomizedGroup) {
				isDeletable = true;
			}
			
			// Role super admin (Id=1) n'est pas supprimable ni modifiable
			if (role.getName().equals(RoleEnum.SUPER_ADMIN.toString())) {
				isDeletable = false;
				isUpdateable = false;
			}
				
			// Role de l'utilisateur connect� n'est pas supprimable ni modifiable
			if (idRoles.contains(role.getId())) {
				isDeletable = false;
				isUpdateable = false;
			}
			
			UIRole newrole = new UIRole(role.getId(), role.getName(), isDeletable, isUpdateable);
			allUIRoles.add(newrole);
		}
		
		return allUIRoles;
	}
	
	/**
	 * retrieve all the roles defined in smsu database.
	 * Called by GroupsController.
	 * @return list of uiRoles
	 */
	public List<UIRole> getAllRoles() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieve the smsu roles from the database");
		}
		
		List<UIRole> allUIRoles = new ArrayList<UIRole>();
		List<Role> allRoles = daoService.getRoles();
		
		for (Role role : allRoles) {
			isDeletable = false;
			isUpdateable = true;
			
			Boolean testCustomizedGroup = testCustomizedGroupBeforeDeleteRole(role);
			
			// if not attached to Customized Groups
			if (testCustomizedGroup) {
				isDeletable = true;
			}
			
			// Role super admin (Id=1) n'est pas supprimable ni modifiable
			if (role.getName().equals(RoleEnum.SUPER_ADMIN.toString())) {
				isDeletable = false;
				isUpdateable = false;
			}
				
			UIRole newrole = new UIRole(role.getId(), role.getName(), isDeletable, isUpdateable);
			allUIRoles.add(newrole);
		}
		
		return allUIRoles;
	}
	
	/**
	 * Used by getAllRoles functions.
	 * @param role 
	 * @return true if no group is linked to 
	 */
	private Boolean testCustomizedGroupBeforeDeleteRole(final Role role) {
		CustomizedGroup customizedGroup = daoService.getCustomizedGroupByRole(role);
		
		if (customizedGroup == null) {
			return true;
		}
		
		return false;
	}

	/**
	 * save action.
	 * @param role 
	 * @param selectedValues
	 */
	public void saveRole(final UIRole role, final List<String> selectedValues) {
		Role newrole = new Role(role.getId(), role.getName().trim());
		Set<Fonction> fonctions = new HashSet<Fonction>();
		Integer fctId;
		
		for (String val : selectedValues) {
			fctId = (Integer) Integer.parseInt(val);
			fonctions.add(daoService.getFonctionById(fctId));
		    }
		newrole.setFonctions(fonctions);
        daoService.saveRole(newrole);
	}
	
	/**
	 * delete action.
	 * @param role 
	 */
	public void deleteRole(final UIRole role) {
		Role newrole = new Role(role.getId(), role.getName());
		daoService.deleteRole(newrole);
	}
	
	/**
	 * update action.
	 * @param role 
	 * @param selectedValues
	 */
	public void updateRole(final UIRole role, final List<String> selectedValues) {
		Role newrole = new Role(role.getId(), role.getName().trim());
		Set<Fonction> fonctions = new HashSet<Fonction>();
		Integer fctId;
		
		for (String val : selectedValues) {
			fctId = Integer.parseInt(val);
			fonctions.add(daoService.getFonctionById(fctId));
		    }
		newrole.setFonctions(fonctions);
		daoService.updateRole(newrole);
	}
	
	/**
	 * retreive Ids fonctions by role.
	 * @param role 
	 * @return Ids fonctions
	 */
	public List<String> getIdFctsByRole(final UIRole role) {
		Role newrole = new Role(role.getId(), role.getName());
		List<String> selectedValues = new ArrayList<String>();
		Set<Fonction> fonctions = daoService.getFctsByRole(newrole);
		for (Fonction fct : fonctions) {
	      selectedValues.add(fct.getId().toString());
	    }
		return selectedValues;
	}
	

	////////////////////////////////////////
	//  setter for spring object daoService
	///////////////////////////////////////
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}





}
