package org.esupportail.smsu.web.beans;

import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;

/**
 * @author xphp8691
 *
 */
public class UIMessage extends Message  {

	private static final long serialVersionUID = 1L;
	
	private Integer nbRecipients;

	/**
	 * senderName.
	 */
	private String senderName;
	
	/**
	 * group names.
	 */
	private String groupSenderName;
	private String groupRecipientName;
	
	private String stateMessage;
	
	private String stateMail;
	private String supervisorsText;

	/**
	 * Log4j logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * constructor.
	 */
	public UIMessage() {
		super();
	}
	
	/**
	 * @param stateMessage 
	 * @param stateMail  
	 * @param senderName
	 * @param groupSenderName
	 * @param groupRecipientName
	 * @param message
	 */
	public UIMessage(final String stateMessage, final String stateMail, final String senderName, 
			final String groupSenderName, final String groupRecipientName, final Message message) {
		super(message);
		this.stateMessage = stateMessage;
		this.stateMail = stateMail;
		this.senderName = senderName;
		this.groupSenderName = groupSenderName;
		this.groupRecipientName = groupRecipientName;

		this.nbRecipients = message.getRecipients().size();
		this.supervisorsText = computeSupervisorsText(message.getSupervisors());
	}
	
	private String computeSupervisorsText(Set<Person> supervisors) {
		if (supervisors == null) return null;
		String t = null;
		for (Person p : supervisors)
			t = (t == null ? "" : t + ", ") + p.getLogin();
		return t;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((senderName == null) ? 0 : senderName.hashCode());
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
		if (senderName == null) {
			if (other.senderName != null) {
				return false;
		    }
		} else if (!senderName.equals(other.senderName)) {
			return false;
		  }
		return true;
	}

	public int getNbRecipients() {
		return nbRecipients;
	}

	/**
	 * @param senderName
	 */
	public void setSenderName(final String senderName) {
		this.senderName = senderName;
	}

	/**
	 * @return senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	public String getGroupSenderName() {
		return groupSenderName;
	}

	public String getGroupRecipientName() {
		return groupRecipientName;
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

	public String getSupervisorsText() {
		return supervisorsText;
	}

}
