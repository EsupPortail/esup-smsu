package org.esupportail.smsu.dao.beans;

import java.io.Serializable;




/**
 * The class that represents accounts.
 */
public class Account implements Serializable {

	/**
	 * Hibernate reference for account.
	 */
	public static final String REF = "Account";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";
	
	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "Label";
	

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2840889867044286899L;

	/**
	 * Account identifier.
	 */
	private java.lang.Integer id;

	/**
	 * Account label.
	 */
	private java.lang.String label;

	
	/**
	 * Collection of messages associated to the account.
	 */
	private java.util.Set<Message> messages;
	
	/**
	 * Collection of customized groups associated to the account.
	 */
	private java.util.Set<CustomizedGroup> customizedGroups;

	/**
	 * Bean constructor.
	 */
	public Account() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public Account(
		final java.lang.Integer id,
		final java.lang.String label) {

		this.setId(id);
		this.setLabel(label);
	}

	public Account(String label) {
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="ACC_ID"
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
	 * Return the value associated with the column: ACC_LABEL.
	 */
	public java.lang.String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: ACC_LABEL.
	 * @param label the ACC_LABEL value
	 */
	public void setLabel(final java.lang.String label) {
		this.label = label;
	}



	/**
	 * Return the value associated with the column: Messages.
	 */
	public java.util.Set<Message> getMessages() {
		return messages;
	}

	/**
	 * Set the value related to the column: Messages.
	 * @param messages the Messages value
	 */
	public void setMessages(final java.util.Set<Message> messages) {
		this.messages = messages;
	}

	/**
	 * add a message to the collection.
	 * @param message
	 */
	public void addToMessages(final Message message) {
		if (null == getMessages()) {
			setMessages(new java.util.TreeSet<Message>());
		}
		getMessages().add(message);
	}



	/**
	 * Return the value associated with the column: CustomizedGroups.
	 */
	public java.util.Set<CustomizedGroup> getCustomizedGroups() {
		return customizedGroups;
	}

	/**
	 * Set the value related to the column: CustomizedGroups.
	 * @param customizedGroups the CustomizedGroups value
	 */
	public void setCustomizedGroups(final java.util.Set<CustomizedGroup> customizedGroups) {
		this.customizedGroups = customizedGroups;
	}

	/**
	 * add a customized group to the collection.
	 * @param customizedGroup
	 */
	public void addToCustomizedGroups(final CustomizedGroup customizedGroup) {
		if (null == getCustomizedGroups()) {
			setCustomizedGroups(new java.util.TreeSet<CustomizedGroup>());
		}
		getCustomizedGroups().add(customizedGroup);
	}




	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Account)) {
			return false;
		} else {
			Account account = (Account) obj;
			if (null == this.getId() || null == account.getId()) {
				return false;
			} else {
				return this.getId().equals(account.getId());
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
		return "Account#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}


}