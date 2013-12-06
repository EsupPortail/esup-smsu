package org.esupportail.smsu.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class manages the periodic purge.
 * @author PRQD8824
 *
 */
public class PeriodicPurge {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	
	/**
	 * Message seniority.
	 */
	private int seniorityDay;
	
	@Autowired private DaoService daoService;

	
	/**
	 * launch the periodic purge. 
	 */
	public void purge() {
		// compute de limite date
		final long currentTimeInMillis = System.currentTimeMillis();
		final Calendar seniorityDateAsCal = new GregorianCalendar();
		seniorityDateAsCal.setTimeInMillis(currentTimeInMillis);
		
		seniorityDateAsCal.add(Calendar.DAY_OF_YEAR, -seniorityDay);
		
		final Date seniorityDateAsDate = seniorityDateAsCal.getTime();
		
		purgeWithSeniorityDate(seniorityDateAsDate);
	}
	
	
	/**
	 * 
	 * @param seniorityDate
	 */
	private void purgeWithSeniorityDate(final Date seniorityDate) {

		if (logger.isDebugEnabled()) {
			logger.debug("Launching periodic purge with parameter : \n" + " - seniority date : " + seniorityDate);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Start the message content purge");
		}

		daoService.deleteMessageContentOlderThan(seniorityDate);

		if (logger.isTraceEnabled()) {
			logger.trace("End of the message content purge\n" + "Start the orphan mail purge");
		}

		daoService.deleteOrphanMail();

		if (logger.isTraceEnabled()) {
			logger.trace("End of the orphan mail purge\n" + "Start the orphan recipient purge");
		}

		daoService.deleteOrphanRecipient();

		if (logger.isTraceEnabled()) {
			logger.trace("End of the orphan recipient purge\n" + "Start the orphan mail recipient purge");
		}

		daoService.deleteOrphanMailRecipient();

		if (logger.isTraceEnabled()) {
			logger.trace("End of the orphan mail recipient purge\n" + "Start the orphan person purge");
		}

		daoService.deleteOrphanPerson();
		
		if (logger.isTraceEnabled()) {
			logger.trace("End of the orphan person purge\n" + "Start the orphan basic group purge");
		}

		daoService.deleteOrphanBasicGroup();
		
		if (logger.isTraceEnabled()) {
			logger.trace("End of the orphan basic group purge\n");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("End of periodic purge");
		}
	}
	
	
	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
	
	
	/**
	 * Standard setter used by spring.
	 * @param seniorityDay
	 */
	public void setSeniorityDay(final int seniorityDay) {
		this.seniorityDay = seniorityDay;
	}

}
