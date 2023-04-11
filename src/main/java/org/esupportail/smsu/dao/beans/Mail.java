package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.esupportail.smsu.domain.beans.mail.MailStatus;


/**
 * The class that represents mails.
 */
@Entity
@Table(name = "mail")
public class Mail  implements Serializable {

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
	private static final long serialVersionUID = -5796260133463399411L;

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
	private String state;
	
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
	 * Bean constructor.
	 */
	public Mail() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public Mail(
		final Integer id,
		final String subject,
		final String content,
		final String state) {

		this.setId(id);
		this.setContent(content);
		this.setState(state);
		this.setSubject(subject);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="MAIL_ID"
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
	 * Return the value associated with the column: MAIL_CONTENT.
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Return the value associated with the column: MAIL_SUBJECT.
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Set the value related to the column: MAIL_CONTENT.
	 * @param content the MAIL_CONTENT value
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * Set the value related to the column: MAIL_SUBJECT.
	 * @param contents the MAIL_SUBJECT value
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * Return the value associated with the column: MAIL_STATE.
	 */
	@Deprecated
	public String getState() {
		return state;
	}

	/**
	 * Return the value associated with the column: MAIL_STATE as an enum.
	 */
	public MailStatus getStateAsEnum() {
		final MailStatus mailState = MailStatus.valueOf(this.state);
		return mailState;
	}
	
	/**
	 * Set the value related to the column: MAIL_STATE.
	 * @param state the MAIL_STATE value
	 */
	@Deprecated
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * Set the value related to the column: MAIL_STATE.
	 * @param state the MAIL_STATE value
	 */
	public void setStateAsEnum(final MailStatus stateAsEnum) {
		if (stateAsEnum != null) {
			this.state = stateAsEnum.name();
		} else {
			this.state = null;
		}
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
	 * Return the value associated with the column: Message.
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * Set the value related to the column: Message.
	 * @param messages the Message value
	 */
	public void setMessage(final Message message) {
		this.message = message;
	}


	/**
	 * Return the value associated with the column: MailRecipients.
	 */
	public Set<MailRecipient> getMailRecipients() {
		return mailRecipients;
	}

	/**
	 * Set the value related to the column: MailRecipients.
	 * @param mailRecipients the MailRecipients value
	 */
	public void setMailRecipients(final Set<MailRecipient> mailRecipients) {
		this.mailRecipients = mailRecipients;
	}


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
		return "Mail#" + hashCode() + "[id=[" + id + "], subject=[" + subject + "], content=[" + content 
		+ "], state=[" + state + "], template=[" + template + "], message=[" + message + "]]";
	}


}