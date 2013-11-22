package org.esupportail.smsu.web.beans;

/**
 * @author xphp8691
 *
 */
public class GroupRecipient extends UiRecipient {

	/**
	 * 
	 */
	public GroupRecipient() {
		super();
	}

	/**
	 * @param displayName
	 * @param id
	 * @param login
	 * @param phone
	 */
	public GroupRecipient(final String displayName) {
		super(displayName, displayName, null, null);
	}


}
