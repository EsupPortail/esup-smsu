package org.esupportail.smsu.web.beans;

import org.esupportail.smsu.dao.beans.Template;

/**
 * @author xphp8691
 *
 */
public class UITemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8830101946656901950L;
	
	/**
	 * 
	 */
	private Boolean isDeletable;

	
	
	/**
	 * 
	 */
	public UITemplate() {
		super();
	}

	/**
	 * @param id
	 * @param label
	 * @param isDeletable
	 */
	public UITemplate(final Integer id, final String label, final Boolean isDeletable) {
		super(id, label);
		this.setIsDeletable(isDeletable);
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
