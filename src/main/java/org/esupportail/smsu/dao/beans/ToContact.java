package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * This is an object that contains data related to the to_recipient table.
 * @hibernate.class
 *  table="to_recipient"
 */
public class ToContact implements Serializable {
    private static final long serialVersionUID = 3486429797504309625L;

    private Contact contact;
    private Message message;

    public Contact getContact() { return contact; }
    public Message getMessage() { return message; }

    public void setContact(final Contact contact) { this.contact = contact; }
    public void setMessage(final Message message) { this.message = message; }

    @Override
    public boolean equals(final Object obj) {
        if (contact == null || message == null || obj == null || !(obj instanceof ToContact)) {
            return false;
        } else {
            ToContact other = (ToContact) obj;
            return contact.equals(other.getContact()) && message.equals(other.getMessage());
        }
    }

}