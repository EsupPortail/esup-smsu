package org.esupportail.smsu.web.controllers;

import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.web.beans.ServicesPaginator;
import org.esupportail.smsu.web.beans.UIService;

/**
 * @author xphp8691
 *
 */
public class ServicesSmsuController extends AbstractContextAwareController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5905000404632042893L;

	/**
	 * The services paginator.
	 */
	private ServicesPaginator paginator;

	/**
	 * a service.
	 */
	private UIService uiService;

    //////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * a constructor.
	 */
	public ServicesSmsuController() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Access Management
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
		return currentUser.hasFonction(FonctionName.FCTN_GESTION_SERVICES_CP);
	}

	//////////////////////////////////////////////////////////////
	// Enter method (for Initialazation)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 */
	public String enter() {
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}

		init();

		return "navigationAdminService";
	}

	//////////////////////////////////////////////////////////////
	// Init methods 
	//////////////////////////////////////////////////////////////
	/**
	 * initialize the page.
	 */
	private void init() {
		//can be used to initialize the page.
		paginator = new ServicesPaginator(getDomainService());
	}

	//////////////////////////////////////////////////////////////
	// Principal methods 
	//////////////////////////////////////////////////////////////
	/**
	 * @return a navigation rule.
	 */
	public String delete() {

		getDomainService().deleteUIService(uiService);
		init();
		return null;
	}

	/**
	 * @return a navigation rule.
	 */
	public String save() {

		String key = uiService.getKey();
		String name = uiService.getName();
		Integer id = uiService.getId();

		Boolean bServiceOk = true;

		if (!getDomainService().isServiceKeyAvailable(key, id)) {
			addErrorMessage("createModifyServiceForm:serviceKey", "SERVICE.KEY.ERROR");
			bServiceOk = false;
		}

		if (!getDomainService().isServiceNameAvailable(name, id)) {
			addErrorMessage("createModifyServiceForm:serviceName", "SERVICE.NAME.ERROR");
			bServiceOk = false;
		}

		if (bServiceOk) {
			if (uiService.getId() == null) {
				getDomainService().addUIService(uiService);
			} else {
				getDomainService().updateUIService(uiService);
			}
			return "navigationAdminService";
		}

		return null;
	}


	/**
	 * @return navigationCreateService
	 */
	public String createServiceButton() {

		//the service is initialized.
		uiService = new UIService();
		//the creation page is displayed.
		return "navigationCreateService";
	}

	//////////////////////////////////////////////////////////////
	// Others 
	//////////////////////////////////////////////////////////////
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of paginator
	//////////////////////////////////////////////////////////////
    /**
	 * @param paginator
	 */
	public void setPaginator(final ServicesPaginator paginator) {
		this.paginator = paginator;
	}

	/**
	 * @return paginator.
	 */
	public ServicesPaginator getPaginator() {
		return paginator;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of uiService
	//////////////////////////////////////////////////////////////
	/**
	 * @param uiService 
	 */
	public void setUiService(final UIService uiService) {
		this.uiService = uiService;
	}

	/**
	 * @return service
	 */
	public UIService getUiService() {
		return uiService;
	}




}
