package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.esupportail.smsu.dao.beans.idClass.ToMailRecipientPk;


/**
 * This is an object that contains data related to the to_mail_recipient table.
 *
 * @hibernate.class
 *  table="to_mail_recipient"
 */
@Entity
@Table(name = ToMailRecipient.TABLE_NAME)
@IdClass(ToMailRecipientPk.class)
public class ToMailRecipient  implements Serializable {

	/**
	 * Hibernate reference for the association ToMailRecipient.
	 */
	public static final String REF = "toMailRecipient";

	public static final String TABLE_NAME = "to_mail_recipient";
	public static final String MAIL_COLUMN = "MAIL_ID";
	public static final String MAIL_RECIPIENT_COLUMN = "MRC_ID";
	
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
	 * Bean constructor.
	 */
	public ToMailRecipient() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public ToMailRecipient(
		final MailRecipient mailRecipient,
		final Mail mail) {

		this.setMailRecipient(mailRecipient);
		this.setMail(mail);
	}


	/**
     * @hibernate.property
     *  column=MRC_ID
	 * not-null=true
	 */
	public MailRecipient getMailRecipient() {
		return this.mailRecipient;
	}

	/**
	 * Set the value related to the column: MRC_ID.
	 * @param mailRecipient the MRC_ID value
	 */
	public void setMailRecipient(final MailRecipient mailRecipient) {
		this.mailRecipient = mailRecipient;
	}

	/**
     * @hibernate.property
     *  column=MAIL_ID
	 * not-null=true
	 */
	public Mail getMail() {
		return this.mail;
	}

	/**
	 * Set the value related to the column: MAIL_ID.
	 * @param mail the MAIL_ID value
	 */
	public void setMail(final Mail mail) {
		this.mail = mail;
	}

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
		return "ToMailRecipient#" + hashCode() + "[mail=[" + mail + "], recipient=[" + mailRecipient 
		+ "]]";
	}


}