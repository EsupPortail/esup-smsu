package org.esupportail.smsu.services.scheduler.job;

import org.apache.log4j.Logger;
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
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * The periodic purge bean name.
	 */
	private static final String PERIODIC_PURGE_BEAN_NAME = "periodicPurge";
	
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("Launching Quartz task PeriodicPurgeJob now");
		}
		
		final PeriodicPurge periodicPurge = (PeriodicPurge) applicationContext.getBean(PERIODIC_PURGE_BEAN_NAME);
		periodicPurge.purge();
		
		if (logger.isDebugEnabled()) {
			logger.debug("End of Quartz task PeriodicPurgeJob");
		}

	}

}
