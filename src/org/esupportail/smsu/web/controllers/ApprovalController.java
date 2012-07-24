package org.esupportail.smsu.web.controllers;

import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.beans.ApprovalPaginator;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

/**
 * A bean to manage files.
 */
public class ApprovalController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1149078913806276304L;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * The approval paginator.
	 */
	private ApprovalPaginator paginator;
		
	/**
	 * The message.
	 */
	 private UIMessage message;
				 
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public ApprovalController() {
		super();
	}
	
	
	//////////////////////////////////////////////////////////////
	// Init method
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
		return currentUser.hasFonction(FonctionName.FCTN_GESTIONS_RESPONSABLES)
		    || getDomainService().isSupervisor(currentUser);
	}
	
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
		return "navigationApproveSMS";
	}
	
	/**
	 * initialize data in the page.
	 * @throws LdapUserNotFoundException 
	 */
	private void init()  {
		paginator = new ApprovalPaginator(getDomainService(), getCurrentUser());		
	}

	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractContextAwareController#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		paginator = new ApprovalPaginator(getDomainService(), getCurrentUser());
	}

	private String getCurrentUserId() {
		User currentUser = getCurrentUser();
		return currentUser == null ? null : currentUser.getId();
	}

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * For treatments.
	 * @return the paginator
	 */
	public ApprovalPaginator getPaginator() {
		return paginator;
	}

	/**
	 * validate action and reload paginator.
	 * @return A String
	 */
	public String validate()  {
		logger.info("" + getCurrentUserId() + " approve message " + message);
		try {
			getDomainService().treatUIMessage(message);
			reset();
			return "navigationApproveSMS";
		} catch (CreateMessageException e) {
			addFormattedError(null, e.toI18nString(getI18nService()));
			return null;
		}

	}
	
	/**
	 * cancel action and reload paginator.
	 * @return A String
	 */
	public String cancel()  {
		getDomainService().updateUIMessage(message);
		reset();
		return "navigationApproveSMS";
	}
	
	//////////////////////////////////////////////////////////////
	// Setter and Getter for message
	//////////////////////////////////////////////////////////////
	/**
	 * @param message the message to set
	 */
	public void setMessage(final UIMessage message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public UIMessage getMessage() {
		return message;
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

	
	
	
}
