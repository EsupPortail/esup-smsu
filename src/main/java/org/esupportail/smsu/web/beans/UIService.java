package org.esupportail.smsu.web.beans;

import org.esupportail.smsu.dao.beans.Service;

/**
 * @author xphp8691
 *
 */
public class UIService extends Service {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4002034421575411225L;

	/**
	 * 
	 */
	private Boolean isDeletable;

	
	/**
	 * 
	 */
	public UIService() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param key
	 * @param isDeletable
	 */
	public UIService(final Integer id, final String name, final String key, final Boolean isDeletable) {
		super(id, name, key);
		this.isDeletable = isDeletable;
	}

	/**
	 * @param isDeletable
	 */
	public void setIsDeletable(final Boolean isDeletable) {
		this.isDeletable = isDeletable;
	}

	/**
	 * @return isDeletable
	 */
	public Boolean getIsDeletable() {
		return isDeletable;
	}
}
