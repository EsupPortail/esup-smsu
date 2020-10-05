package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

public class ContactShare  implements Serializable {
    private static final long serialVersionUID = 3486429797504309625L;

    private Contact contact;
    private CustomizedGroup customizedGroup;

    public Contact getContact() { return contact; }
    public CustomizedGroup getCustomizedGroup() { return customizedGroup; }

    public void setContact(final Contact contact) { this.contact = contact; }
    public void setCustomizedGroup(final CustomizedGroup customizedGroup) { this.customizedGroup = customizedGroup; }

    @Override
    public boolean equals(final Object obj) {
        if (contact == null || customizedGroup == null || obj == null || !(obj instanceof ContactShare)) {
            return false;
        } else {
            ContactShare other = (ContactShare) obj;
            return contact.equals(other.getContact()) && customizedGroup.equals(other.getCustomizedGroup());
        }
    }

}