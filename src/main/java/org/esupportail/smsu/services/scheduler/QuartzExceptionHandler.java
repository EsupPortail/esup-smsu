package org.esupportail.smsu.services.scheduler;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.springframework.dao.DataAccessException;

/**
 * Exception handler for quartz error.
 * @author PRQD8824
 *
 */
public class QuartzExceptionHandler {

	/**
     * logger.
     */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * 
	 * @param taskTitle
	 * @param exception
	 */
    public void process(final String taskTitle, final Throwable exception) {
        
        if (exception instanceof DataAccessException) {
        	logger.error("A DataAccessException occurred during the task [" + taskTitle + "] : " + 
			     exception.getClass() + " - " + exception.getMessage(), exception);
        } else {
		logger.error("A DataAccessException occurred during the task [" + taskTitle + "] : " + 
			     exception.getClass(), exception);
        }

    }
}
