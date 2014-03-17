package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Role;
import org.esupportail.smsu.domain.beans.role.RoleEnum;
import org.esupportail.smsu.web.beans.UIRole;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Business layer concerning smsu service.
 *
 */
public class RoleManager {
	
	@Autowired private DaoService daoService;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * retrieve all the roles defined in smsu database.
	 * @return list of uiRoles
	 */
	public List<UIRole> getAllRoles() {
		logger.debug("Retrieve the smsu roles from the database");
		List<UIRole> allUIRoles = new ArrayList<UIRole>();
		for (Role role : daoService.getRoles()) {
			allUIRoles.add(convertToUI(role));
		}
		return allUIRoles;
	}

	/**
	 * save action.
	 */
	public void saveRole(final UIRole role) {
		daoService.saveRole(convertFromUI(role, true));
	}
	
	/**
	 * delete action.
	 */
	public void deleteRole(int id) {
		daoService.deleteRole(daoService.getRoleById(id));
	}
	
	/**
	 * update action.
	 */
	public void updateRole(final UIRole uiRole) {
		Role role = convertFromUI(uiRole, false);
		Role persistent = daoService.getRoleById(role.getId());
		persistent.setName(role.getName());
		persistent.setFonctions(role.getFonctions());
		daoService.updateRole(persistent);
	}
	
	private Role convertFromUI(UIRole uiRole, boolean isAddMode) {
		Role result = new Role();
		if (!isAddMode) {
			result.setId(uiRole.id);
		}
		result.setName(uiRole.name);
		result.setFonctions(stringNamesToFonctions(uiRole.fonctions));
		return result;
	}
	
	private Fonction stringNameToFonction(String val) {
		return daoService.getFonctionByName(val);
	}

	private Set<Fonction> stringNamesToFonctions(List<String> selectedValues) {
		Set<Fonction> fonctions = new HashSet<Fonction>();		
		for (String val : selectedValues) {
			fonctions.add(stringNameToFonction(val));
		}
		return fonctions;
	}

	private UIRole convertToUI(Role role) {
		boolean isDeletable = !daoService.isRoleInUse(role); // if not attached to Customized Groups
		boolean isUpdateable = true;

		// Role super admin (Id=1) et utilisateur connected n'est pas supprimable ni modifiable
		if (role.getName().equals(RoleEnum.SUPER_ADMIN.toString())) {
			isDeletable = false;
			isUpdateable = false;
		}
					
		UIRole result = new UIRole();
		result.id = role.getId();
		result.name = role.getName();
		result.fonctions = fonctionsToStringNames(role.getFonctions());
		result.isDeletable = isDeletable;
		result.isUpdateable = isUpdateable;
		return result;
	}

	private List<String> fonctionsToStringNames(Set<Fonction> fonctions) {
		List<String> selectedValues = new ArrayList<String>();
		for (Fonction fct : fonctions) {
			if (FonctionManager.toFonctionName(fct.getName()) != null) // filter obsolete Fonctions (eg: FCTN_APPROBATION_ENVOI)
				selectedValues.add(fct.getName());
		}
		return selectedValues;
	}
	
}
