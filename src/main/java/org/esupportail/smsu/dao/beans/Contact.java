package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Contact implements Serializable {
    private static final long serialVersionUID = 1280606540897897907L;

    private java.lang.Integer id;
    private java.lang.String label;
    private java.lang.String owner; // user who has rights on the contact
    private java.lang.String phones; // space separated phone numbers
    private java.util.Set<CustomizedGroup> sharedWith;

    public java.lang.Integer getId() { return id; }
    public java.lang.String getLabel() { return label; }
    public java.lang.String getOwner() { return owner; }
    public java.lang.String getPhones() { return phones; }
    public java.util.Set<CustomizedGroup> getSharedWith() { return sharedWith; }
    
    public void setId(final java.lang.Integer id) { this.id = id; }
    public void setLabel(final java.lang.String label) { this.label = label; }
    public void setOwner(final java.lang.String owner) { this.owner = owner; }
    public void setPhones(final java.lang.String phones) { this.phones = phones; }
    public void setSharedWith(final java.util.Set<CustomizedGroup> sharedWith) { this.sharedWith = sharedWith; }

    public List<String> getPhonesList() {
        return Arrays.asList(StringUtils.splitByWholeSeparator(phones, " "));
    }

    @Override
    public boolean equals(final Object obj) {
        if (id == null || owner == null || obj == null || !(obj instanceof Contact)) {
            return false;
        } else {
            Contact other = (Contact) obj;
            return id.equals(other.getId());
        }
    }

    @Override
    public String toString() {
        return "Contact#" + hashCode() + "[" + 
            "id=[" + id + "]" + ", " + 
            "owner=[" + owner + "]" + ", " + 
            "label=[" + label +  "]" + 
        "]";
    }


}