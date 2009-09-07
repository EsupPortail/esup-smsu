package org.esupportail.smsu.services.scheduler.job;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.purge.PeriodicPurge;
import org.esupportail.smsu.services.scheduler.AbstractQuartzJob;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the periodic purge.
 * @author PRQD8824
 *
 */
public class PeriodicPurgeJob extends AbstractQuartzJob {

	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * The periodic purge bean name.
	 */
	private static final String PERIODIC_PURGE_BEAN_NAME = "periodicPurge";
	
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext) {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("Launching Quartz task PeriodicPurgeJob now");
			logger.debug(sb.toString());
		}
		
		final PeriodicPurge periodicPurge = (PeriodicPurge) applicationContext.getBean(PERIODIC_PURGE_BEAN_NAME);
		periodicPurge.purge();
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("End of Quartz task PeriodicPurgeJob");
			logger.debug(sb.toString());
		}

	}

}
