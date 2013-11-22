package org.esupportail.smsu.web.beans;

import org.esupportail.smsu.dao.beans.Role;

/**
 * @author hcbp0056
 *
 */
public class UIRole extends Role {
 	
	private static final long serialVersionUID = 1L;
	
	private Boolean isDeletable;
	
	private Boolean isUpdateable;
	
	public UIRole() {
		super();
	}

	/**
	 * @param id
	 * @param label
	 * @param isDeletable 
	 */
	public UIRole(final Integer id, final String name, final Boolean isDeletable, final Boolean isUpdateable) {
		super(id, name);
		this.setIsDeletable(isDeletable);
		this.setIsUpdateable(isUpdateable);
	}
/**
 * @param isDeletable the isDeletable to set
 */
public void setIsDeletable(final Boolean isDeletable) {
	this.isDeletable = isDeletable;
}
/**
 * @return the isDeletable
 */
public Boolean getIsDeletable() {
	return isDeletable;
}

/**
 * @param isUpdateable the isUpdateable to set
 */
public void setIsUpdateable(final Boolean isUpdateable) {
	this.isUpdateable = isUpdateable;
}

/**
 * @return the isUpdateable
 */
public Boolean getIsUpdateable() {
	return isUpdateable;
}


}
