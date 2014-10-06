package org.esupportail.smsu.services.scheduler.job;

import org.apache.log4j.Logger;
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
	private final Logger logger = Logger.getLogger(getClass());
	
	private String beanName;

	@Override
	protected void executeJob(final ApplicationContext applicationContext) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Launching Quartz task NotificationByMailForInvalidPhoneJob now");
		}
		
		final NotificationByMailForInvalidPhoneManager notifForInvalidPhone = (NotificationByMailForInvalidPhoneManager) applicationContext.getBean(beanName);
		try {
			notifForInvalidPhone.sendMails();
		} catch (HttpException e) {
			logger.error(e);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("End of Quartz task NotificationByMailForInvalidPhoneJob");
		}
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
}
