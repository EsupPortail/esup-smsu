package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class that represents persons.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
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

	public Person(String login) {
		this.setLogin(login);
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
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Person#" + hashCode() + "[id=[" + id + "], login=[" + login + "]]";
	}
}