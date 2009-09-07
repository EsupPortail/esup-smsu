package org.esupportail.smsu.business.beans;

/**
 * This class is used to stored customized message ready to be sent.
 * @author PRQD8824
 *
 */
public class CustomizedMessage {

	/**
	 * message Id.
	 */
	private Integer messageId;
	
	/**
	 * sender Id.
	 */
	private Integer senderId;
	
	/**
	 * group sender Id.
	 */
	private Integer groupSenderId;
	
	/**
	 * service Id.
	 */
	private Integer serviceId;
	
	/**
	 * recipient phone number.
	 */
	private String recipiendPhoneNumber;
	
	/**
	 * user account label.
	 */
	private String userAccountLabel;
	
	/**
	 * message.
	 */
	private String message;
	
	/////////////////////
	// Constructors
	/////////////////////
	/**
	 * Bean constructor.
	 */
	public CustomizedMessage()  {
		
	}
	
	///////////////////////
	// Getters and Setters
	///////////////////////
	/**
	 * @return the messageId
	 */
	public Integer getMessageId() {
		return messageId;
	}
	
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(final Integer messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the senderId
	 */
	public Integer getSenderId() {
		return senderId;
	}
	
	/**
	 * @param senderId the senderId to set
	 */
	public void setSenderId(final Integer senderId) {
		this.senderId = senderId;
	}

	/**
	 * @return the groupSenderId
	 */
	public Integer getGroupSenderId() {
		return groupSenderId;
	}
	
	/**
	 * @param groupSenderId the groupSenderId to set
	 */
	public void setGroupSenderId(final Integer groupSenderId) {
		this.groupSenderId = groupSenderId;
	}

	/**
	 * @return the serviceId
	 */
	public Integer getServiceId() {
		return serviceId;
	}
	
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(final Integer serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the recipiendPhoneNumber
	 */
	public String getRecipiendPhoneNumber() {
		return recipiendPhoneNumber;
	}
	
	/**
	 * @param recipiendPhoneNumber the recipiendPhoneNumber to set
	 */
	public void setRecipiendPhoneNumber(final String recipiendPhoneNumber) {
		this.recipiendPhoneNumber = recipiendPhoneNumber;
	}

	/**
	 * @return the userAccountLabel
	 */
	public String getUserAccountLabel() {
		return userAccountLabel;
	}
	
	/**
	 * @param userAccountLabel the userAccountLabel to set
	 */
	public void setUserAccountLabel(final String userAccountLabel) {
		this.userAccountLabel = userAccountLabel;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
	
}
