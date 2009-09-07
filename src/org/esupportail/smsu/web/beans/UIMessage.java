package org.esupportail.smsu.web.beans;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Message;

/**
 * @author xphp8691
 *
 */
public class UIMessage extends Message  {

	private static final long serialVersionUID = 1L;
	
	/**
	 * displayName.
	 */
	private String displayName;
	
	/**
	 * groupName.
	 */
	private String groupName;
	
	/**
	 * displayName.
	 */
	private String stateMessage;
	
	/**
	 * displayName.
	 */
	private String stateMail;
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * constructor.
	 */
	public UIMessage() {
		super();
	}

	/**
	 * @param displayName
	 * @param groupName 
	 * @param message 
	 */
	public UIMessage(final String displayName,	final String groupName, final Message message) {
		super(message);
		this.displayName = displayName;
		this.groupName = groupName;
	}
	
	/**
	 * @param displayName
	 * @param groupName 
	 * @param message
	 * @param stateMessage 
	 * @param stateMail  
	 */
	public UIMessage(final String stateMessage, final String stateMail, final String displayName, 
			final String groupName, final Message message) {
		super(message);
		this.displayName = displayName;
		this.groupName = groupName;
		this.stateMessage = stateMessage;
		this.stateMail = stateMail;
	}
	

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result;
		return result;
	}



	/* *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}	
		if (obj == null) {
			return false;
		}	
		if (getClass() != obj.getClass()) {
			return false;
		}	
		UIMessage other = (UIMessage) obj;
		if (displayName == null) {
			if (other.displayName != null) {
				return false;
		    }
		} else if (!displayName.equals(other.displayName)) {
			return false;
		  }
		return true;
	}



	/**
	 * @param displayName
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param stateMessage the stateMessage to set
	 */
	public void setStateMessage(final String stateMessage) {
		this.stateMessage = stateMessage;
	}

	/**
	 * @return the stateMessage
	 */
	public String getStateMessage() {
		return stateMessage;
	}

	/**
	 * @param stateMail the stateMail to set
	 */
	public void setStateMail(final String stateMail) {
		this.stateMail = stateMail;
	}

	/**
	 * @return the stateMail
	 */
	public String getStateMail() {
		return stateMail;
	}

}
