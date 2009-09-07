package org.esupportail.smsu.services.scheduler;

import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.context.ApplicationContextUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 
 * @author PRQD8824
 *
 */
public abstract class AbstractQuartzJob extends QuartzJobBean implements StatefulJob {

	/**
     * logger.
     */
	private final Logger logger = new LoggerImpl(getClass());
	
	 /**
     * Name of the spring application context bean name. 
     */
    private static final String APPLICATION_CONTEXT_BEAN_NAME = "applicationContext";
    
    /**
     * 
     */
    private static final String QUARTZ_EXCEPTION_HANDLER_BEAN_NAME = "quartzExceptionHandler";
    
    /**
     * Retrieves the spring application context from the job execution context.
     * 
     * @param context the quartz job execution context which is used to retrieve the spring application context bean.
     * 
     * @return the spring application context bean.
     */
    public ApplicationContext getApplicationContext(final JobExecutionContext context) throws JobExecutionException {
        SchedulerContext schedulerContext;
        try {
            final Scheduler scheduler = context.getScheduler();
            schedulerContext = scheduler.getContext();
        } catch (SchedulerException e) {
        	logger.error("Unable to retrieve the scheduler context, cause : ", e);
            throw new JobExecutionException(e);
        }
        final ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get(APPLICATION_CONTEXT_BEAN_NAME);
        return applicationContext;
    }
    
    /**
     * Retrieve the quartz exception handler bean.
     * @param context
     * @return
     */
    public QuartzExceptionHandler getQuartzExceptionHandler(final JobExecutionContext context) {
        QuartzExceptionHandler exceptionHandler = null;
        try {
            final ApplicationContext applicationContext = getApplicationContext(context);

            exceptionHandler = (QuartzExceptionHandler) applicationContext.getBean(QUARTZ_EXCEPTION_HANDLER_BEAN_NAME);

        } catch (JobExecutionException e) {
        	logger.error("An error occurs getting the Quartz exception Handler", e);
        }

        return exceptionHandler;
    }
    
	
	@Override
	protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		// get the application context, usefull the get bean spring
        final ApplicationContext applicationContext = getApplicationContext(context);
        // get the quartz exception handler (use to manage error in job)
        final QuartzExceptionHandler exceptionHandler = getQuartzExceptionHandler(context);

        try {
        	ApplicationContextUtils.initApplicationContext();
             // Open session & transaction (esup commons way)
        	DatabaseUtils.open();
        	DatabaseUtils.begin();

        	// do the job
        	executeJob(applicationContext);
        	// if everything ok, commit
        	DatabaseUtils.commit();
 
        } catch (Throwable t) {
        	// if an error occurs, rollback
        	DatabaseUtils.rollback();

        	logger.error("An exception occurred during the execute internal ", t);
            if (exceptionHandler != null) {
                exceptionHandler.process("Abstract Quartz job", t);
            } else {
                throw new UnsupportedOperationException("The exceptionHander has to be not null");
            }
        } finally {
        	DatabaseUtils.close();
        }
	}
	
	/**
	 * Job implementation.
	 * @param applicationContext
	 */
	protected abstract void executeJob(final ApplicationContext applicationContext);
}
