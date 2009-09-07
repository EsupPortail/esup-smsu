package org.esupportail.smsu.services.scheduler.job;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.purge.PurgePendingMember;
import org.esupportail.smsu.services.scheduler.AbstractQuartzJob;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the pending member table purge.
 * @author PRQD8824
 *
 */
public class PurgePendingMemberJob extends AbstractQuartzJob {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * The purge pending member bean name.
	 */
	private static final String PURGE_PENDING_MEMBER_BEAN_NAME = "purgePendingMember";
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext) {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("Launching Quartz task PurgePendingMemberJob now");
			logger.debug(sb.toString());
		}
		
		final PurgePendingMember purgePendingMember = (PurgePendingMember) applicationContext.getBean(PURGE_PENDING_MEMBER_BEAN_NAME);
		purgePendingMember.purgePendingMember();
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("End of Quartz task PurgePendingMemberJob");
			logger.debug(sb.toString());
		}
	}

}
