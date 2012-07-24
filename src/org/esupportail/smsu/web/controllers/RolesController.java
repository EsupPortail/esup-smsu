package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.web.beans.RolePaginator;
import org.esupportail.smsu.web.beans.UIFonction;
import org.esupportail.smsu.web.beans.UIRole;

/**
 * A bean to manage files.
 */
public class RolesController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1149078913806276304L;
	
	/**
	 * The role.
	 */
	private UIRole role;
	
	/**
	 * The role paginator.
	 */
	private RolePaginator paginator;
		
	/**
	 * list of the fonctions.
	 */
	private List<Fonction> allFonctions;
	
	/**
	 * list of the fonctions.
	 */
	private List<UIFonction> allBundleFonctions;
	
	/**
	 * list of the selected fonctions.
	 */
	private List<String> selectedValues; 
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public RolesController() {
		super();
	}
	

	//////////////////////////////////////////////////////////////
	// Acces control method
	//////////////////////////////////////////////////////////////
	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		//an access control is required for this page.
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return currentUser.hasFonction(FonctionName.FCTN_GESTION_ROLES_CRUD);
	}
	
	//////////////////////////////////////////////////////////////
	// Enter method (for Initialazation)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 * @throws LdapUserNotFoundException 
	 */
	public String enter()  {
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		// initialize data in the page
		init();
		return "navigationAdminRoles";
	}
	
	//////////////////////////////////////////////////////////////
	// Init method
	//////////////////////////////////////////////////////////////
	/**
	 * initialize data in the page.
	 * @throws LdapUserNotFoundException 
	 */
	private void init()  {
		User currentUser = getCurrentUser();
		if (currentUser != null) {
		logger.debug("Current user is not null");	
		paginator = new RolePaginator(getDomainService(), currentUser.getRoles());
		} else {
		logger.debug("Current user is null");	
		paginator = new RolePaginator(getDomainService());
		}
		
		this.allFonctions = getDomainService().getAllFonctions();
		
		initFunctionsUsingBundles();
		logger.debug("ici 7");
	}

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * save action.
	 * @return A String
	 */
	public String save() {
		getDomainService().saveRole(role, this.selectedValues);
		return "navigationAdminRoles";
	}
	
	/**
	 * update action.
	 * @return A String
	 */
	public String update() {
		getDomainService().updateRole(role, this.selectedValues);
		return "navigationAdminRoles";
	}
	
	/**
	 * delete action and reload paginator.
	 * @return A String
	 */
	public String delete()  {
		getDomainService().deleteRole(role);
		reset();
		return "navigationAdminRoles";
	}
	
	/**
	 * create action.
	 * @return A String
	 */
	public String create() {
		this.role = new UIRole();
		this.selectedValues = new ArrayList<String>();
		return "navigationCreateRole";
	}
	
	public String display() {
		selectedValues = getDomainService().getIdFctsByRole(role);
		return "navigationDetailRole";
	}
	
	//////////////////////////////////////////////////////////////
	// Others
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractContextAwareController#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		User currentUser = getCurrentUser();
		if (currentUser != null) {
		paginator = new RolePaginator(getDomainService(), currentUser.getRoles());
		} else {
		paginator = new RolePaginator(getDomainService());
		}
	}
	
	private void initFunctionsUsingBundles() {
		this.allBundleFonctions = new ArrayList<UIFonction>();
		
		// Init Fonctions using Bundle
		if (this.allFonctions != null) {
			for (Fonction fct : this.allFonctions) {
				this.allBundleFonctions.add(toUIFonction(fct));
			}
		}
	}

	private UIFonction toUIFonction(Fonction fct) {
		String msg = getI18nService().getString("MSG." + fct.getName().trim(), 
							getI18nService().getDefaultLocale());			
		return new UIFonction(fct.getId().toString(), msg);
	}
	
	/**
	 * For treatments.
	 * @return the paginator
	 */
	public RolePaginator getPaginator() {
		return paginator;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of role
	//////////////////////////////////////////////////////////////
    /**
	 *  the role to get.
	 */
	public UIRole getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(final UIRole role) {
		this.role = role;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter of allFonctions
	//////////////////////////////////////////////////////////////
	/**
	 * @return the allFonctions
	 */
	public List<Fonction> getAllFonctions() {
		return allFonctions;
	}

	//////////////////////////////////////////////////////////////
	// Getter of allBundleFonctions
	//////////////////////////////////////////////////////////////
	/**
	 * @return the allBundleFonctions
	 */
	public List<UIFonction> getAllBundleFonctions() {
		return allBundleFonctions;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of selected values
	//////////////////////////////////////////////////////////////
	/**
	 * @param selectedFonctions the selectedFonctions to set
	 */
	public void setSelectedValues(final List<String> values) {
		this.selectedValues = values;
	}

	/**
	 * @return the selectedFonctions
	 */
	public List<String> getSelectedValues() {
			return selectedValues;
	}
	
	
}
