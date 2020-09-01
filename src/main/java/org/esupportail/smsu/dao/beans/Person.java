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