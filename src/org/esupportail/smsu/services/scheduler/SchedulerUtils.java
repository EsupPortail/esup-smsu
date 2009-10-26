package org.esupportail.smsu.services.scheduler;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.services.scheduler.job.SuperviseSmsSending;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Provide tools to manage quartz tasks.
 * @author PRQD8824
 *
 */
public class SchedulerUtils {

	/**
     * logger.
     */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Quartz scheduler.
	 */
	private Scheduler scheduler;

	/**
	 * Use to launched a fire now trigger for supervise sms sending job.
	 */
	public void launchSuperviseSmsSending() {
		final String jobName = SuperviseSmsSending.SUPERVISE_SMS_SENDING_JOB_NAME;
		final String groupName = Scheduler.DEFAULT_GROUP;
		
		try {
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Launching job with parameter : \n");
				sb.append(" - jobName : ").append(jobName).append("\n");
				sb.append(" - groupName : ").append(groupName).append("\n");
				logger.debug(sb.toString());
			}
			
			scheduler.triggerJobWithVolatileTrigger(jobName, groupName);
			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Job successfully launched");
				logger.debug(sb.toString());
				
			}
		} catch (SchedulerException e) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("An error occurs launching the job with parameter : \n");
			sb.append(" - jobName : ").append(jobName).append("\n");
			sb.append(" - groupName : ").append(groupName).append("\n");
			
			logger.warn(sb.toString());
		}
	}
	
	
	/**
	 * Standard setter used by spring.
	 * @param scheduler
	 */
	public void setScheduler(final Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	
}
