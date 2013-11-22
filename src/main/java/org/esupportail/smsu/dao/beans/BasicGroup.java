package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represents basic groups.
 */
public class BasicGroup  implements Serializable {

	/**
	 * Hibernate reference for the basic group.
	 */
	public static final String REF = "BasicGroup";

	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "Label";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -2436913232913567894L;

	/**
	 * basic group identifier.
	 */
	private java.lang.Integer id;

	/**
	 * basic group label.
	 */
	private java.lang.String label;

	/**
	 * collection of messages for which the group is the sender group.
	 */
	private java.util.Set<Message> messagesByGrpSender;

	/**
	 * collection of messages for which the group is the recipient group.
	 */
	private java.util.Set<Message> messagesByGrpRecipient;

	/**
	 * Bean constructor.
	 */
	public BasicGroup() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public BasicGroup(
		final java.lang.Integer id,
		final java.lang.String label) {

		this.setId(id);
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="BGR_ID"
     */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final java.lang.Integer id) {
		this.id = id;
	}




	/**
	 * Return the value associated with the column: BGR_LABEL.
	 */
	public java.lang.String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: BGR_LABEL.
	 * @param label the BGR_LABEL value
	 */
	public void setLabel(final java.lang.String label) {
		this.label = label;
	}



	/**
	 * Return the value associated with the column: MessagesByGrpSender.
	 */
	public java.util.Set<Message> getMessagesByGrpSender() {
		return messagesByGrpSender;
	}

	/**
	 * Set the value related to the column: MessagesByGrpSender.
	 * @param messagesByGrpSender the MessagesByGrpSender value
	 */
	public void setMessagesByGrpSender(final java.util.Set<Message> messagesByGrpSender) {
		this.messagesByGrpSender = messagesByGrpSender;
	}

	/**
	 * Add a message to the collection of messages messagesByGrpSender.
	 * @param message
	 */
	public void addToMessagesByGrpSender(final Message message) {
		if (null == getMessagesByGrpSender()) {
			setMessagesByGrpSender(new java.util.TreeSet<Message>());
		}
		getMessagesByGrpSender().add(message);
	}



	/**
	 * Return the value associated with the column: MessagesByGrpRecipient.
	 */
	public java.util.Set<Message> getMessagesByGrpRecipient() {
		return messagesByGrpRecipient;
	}

	/**
	 * Set the value related to the column: MessagesByGrpRecipient.
	 * @param messagesByGrpRecipient the MessagesByGrpRecipient value
	 */
	public void setMessagesByGrpRecipient(final java.util.Set<Message> messagesByGrpRecipient) {
		this.messagesByGrpRecipient = messagesByGrpRecipient;
	}

	/**
	 * Add a message to the collection of messages messagesByGrpRecipient.
	 * @param message
	 */
	public void addToMessagesByGrpRecipient(final Message message) {
		if (null == getMessagesByGrpRecipient()) {
			setMessagesByGrpRecipient(new java.util.TreeSet<Message>());
		}
		getMessagesByGrpRecipient().add(message);
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof BasicGroup)) {
			return false;
		} else {
			BasicGroup basicGroup = (BasicGroup) obj;
			if (null == this.getId() || null == basicGroup.getId()) {
				return false;
			} else {
				return this.getId().equals(basicGroup.getId());
			}
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasicGroup#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}

}