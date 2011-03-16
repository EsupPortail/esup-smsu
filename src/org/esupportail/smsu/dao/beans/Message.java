package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import org.esupportail.smsu.domain.beans.message.MessageStatus;

/**
 * This is an object that contains data related to the message table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="message"
 */

public class Message  implements Serializable {
	/**
	 * Hibernate reference for message.
	 */
	public static final String REF = "Message";

	/**
	 * Hibernate property for the service.
	 */
	public static final String PROP_SERVICE = "Service";

	/**
	 * Hibernate property for the account.
	 */
	public static final String PROP_ACCOUNT = "Account";

	/**
	 * Hibernate property for the state.
	 */
	public static final String PROP_STATE = "State";

	/**
	 * Hibernate property for the group sender.
	 */
	public static final String PROP_GROUP_SENDER = "GroupSender";

	/**
	 * Hibernate property for the template.
	 */
	public static final String PROP_TEMPLATE = "Template";

	/**
	 * Hibernate property for the mail.
	 */
	public static final String PROP_MAIL = "Mail";

	/**
	 * Hibernate property for the date.
	 */
	public static final String PROP_DATE = "Date";

	/**
	 * Hibernate property for the sender.
	 */
	public static final String PROP_SENDER = "Sender";

	/**
	 * Hibernate property for the group recipient.
	 */
	public static final String PROP_GROUP_RECIPIENT = "GroupRecipient";

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
	private static final long serialVersionUID = -873902932158754471L;

	/**
	 * message identifier.
	 */
	private java.lang.Integer id;

	/**
	 * creation date.
	 */
	private java.util.Date date;
	
	/**
	 * message content.
	 */
	private java.lang.String content;
	
	/**
	 * message state.
	 */
	private java.lang.String state;

	/**
	 * associated account.
	 */
	private Account account;
	
	/**
	 * associated template.
	 */
	private Template template;
	
	/**
	 * message sender.
	 */
	private Person sender;
	
	/**
	 * message service.
	 */
	private Service service;
	
	/**
	 * associated mail.
	 */
	private Mail mail;
	
	/**
	 * message group sender.
	 */
	private BasicGroup groupSender;
	
	/**
	 * message group recipient.
	 */
	private BasicGroup groupRecipient;

	/**
	 * collection of recipients.
	 */
	private java.util.Set<Recipient> recipients;
	
	/**
	 * collection of supervisors.
	 */
	private java.util.Set<Person> supervisors;

	/**
	 * Bean constructor.
	 */
	public Message() {
		super();
	}

	/**
	 * Bean constructor par copie.
	 */
	public Message(final Message message) {
		this.setId(message.getId());
		this.setDate(message.getDate());
		this.setAccount(message.getAccount());
		this.setSender(message.getSender());
		this.setService(message.getService());
		this.setGroupSender(message.getGroupSender());
		this.setGroupRecipient(message.getGroupRecipient());
		this.setContent(message.getContent());
		this.setState(message.getState());
		this.setMail(message.getMail());
	}

	/**
	 * Constructor for required fields.
	 */
	public Message(
		final java.lang.Integer id,
		final Account account,
		final Person sender,
		final Service service,
		final BasicGroup groupSender,
		final BasicGroup groupRecipient,
		final java.lang.String content,
		final java.lang.String state) {
		this.setId(id);
		this.setAccount(account);
		this.setSender(sender);
		this.setService(service);
		this.setGroupSender(groupSender);
		this.setGroupRecipient(groupRecipient);
		this.setContent(content);
		this.setState(state);
	}





	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="MSG_ID"
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
	 * Return the value associated with the column: MSG_DATE.
	 */
	public java.util.Date getDate() {
		return date;
	}

	/**
	 * Set the value related to the column: MSG_DATE.
	 * @param date the MSG_DATE value
	 */
	public void setDate(final java.util.Date date) {
		this.date = date;
	}



	/**
	 * Return the value associated with the column: MSG_CONTENT.
	 */
	public java.lang.String getContent() {
		return content;
	}

	/**
	 * Set the value related to the column: MSG_CONTENT.
	 * @param content the MSG_CONTENT value
	 */
	public void setContent(final java.lang.String content) {
		this.content = content;
	}



	/**
	 * Return the value associated with the column: MSG_STATE.
	 */
	@Deprecated
	public java.lang.String getState() {
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
	public void setState(final java.lang.String state) {
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
	public java.util.Set<Recipient> getRecipients() {
		return recipients;
	}

	/**
	 * Set the value related to the column: Recipients.
	 * @param recipients the Recipients value
	 */
	public void setRecipients(final java.util.Set<Recipient> recipients) {
		this.recipients = recipients;
	}

	/**
	 * add a recipient to the collection.
	 * @param recipient
	 */
	public void addToRecipients(final Recipient recipient) {
		if (null == getRecipients()) {
			setRecipients(new java.util.TreeSet<Recipient>());
		}
		getRecipients().add(recipient);
	}



	/**
	 * Return the value associated with the column: Supervisors.
	 */
	public java.util.Set<Person> getSupervisors() {
		return supervisors;
	}

	/**
	 * Set the value related to the column: Supervisors.
	 * @param supervisors the Supervisors value
	 */
	public void setSupervisors(final java.util.Set<Person> supervisors) {
		this.supervisors = supervisors;
	}

	/**
	 * add a person to the collection of supervisors.
	 * @param person
	 */
	public void addToSupervisors(final Person person) {
		if (null == getSupervisors()) {
			setSupervisors(new java.util.TreeSet<Person>());
		}
		getSupervisors().add(person);
	}




	/**
	 * @see java.lang.Object#hashCode()
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
		return "Message#" + hashCode() + "[id=[" + id + "], content=[" + content 
		+ "], state=[" + state +  "]]";
	}

	//////////////////////////////////////////////////////////////
	// Getter of userGroupLabel
	//////////////////////////////////////////////////////////////
	/**
	 * @return the userGroupLabel
	 */
	public String getUserGroupLabel() {
		return groupSender.getLabel();
		
	}

	//////////////////////////////////////////////////////////////
	// Getter of userAccountLabel
	//////////////////////////////////////////////////////////////

	/**
	 * @return the userAccountLabel
	 */
	public String getUserAccountLabel() {
		return account.getLabel();

	}
	
}