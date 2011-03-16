package org.esupportail.smsu.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;

/**
 *  Manage the purge in table PendingMember.
 * @author PRQD8824
 *
 */
public class PurgePendingMember {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());


	/**
	 * Maximum day of sms seniority.
	 */
	private int seniorityDay;
	
	/**
	 * Used to manage db.
	 */
	private DaoService daoService;
	
	

	/**
	 * 
	 */
	public void purgePendingMember() {
		// compute de limite date
		final long currentTimeInMillis = System.currentTimeMillis();
		final Calendar seniorityDateAsCal = new GregorianCalendar();
		seniorityDateAsCal.setTimeInMillis(currentTimeInMillis);
		
		seniorityDateAsCal.add(Calendar.DAY_OF_YEAR, -seniorityDay);
		
		final Date seniorityDateAsDate = seniorityDateAsCal.getTime();
		
		purgePendingMemberOlderThan(seniorityDateAsDate);
	}
	
	/**
	 * Purge the pending member in db with date older than the specified date.
	 * @param date
	 */
	private void purgePendingMemberOlderThan(final Date date) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Starting purge of pending member table with parameter : \n" + 
				     " - date : " + date);
		}
		
		final int nbSmsDeleted = daoService.deletePendingMemberOlderThan(date);
		
		if (logger.isDebugEnabled()) {
			logger.debug("End purge of pending member table, result : \n" +
				     " - number of pending member deleted : " + nbSmsDeleted);
		}
	}
	
	/***********
	 * Mutator
	 */
	
	
	/**
	 * Standard setter used by spring.
	 * @param seniorityDay
	 */
	public void setSeniorityDay(final int seniorityDay) {
		this.seniorityDay = seniorityDay;
	}
	

	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
}
