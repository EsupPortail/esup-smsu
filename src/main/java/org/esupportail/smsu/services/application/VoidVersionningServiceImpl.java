package org.esupportail.smsu.services.application;

import org.esupportail.commons.services.application.VersionException;
import org.esupportail.commons.services.application.VersionningService;
import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.web.controllers.AbstractDomainAwareBean;

@SuppressWarnings("serial")
public class VoidVersionningServiceImpl extends AbstractDomainAwareBean implements VersionningService{
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	public void checkVersion(boolean throwException, boolean printLatestVersion)
			throws VersionException {
		// rien
		
	}

	public void initDatabase() {
		DatabaseUtils.create();
		logger.info("the database has been created.");
		
	}

	public boolean upgradeDatabase() {
		// rien
		return false;
	}
}
