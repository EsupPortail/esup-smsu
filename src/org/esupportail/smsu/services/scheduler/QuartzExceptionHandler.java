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
        	final StringBuilder sb = new StringBuilder(100);
        	sb.append("A DataAccessException occurred during the task [");
        	sb.append(taskTitle);
        	sb.append("] : ");
        	sb.append(exception.getClass());
        	sb.append(" - ");
        	sb.append(exception.getMessage());
            
        	logger.error(sb.toString(), exception);
            
        } else {
        	final StringBuilder sb = new StringBuilder(100);
        	sb.append("A DataAccessException occurred during the task [");
        	sb.append(taskTitle);
        	sb.append("] : ");
        	sb.append(exception.getClass());
        	
            logger.error(sb.toString(), exception);

        }

    }
}
