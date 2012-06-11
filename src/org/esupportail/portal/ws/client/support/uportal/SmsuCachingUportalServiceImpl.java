package org.esupportail.portal.ws.client.support.uportal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

/* wrapper around CachingUportalServiceImpl to add some nice error logs */
public class SmsuCachingUportalServiceImpl extends CachingUportalServiceImpl {

    	/**
	 * The URL of the web service.
	 */
	private String theUrl;

	/**
	 * Logger object.
	 */
	private final Log logger = LogFactory.getLog(CachingUportalServiceImpl.class);


	public String getUrl() {
		return theUrl;
	}

	protected DataAccessException wrap(final Exception e) {
		String reason = e.toString();
		String msg = "failed accessing portail services using " + getUrl() + " : " + reason;
		if (e instanceof org.apache.axis.AxisFault &&
		    reason != null && reason.contains("java.net.ConnectException:")) {
		    logger.fatal(msg);
		} else
			logger.error(msg);
		return super.wrap(e);
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(final String url) {
		this.theUrl = url;
		super.setUrl(url);		
	}

}