package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.esupportail.smsu.dao.beans.idClass.ToMailRecipientPk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is an object that contains data related to the to_mail_recipient table.
 *
 * @hibernate.class table="to_mail_recipient"
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = ToMailRecipient.TABLE_NAME)
@IdClass(ToMailRecipientPk.class)
public class ToMailRecipient implements Serializable {

	/**
	 * Hibernate reference for the association ToMailRecipient.
	 */
	public static final String REF = "toMailRecipient";

	static final String TABLE_NAME = "to_mail_recipient";
	static final String MAIL_COLUMN = "MAIL_ID";
	static final String MAIL_RECIPIENT_COLUMN = "MRC_ID";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5707689193558690952L;

	/**
	 * mail recipient identifier.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = MAIL_RECIPIENT_COLUMN)
	private MailRecipient mailRecipient;

	/**
	 * mail identifier.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = MAIL_COLUMN)
	private Mail mail;

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof ToMailRecipient)) {
			return false;
		} else {
			ToMailRecipient toMailRecipient = (ToMailRecipient) obj;
			if (null != this.getMailRecipient() && null != toMailRecipient.getMailRecipient()) {
				if (!this.getMailRecipient().equals(toMailRecipient.getMailRecipient())) {
					return false;
				}
			} else {
				return false;
			}
			if (null != this.getMail() && null != toMailRecipient.getMail()) {
				if (!this.getMail().equals(toMailRecipient.getMail())) {
					return false;
				}
			} else {
				return false;
			}
			return true;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ToMailRecipient#" + hashCode() + "[mail=[" + mail + "], recipient=[" + mailRecipient + "]]";
	}
}