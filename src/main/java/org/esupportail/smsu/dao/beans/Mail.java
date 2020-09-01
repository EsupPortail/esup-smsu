package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import org.esupportail.smsu.domain.beans.mail.MailStatus;


/**
 * The class that represents mails.
 */
public class Mail  implements Serializable {

	/**
	 * Hibernate reference for the mail.
	 */
	public static final String REF = "Mail";

	/**
	 * Hibernate property for the subject.
	 */
	public static final String PROP_SUBJECT = "Subject";
	
	/**
	 * Hibernate property for the state.
	 */
	public static final String PROP_STATE = "State";

	/**
	 * Hibernate property for the template.
	 */
	public static final String PROP_TEMPLATE = "Template";

	/**
	 * Hibernate property for the message.
	 */
	public static final String PROP_MESSAGE = "Message";

	/**
	 * Hibernate property for the content.
	 */
	public static final String PROP_CONTENT = "Content";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -5796260133463399411L;

	/**
	 * mail identifier.
	 */
	private java.lang.Integer id;

	/**
	 * mail content.
	 */
	private java.lang.String content;

	/**
	 * mail state.
	 */
	private java.lang.String state;
	
	/**
	 * mail state.
	 */
	private java.lang.String subject;


	/**
	 * template of mail.
	 */
	private Template template;

	/**
	 * message associated to the mail.
	 */
	private Message message;

	/**
	 * collection of mail recipients.
	 */
	private java.util.Set<MailRecipient> mailRecipients;

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
		final java.lang.Integer id,
		final java.lang.String subject,
		final java.lang.String content,
		final java.lang.String state) {

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
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final java.lang.Integer id) {
		this.id = id;
	}




	/**
	 * Return the value associated with the column: MAIL_CONTENT.
	 */
	public java.lang.String getContent() {
		return content;
	}
	
	/**
	 * Return the value associated with the column: MAIL_SUBJECT.
	 */
	public java.lang.String getSubject() {
		return subject;
	}
	
	/**
	 * Set the value related to the column: MAIL_CONTENT.
	 * @param content the MAIL_CONTENT value
	 */
	public void setContent(final java.lang.String content) {
		this.content = content;
	}

	/**
	 * Set the value related to the column: MAIL_SUBJECT.
	 * @param contents the MAIL_SUBJECT value
	 */
	public void setSubject(final java.lang.String subject) {
		this.subject = subject;
	}

	/**
	 * Return the value associated with the column: MAIL_STATE.
	 */
	@Deprecated
	public java.lang.String getState() {
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
	public void setState(final java.lang.String state) {
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
	public java.util.Set<MailRecipient> getMailRecipients() {
		return mailRecipients;
	}

	/**
	 * Set the value related to the column: MailRecipients.
	 * @param mailRecipients the MailRecipients value
	 */
	public void setMailRecipients(final java.util.Set<MailRecipient> mailRecipients) {
		this.mailRecipients = mailRecipients;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
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
		return "Mail#" + hashCode() + "[id=[" + id + "], subject=[" + subject + "], content=[" + content 
		+ "], state=[" + state + "], template=[" + template + "], message=[" + message + "]]";
	}


}