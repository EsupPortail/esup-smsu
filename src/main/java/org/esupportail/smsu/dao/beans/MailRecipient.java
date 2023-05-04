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
 * The class that represents mail recipients.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "mail_recipient")
public class MailRecipient implements Serializable {

	/**
	 * Hibernate reference for mail recipient.
	 */
	public static final String REF = "mailRecipient";

	/**
	 * Hibernate property for the address.
	 */
	public static final String PROP_ADDRESS = "address";

	/**
	 * Hibernate property for the address.
	 */
	public static final String PROP_LOGIN = "login";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -6480239557091230008L;

	/**
	 * mail recipient identifier.
	 */
	@Id
	@Column(name = "MRC_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * mail recipient address.
	 */
	@Column(name = "MRC_ADDRESS", nullable = false, length = 100, unique = true)
	@NotNull
	private String address;

	/**
	 * mail recipient login.
	 */
	@Column(name = "MRC_LOGIN", length = 32)
	private String login;

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof MailRecipient)) {
			return false;
		} else {
			MailRecipient mailRecipient = (MailRecipient) obj;
			if (null == this.getId() || null == mailRecipient.getId()) {
				return false;
			} else {
				return this.getId().equals(mailRecipient.getId());
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "MailRecipient#" + hashCode() + "[id=[" + id + "], address=[" + address + "], login[" + login + "]]";
	}
}