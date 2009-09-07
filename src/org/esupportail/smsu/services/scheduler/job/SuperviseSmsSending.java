package org.esupportail.smsu.services.scheduler.job;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.services.scheduler.AbstractQuartzJob;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author PRQD8824
 *
 */
public class SuperviseSmsSending extends AbstractQuartzJob {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * The quartz job name associated (by defaults it is the application bean name).
	 */
	public static final String SUPERVISE_SMS_SENDING_JOB_NAME = "superviseSmsSendingJob";
	
	private static final String SEND_SMS_MANAGER_BEAN_NAME = "sendSmsManager";
	

	@Override
	protected void executeJob(final ApplicationContext applicationContext) {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("Launching Quartz task SuperviseSmsSending now");
			logger.debug(sb.toString());
		}
		
		final SendSmsManager sendSmsManager = (SendSmsManager) applicationContext.getBean(SEND_SMS_MANAGER_BEAN_NAME);
		
		sendSmsManager.sendWaitingForSendingMessage();

		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("End of Quartz task SuperviseSmsSending");
			logger.debug(sb.toString());
		}
	}

}
