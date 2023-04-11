package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import lombok.NoArgsConstructor;

/**
 * This is an object that contains data related to the message table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="message"
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message  implements Serializable {
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
	private static final long serialVersionUID = -873902932158754471L;

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
	private String state;

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
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="MSG_ID"
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
	 * Return the value associated with the column: MSG_DATE.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Set the value related to the column: MSG_DATE.
	 * @param date the MSG_DATE value
	 */
	public void setDate(final Date date) {
		this.date = date;
	}



	/**
	 * Return the value associated with the column: MSG_CONTENT.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the value related to the column: MSG_CONTENT.
	 * @param content the MSG_CONTENT value
	 */
	public void setContent(final String content) {
		this.content = content;
	}



	/**
	 * Return the value associated with the column: MSG_STATE.
	 */
	@Deprecated
	public String getState() {
		return state;
	}

	/**
	 * Return the value associated with the column: MSG_STATE as an MessageStatus.
	 * @return
	 */
	public MessageStatus getStateAsEnum() {
		final MessageStatus state = MessageStatus.valueOf(this.state);
		return state;
	}
	
	/**
	 * Set the value related to the column: MSG_STATE.
	 * @param state the MSG_STATE value
	 */
	@Deprecated
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * Set the value related to the column: MSG_STATE.
	 * @param stateAsEnum
	 */
	public void setStateAsEnum(final MessageStatus stateAsEnum) {
		if (stateAsEnum != null) {
			this.state = stateAsEnum.name();
		} else {
			this.state = null;
		}
	}


	/**
	 * Return the value associated with the column: ACC_ID.
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Set the value related to the column: ACC_ID.
	 * @param account the ACC_ID value
	 */
	public void setAccount(final Account account) {
		this.account = account;
	}



	/**
	 * Return the value associated with the column: TPL_ID.
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * Set the value related to the column: TPL_ID.
	 * @param template the TPL_ID value
	 */
	public void setTemplate(final Template template) {
		this.template = template;
	}



	/**
	 * Return the value associated with the column: PER_ID.
	 */
	public Person getSender() {
		return sender;
	}

	/**
	 * Set the value related to the column: PER_ID.
	 * @param sender the PER_ID value
	 */
	public void setSender(final Person sender) {
		this.sender = sender;
	}



	/**
	 * Return the value associated with the column: SVC_ID.
	 */
	public Service getService() {
		return service;
	}

	/**
	 * Set the value related to the column: SVC_ID.
	 * @param service the SVC_ID value
	 */
	public void setService(final Service service) {
		this.service = service;
	}



	/**
	 * Return the value associated with the column: MAIL_ID.
	 */
	public Mail getMail() {
		return mail;
	}

	/**
	 * Set the value related to the column: MAIL_ID.
	 * @param mail the MAIL_ID value
	 */
	public void setMail(final Mail mail) {
		this.mail = mail;
	}



	/**
	 * Return the value associated with the column: BGR_SENDER_ID.
	 */
	public BasicGroup getGroupSender() {
		return groupSender;
	}

	/**
	 * Set the value related to the column: BGR_SENDER_ID.
	 * @param groupSender the BGR_SENDER_ID value
	 */
	public void setGroupSender(final BasicGroup groupSender) {
		this.groupSender = groupSender;
	}



	/**
	 * Return the value associated with the column: BGR_RECIPIENT_ID.
	 */
	public BasicGroup getGroupRecipient() {
		return groupRecipient;
	}

	/**
	 * Set the value related to the column: BGR_RECIPIENT_ID.
	 * @param groupRecipient the BGR_RECIPIENT_ID value
	 */
	public void setGroupRecipient(final BasicGroup groupRecipient) {
		this.groupRecipient = groupRecipient;
	}



	/**
	 * Return the value associated with the column: Recipients.
	 */
	public Set<Recipient> getRecipients() {
		return recipients;
	}

	/**
	 * Set the value related to the column: Recipients.
	 * @param recipients the Recipients value
	 */
	public void setRecipients(final Set<Recipient> recipients) {
		this.recipients = recipients;
	}

	/**
	 * Return the value associated with the column: Supervisors.
	 */
	public Set<Person> getSupervisors() {
		return supervisors;
	}

	/**
	 * Set the value related to the column: Supervisors.
	 * @param supervisors the Supervisors value
	 */
	public void setSupervisors(final Set<Person> supervisors) {
		this.supervisors = supervisors;
	}

	/**
	 * @see Object#hashCode()
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
		return "Message#" + hashCode() + "[id=[" + id + "], content=[" + content 
		+ "], state=[" + state +  "]]";
	}

}