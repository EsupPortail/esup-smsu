package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.esupportail.smsu.domain.beans.mail.MailStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class that represents mails.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "mail")
public class Mail implements Serializable {

	/**
	 * Hibernate reference for the mail.
	 */
	public static final String REF = "mail";

	/**
	 * Hibernate property for the subject.
	 */
	public static final String PROP_SUBJECT = "subject";

	/**
	 * Hibernate property for the state.
	 */
	public static final String PROP_STATE = "state";

	/**
	 * Hibernate property for the template.
	 */
	public static final String PROP_TEMPLATE = "template";

	/**
	 * Hibernate property for the message.
	 */
	public static final String PROP_MESSAGE = "message";

	/**
	 * Hibernate property for the content.
	 */
	public static final String PROP_CONTENT = "content";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -8863093058920714428L;

	/**
	 * mail identifier.
	 */
	@Id
	@Column(name = "MAIL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * mail content.
	 */
	@Column(name = "MAIL_CONTENT", nullable = false, length = 300)
	@NotNull
	private String content;

	/**
	 * mail state.
	 */
	@Column(name = "MAIL_STATE", nullable = false, length = 16)
	@NotNull
	@Enumerated(EnumType.STRING)
	private MailStatus state;

	/**
	 * mail state.
	 */
	@Column(name = "MAIL_SUBJECT", length = 300)
	private String subject;

	/**
	 * template of mail.
	 */
	@ManyToOne
	@JoinColumn(name = "TPL_ID")
	private Template template;

	/**
	 * message associated to the mail.
	 */
	@OneToOne(mappedBy = "mail")
	private Message message;

	/**
	 * collection of mail recipients.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = ToMailRecipient.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = ToMailRecipient.MAIL_COLUMN) }, //
			inverseJoinColumns = { @JoinColumn(name = ToMailRecipient.MAIL_RECIPIENT_COLUMN) })
	private Set<MailRecipient> mailRecipients;

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Mail)) {
			return false;
		} else {
			Mail mail = (Mail) obj;
			if (null == this.getId() || null == mail.getId()) {
				return false;
			} else {
				return this.getId().equals(mail.getId());
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Mail#" + hashCode() + "[id=[" + id + "], subject=[" + subject + "], content=[" + content + "], state=["
				+ state + "], template=[" + template + "], message=[" + message + "]]";
	}
}