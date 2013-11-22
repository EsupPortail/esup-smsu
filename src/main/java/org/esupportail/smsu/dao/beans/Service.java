package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represent services.
 */
public class Service implements Serializable {

	/**
	 * Hibernate reference for service.
	 */
	public static final String REF = "Service";

	/**
	 * Hibernate property for the key.
	 */
	public static final String PROP_KEY = "Key";

	/**
	 * Hibernate property for the name.
	 */
	public static final String PROP_NAME = "Name";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";


	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2450143530342732074L;

	/**
	 * Service identifier.
	 */
	private java.lang.Integer id;

	/**
	 * Service name.
	 */
	private java.lang.String name;
	
	/**
	 * Service key.
	 */
	private java.lang.String key;

	/**
	 * collection of message for the service.
	 */
	private java.util.Set<Message> messages;

	/**
	 * Bean constructor.
	 */
	public Service() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public Service(final java.lang.Integer id,
		final java.lang.String name,
		final java.lang.String key) {
		this.setId(id);
		this.setName(name);
		this.setKey(key);
	}

	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="SVC_ID"
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
	 * Return the value associated with the column: SVC_NAME.
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: SVC_NAME.
	 * @param name the SVC_NAME value
	 */
	public void setName(final java.lang.String name) {
		this.name = name;
	}



	/**
	 * Return the value associated with the column: SVC_KEY.
	 */
	public java.lang.String getKey() {
		return key;
	}

	/**
	 * Set the value related to the column: SVC_KEY.
	 * @param key the SVC_KEY value
	 */
	public void setKey(final java.lang.String key) {
		this.key = key;
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
	 * Add a message to the set associated to the service.
	 * @param message a message value
	 */
	public void addToMessages(final Message message) {
		if (null == getMessages()) {
			setMessages(new java.util.TreeSet<Message>());
		}
		getMessages().add(message);
	}




	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Service)) {
			return false;
		} else {
			Service service = (Service) obj;
			if (null == this.getId() || null == service.getId()) {
				return false;
			} else {
				return this.getId().equals(service.getId());
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
		return "Service#" + hashCode() + "[id=[" + id + "], name=[" + name 
		+ "], key=[" + key + "]]";
	}


}