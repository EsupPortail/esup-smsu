package org.esupportail.smsu.web.beans;

import org.esupportail.smsu.dao.beans.CustomizedGroup;

/**
 * @author xphp8691
 *
 */
public class DisplayedGroup {

	private CustomizedGroup customizedGroup;

	private String displayName;
	
	public DisplayedGroup() {
		super();
	}

	public void setCustomizedGroup(final CustomizedGroup customizedGroup) {
		this.customizedGroup = customizedGroup;
	}

	public CustomizedGroup getCustomizedGroup() {
		return customizedGroup;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}
