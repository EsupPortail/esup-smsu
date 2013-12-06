package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represents persons.
 */
public class Person  implements Serializable {

	/**
	 * Hibernate reference for person.
	 */
	public static final String REF = "Person";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * Hibernate property for the login.
	 */
	public static final String PROP_LOGIN = "Login";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 6084898523596550383L;

	/**
	 * person identifier.
	 */
	private java.lang.Integer id;

	/**
	 * login identifier.
	 */
	private java.lang.String login;

	/**
	 * collection of messages by sender.
	 */
	private java.util.Set<Message> messagesBySender;

	/**
	 * collection messages by supervisor.
	 */
	private java.util.Set<Message> messagesBySupervisor;

	/**
	 * collection of customized group.
	 */
	private java.util.Set<CustomizedGroup> customizedGroups;

	/**
	 * Bean constructor.
	 */
	public Person() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public Person(
		final java.lang.Integer id,
		final java.lang.String login) {

		this.setId(id);
		this.setLogin(login);
	}


	public Person(String login) {
		this.setLogin(login);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="PER_ID"
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
	 * Return the value associated with the column: PER_LOGIN.
	 */
	public java.lang.String getLogin() {
		return login;
	}

	/**
	 * Set the value related to the column: PER_LOGIN.
	 * @param login the PER_LOGIN value
	 */
	public void setLogin(final java.lang.String login) {
		this.login = login;
	}



	/**
	 * Return the value associated with the column: MessagesBySender.
	 */
	public java.util.Set<Message> getMessagesBySender() {
		return messagesBySender;
	}

	/**
	 * Set the value related to the column: MessagesBySender.
	 * @param messagesBySender the MessagesBySender value
	 */
	public void setMessagesBySender(final java.util.Set<Message> messagesBySender) {
		this.messagesBySender = messagesBySender;
	}

	/**
	 * Add a message to the collection of messages messagesBySender.
	 * @param message
	 */
	public void addToMessagesBySender(final Message message) {
		if (null == getMessagesBySender()) {
			setMessagesBySender(new java.util.TreeSet<Message>());
		}
		getMessagesBySender().add(message);
	}



	/**
	 * Return the value associated with the column: MessagesBySupervisor.
	 */
	public java.util.Set<Message> getMessagesBySupervisor() {
		return messagesBySupervisor;
	}

	/**
	 * Set the value related to the column: MessagesBySupervisor.
	 * @param messagesBySupervisor the MessagesBySupervisor value
	 */
	public void setMessagesBySupervisor(final java.util.Set<Message> messagesBySupervisor) {
		this.messagesBySupervisor = messagesBySupervisor;
	}

	/**
	 * Add a message to the collection of messages messagesBySupervisor.
	 * @param message
	 */
	public void addToMessagesBySupervisor(final Message message) {
		if (null == getMessagesBySupervisor()) {
			setMessagesBySupervisor(new java.util.TreeSet<Message>());
		}
		getMessagesBySupervisor().add(message);
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
	 * Add a customized group to the collection of customized groups.
	 * @param message
	 */
	public void addToCustomizedGroups(final CustomizedGroup customizedGroup) {
		if (null == getCustomizedGroups()) {
			setCustomizedGroups(new java.util.TreeSet<CustomizedGroup>());
		}
		getCustomizedGroups().add(customizedGroup);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Person)) {
			return false;
		} else {
			Person person = (Person) obj;
			if (null == this.getId() || null == person.getId()) {
				return false;
			} else {
				return this.getId().equals(person.getId());
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
		return "Person#" + hashCode() + "[id=[" + id + "], login=[" + login 
		+  "]]";
	}


}