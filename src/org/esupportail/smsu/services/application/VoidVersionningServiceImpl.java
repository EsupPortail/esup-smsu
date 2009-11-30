package org.esupportail.smsu.services.application;

import org.esupportail.commons.services.application.VersionException;
import org.esupportail.commons.services.application.VersionningService;
import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.web.controllers.AbstractDomainAwareBean;

public class VoidVersionningServiceImpl extends AbstractDomainAwareBean implements VersionningService{
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * The id of the first administrator.
	 */
	private String firstAdministratorId;
	
	public void checkVersion(boolean throwException, boolean printLatestVersion)
			throws VersionException {
		// rien
		
	}

	public void initDatabase() {
		DatabaseUtils.create();
		logger.info("creating the first user of the application thanks to " 
				+ getClass().getName() + ".firstAdministratorId...");
		User firstAdministrator = getDomainService().getUser(firstAdministratorId);
		getDomainService().addAdmin(firstAdministrator);
		logger.info("the database has been created.");
		
	}

	public boolean upgradeDatabase() {
		// rien
		return false;
	}
	
	/**
	 * @return the firstAdministratorId
	 */
	public String getFirstAdministratorId() {
		return firstAdministratorId;
	}

	/**
	 * @param firstAdministratorId the firstAdministratorId to set
	 */
	public void setFirstAdministratorId(final String firstAdministratorId) {
		this.firstAdministratorId = firstAdministratorId;
	}
}
