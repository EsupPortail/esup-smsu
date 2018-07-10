package org.esupportail.smsu.services.scheduler;

import org.apache.log4j.Logger;
import org.esupportail.smsu.services.scheduler.job.SuperviseSmsSending;
import org.quartz.JobKey;
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
	private final Logger logger = Logger.getLogger(getClass());
	
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
				logger.debug("Launching job with parameter : \n" + 
					     " - jobName : " + jobName + "\n" + 
					     " - groupName : " + groupName + "\n");
			}
			
			scheduler.triggerJob(new JobKey(jobName, groupName));
			
			if (logger.isDebugEnabled()) {
				logger.debug("Job successfully launched");
				
			}
		} catch (SchedulerException e) {
			logger.warn("An error occurs launching the job with parameter : \n" + 
				    " - jobName : " + jobName + "\n" +
				    " - groupName : " + groupName + "\n");
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
