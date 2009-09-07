package org.esupportail.smsu.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;

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
	

	/**
	 * provides tools to manage db.
	 */
	private DaoService daoService;

	
	
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
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Launching periodic purge with parameter : \n");
			sb.append(" - seniority date : ").append(seniorityDate);
			logger.debug(sb.toString());
		}

		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Start the message purge");
			logger.trace(sb.toString());
		}

		daoService.deleteMessageOlderThan(seniorityDate);

		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of the message purge\n");
			sb.append("Start the orphan mail purge");
			logger.trace(sb.toString());
		}

		daoService.deleteOrphanMail();

		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of the orphan mail purge\n");
			sb.append("Start the orphan recipient purge");
			logger.trace(sb.toString());
		}

		daoService.deleteOrphanRecipient();

		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of the orphan recipient purge\n");
			sb.append("Start the orphan mail recipient purge");
			logger.trace(sb.toString());
		}

		daoService.deleteOrphanMailRecipient();

		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of the orphan mail recipient purge\n");
			sb.append("Start the orphan person purge");
			logger.trace(sb.toString());
		}

		daoService.deleteOrphanPerson();
		
		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of the orphan person purge\n");
			sb.append("Start the orphan basic group purge");
			logger.trace(sb.toString());
		}

		daoService.deleteOrphanBasicGroup();
		
		if (logger.isTraceEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of the orphan basic group purge\n");
			logger.trace(sb.toString());
		}
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End of periodic purge");
			logger.debug(sb.toString());
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
