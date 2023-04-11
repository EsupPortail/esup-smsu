package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * The class that represents persons.
 */
@Entity
@Table(name = "person")
public class Person implements Serializable {

	/**
	 * Hibernate reference for person.
	 */
	public static final String REF = "person";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * Hibernate property for the login.
	 */
	public static final String PROP_LOGIN = "login";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 6084898523596550383L;

	/**
	 * person identifier.
	 */
	@Id
	@Column(name = "PER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * login identifier.
	 */
	@Column(name = "PER_LOGIN", nullable = false, length = 32, unique = true)
	@NotNull
	private String login;

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
		final Integer id,
		final String login) {

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
	public Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * Return the value associated with the column: PER_LOGIN.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Set the value related to the column: PER_LOGIN.
	 * @param login the PER_LOGIN value
	 */
	public void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * @see Object#equals(Object)
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
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}


	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Person#" + hashCode() + "[id=[" + id + "], login=[" + login 
		+  "]]";
	}


}