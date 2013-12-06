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
 */
public class PeriodicPurge {

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
			logger.debug("Launching periodic purge with parameter : \n" + " - seniority date : " + seniorityDate);
			logger.trace("Start the message content purge");
		daoService.deleteMessageContentOlderThan(seniorityDate);
			logger.trace("End of the message content purge\n" + "Start the orphan mail purge");
		daoService.deleteOrphanMail();
			logger.trace("End of the orphan mail purge\n" + "Start the orphan recipient purge");
		daoService.deleteOrphanRecipient();
			logger.trace("End of the orphan recipient purge\n" + "Start the orphan mail recipient purge");
		daoService.deleteOrphanMailRecipient();
			logger.trace("End of the orphan mail recipient purge\n" + "Start the orphan person purge");
		daoService.deleteOrphanPerson();
			logger.trace("End of the orphan person purge\n" + "Start the orphan basic group purge");
		daoService.deleteOrphanBasicGroup();
			logger.trace("End of the orphan basic group purge\n");
			logger.debug("End of periodic purge");
	}
	
	
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
	
	public void setSeniorityDay(final int seniorityDay) {
		this.seniorityDay = seniorityDay;
	}

}
