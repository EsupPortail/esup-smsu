package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.esupportail.smsu.domain.beans.message.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is an object that contains data related to the message table. Do not
 * modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class table="message"
 */
// lombok
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "message")
public class Message implements Serializable {
	/**
	 * Hibernate reference for message.
	 */
	public static final String REF = "message";

	/**
	 * Hibernate property for the service.
	 */
	public static final String PROP_SERVICE = "service";

	/**
	 * Hibernate property for the account.
	 */
	public static final String PROP_ACCOUNT = "account";

	/**
	 * Hibernate property for the state.
	 */
	public static final String PROP_STATE = "state";

	/**
	 * Hibernate property for the group sender.
	 */
	public static final String PROP_GROUP_SENDER = "groupSender";

	/**
	 * Hibernate property for the template.
	 */
	public static final String PROP_TEMPLATE = "template";

	/**
	 * Hibernate property for the mail.
	 */
	public static final String PROP_MAIL = "mail";

	/**
	 * Hibernate property for the date.
	 */
	public static final String PROP_DATE = "date";

	/**
	 * Hibernate property for the sender.
	 */
	public static final String PROP_SENDER = "sender";

	/**
	 * Hibernate property for the group recipient.
	 */
	public static final String PROP_GROUP_RECIPIENT = "groupRecipient";

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
	private static final long serialVersionUID = 4912497159307656533L;

	/**
	 * message identifier.
	 */
	@Id
	@Column(name = "MSG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * creation date.
	 */
	@Column(name = "MSG_DATE", nullable = false)
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	/**
	 * message content.
	 */
	@Column(name = "MSG_CONTENT", nullable = false, length = 255)
	@NotNull
	private String content;

	/**
	 * message state.
	 */
	@Column(name = "MSG_STATE", nullable = false, length = 32)
	@NotNull
	@Enumerated(EnumType.STRING)
	private MessageStatus state;

	/**
	 * associated account.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ACC_ID", nullable = false)
	@NotNull
	private Account account;

	/**
	 * associated template.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "TPL_ID")
	private Template template;

	/**
	 * message sender.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PER_ID", nullable = false)
	@NotNull
	private Person sender;

	/**
	 * message service.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "SVC_ID")
	private Service service;

	/**
	 * associated mail.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "MAIL_ID", unique = true)
	private Mail mail;

	/**
	 * message group sender.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "BGR_SENDER_ID", nullable = false)
	@NotNull
	private BasicGroup groupSender;

	/**
	 * message group recipient.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "BGR_RECIPIENT_ID")
	private BasicGroup groupRecipient;

	/**
	 * collection of recipients.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = ToRecipient.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = ToRecipient.MSG_COLUMN) }, //
			inverseJoinColumns = { @JoinColumn(name = ToRecipient.RECIPIENT_COLUMN) })
	private Set<Recipient> recipients;

	/**
	 * collection of supervisors.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = SupervisorSender.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = SupervisorSender.MSG_COLUMN) }, //
			inverseJoinColumns = { @JoinColumn(name = SupervisorSender.SUPERVISOR_COLUMN) })
	private Set<Person> supervisors;

	/**
	 * @see Object#equals()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Message)) {
			return false;
		} else {
			Message message = (Message) obj;
			if (null == this.getId() || null == message.getId()) {
				return false;
			} else {
				return this.getId().equals(message.getId());
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Message#" + hashCode() + "[id=[" + id + "], content=[" + content + "], state=[" + state + "]]";
	}

}