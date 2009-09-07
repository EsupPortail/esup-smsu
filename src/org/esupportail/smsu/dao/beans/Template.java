package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represent template of mail or SMS.
 */
public class Template  implements Serializable {

	/**
	 * Hibernate reference for template.
	 */
	public static final String REF = "Template";

	/**
	 * Hibernate property for the signature.
	 */
	public static final String PROP_SIGNATURE = "Signature";
	
	/**
	 * Hibernate property for the heading.
	 */
	public static final String PROP_HEADING = "Heading";
	
	/**
	 * Hibernate property for the body.
	 */
	public static final String PROP_BODY = "Body";
	
	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "Label";
	
	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 1044379115170016078L;

	/**
	 * template identifier.
	 */
	private java.lang.Integer id;

	/**
	 * template label.
	 */
	private java.lang.String label;
	
	/**
	 * template heading.
	 */
	private java.lang.String heading;
	
	/**
	 * template body.
	 */
	private java.lang.String body;
	
	/**
	 * template signature.
	 */
	private java.lang.String signature;

	/**
	 * collection of messages that used this template.
	 */
	private java.util.Set<Message> messages;

	/**
	 * collection of mails that used this template.
	 */
	private java.util.Set<Mail> mails;

	/**
	 * Bean constructor.
	 */
	public Template() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public Template(
		final java.lang.Integer id,
		final java.lang.String label) {

		this.setId(id);
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="TPL_ID"
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
	 * Return the value associated with the column: TPL_LABEL.
	 */
	public java.lang.String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: TPL_LABEL.
	 * @param label the TPL_LABEL value
	 */
	public void setLabel(final java.lang.String label) {
		this.label = label;
	}



	/**
	 * Return the value associated with the column: TPL_HEADING.
	 */
	public java.lang.String getHeading() {
		return heading;
	}

	/**
	 * Set the value related to the column: TPL_HEADING.
	 * @param heading the TPL_HEADING value
	 */
	public void setHeading(final java.lang.String heading) {
		this.heading = heading;
	}



	/**
	 * Return the value associated with the column: TPL_BODY.
	 */
	public java.lang.String getBody() {
		return body;
	}

	/**
	 * Set the value related to the column: TPL_BODY.
	 * @param body the TPL_BODY value
	 */
	public void setBody(final java.lang.String body) {
		this.body = body;
	}



	/**
	 * Return the value associated with the column: TPL_SIGNATURE.
	 */
	public java.lang.String getSignature() {
		return signature;
	}

	/**
	 * Set the value related to the column: TPL_SIGNATURE.
	 * @param signature the TPL_SIGNATURE value
	 */
	public void setSignature(final java.lang.String signature) {
		this.signature = signature;
	}



	/**
	 * Return the value associated with the column: Messages.
	 */
	public java.util.Set<Message> getMessages() {
		return messages;
	}

	/**
	 * Set the value related to the column: Messages.
	 * @param messages the Messages value
	 */
	public void setMessages(final java.util.Set<Message> messages) {
		this.messages = messages;
	}

	/**
	 * add a message to the collection of messages.
	 * @param message
	 */
	public void addToMessages(final Message message) {
		if (null == getMessages()) {
			setMessages(new java.util.TreeSet<Message>());
		}
		getMessages().add(message);
	}



	/**
	 * Return the value associated with the column: Mails.
	 */
	public java.util.Set<Mail> getMails() {
		return mails;
	}

	/**
	 * Set the value related to the column: Mails.
	 * @param mails the Mails value
	 */
	public void setMails(final java.util.Set<Mail> mails) {
		this.mails = mails;
	}

	/**
	 * Add a mail to the collection of mails.
	 * @param mail
	 */
	public void addToMails(final Mail mail) {
		if (null == getMails()) {
			setMails(new java.util.TreeSet<Mail>());
		}
		getMails().add(mail);
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Template)) {
			return false;
		} else {
			Template template = (Template) obj;
			if (null == this.getId() || null == template.getId()) {
				return false;
			} else {
				return this.getId().equals(template.getId());
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
		return "Template#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+ "], heading=[" + heading + "], body=[" + body + "], signature=[" + signature + "]]";
	}

}