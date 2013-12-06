package org.esupportail.smsu.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 *  Manage the purge in table PendingMember.
 *
 */
public class PurgePendingMember {

	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * Maximum day of sms seniority.
	 */
	private int seniorityDay;
	
	@Autowired private DaoService daoService;
	

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
		
			logger.debug("Starting purge of pending member table with parameter : \n" + 
				     " - date : " + date);
		
		final int nbSmsDeleted = daoService.deletePendingMemberOlderThan(date);
		
			logger.debug("End purge of pending member table, result : \n" +
				     " - number of pending member deleted : " + nbSmsDeleted);
	}
	
	/***********
	 * Mutator
	 */
	@Required
	public void setSeniorityDay(final int seniorityDay) {
		this.seniorityDay = seniorityDay;
	}
	
}
