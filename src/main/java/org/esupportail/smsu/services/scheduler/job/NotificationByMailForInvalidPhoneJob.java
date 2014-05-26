package org.esupportail.smsu.services.scheduler.job;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.NotificationByMailForInvalidPhoneManager;
import org.esupportail.smsu.services.scheduler.AbstractQuartzJob;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the pending member table purge.
 * @author PRQD8824
 *
 */
public class NotificationByMailForInvalidPhoneJob extends AbstractQuartzJob {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Launching Quartz task NotificationByMailForInvalidPhoneJob now");
		}
		
		final NotificationByMailForInvalidPhoneManager notifForInvalidPhone = (NotificationByMailForInvalidPhoneManager) applicationContext.getBean("notificationByMailForInvalidPhoneManager");
		try {
			notifForInvalidPhone.sendMails();
		} catch (HttpException e) {
			logger.error(e);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("End of Quartz task NotificationByMailForInvalidPhoneJob");
		}
	}

}
