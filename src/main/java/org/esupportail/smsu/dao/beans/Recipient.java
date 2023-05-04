package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class that represent recipients.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "recipient", indexes = @Index(name = "RCP_PHONE_LOGIN", columnList = "RCP_PHONE, RCP_LOGIN"))
public class Recipient implements Serializable {

	/**
	 * Hibernate reference for person.
	 */
	public static final String REF = "recipient";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * Hibernate property for the phone.
	 */
	public static final String PROP_PHONE = "phone";

	/**
	 * Hibernate property for the login.
	 */
	public static final String PROP_LOGIN = "login";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -2722215388225845211L;

	/**
	 * recipient identifier.
	 */
	@Id
	@Column(name = "RCP_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * recipient phone.
	 */
	@Column(name = "RCP_PHONE", nullable = false, length = 255)
	@NotNull
	private String phone;

	/**
	 * recipient login.
	 */
	@Column(name = "RCP_LOGIN", length = 32)
	private String login;

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Recipient)) {
			return false;
		} else {
			Recipient recipient = (Recipient) obj;
			if (null == this.getId() || null == recipient.getId()) {
				return false;
			} else {
				return this.getId().equals(recipient.getId());
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Recipient#" + hashCode() + "[id=[" + id + "], phone=[" + phone + "], login=[" + login + "]]";
	}
}