package org.esupportail.smsu.business;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.services.GroupUtils;
import org.esupportail.smsu.web.beans.UIContact;
import org.esupportail.smsu.dao.beans.Contact;
import org.esupportail.smsu.dao.beans.CustomizedGroup;

import javax.inject.Inject;

public class ContactManager {

    @Inject private DaoService daoService;
    @Inject private GroupUtils groupUtils;

    private final Logger logger = Logger.getLogger(getClass());


    public List<UIContact> getContacts(String userIdentifier) {
        List<UIContact> listContacts = new ArrayList<>();
        for (Contact contact : daoService.getContactsByOwner(userIdentifier)) {
            listContacts.add(convertToUI(contact));
        }
        return listContacts;
    }
    
    public void addOrUpdateContact(final UIContact contact, String user, String loggedUserSortedAttributes) {
        Contact c = convertFromUI(contact, user, loggedUserSortedAttributes);
        if (contact.id != null) daoService.updateContact(c); daoService.addContact(c);
    }
    
    public void deleteContact(int id, String user) {
        Contact contact = getContactById(id);
        checkExistingContactOwner(contact, user);
        daoService.deleteContact(contact);
    }

    public Map<Integer, String> searchOwnerOrSharedContacts(String token, String user, String loggedUserSortedAttributes) {
        return daoService.searchOwnerOrSharedContacts(token, user, groupUtils.getCustomizedGroups(user, loggedUserSortedAttributes))
            .stream().collect(Collectors.toMap(Contact::getId, Contact::getLabel, (x,y) -> x));
    }

    public void contactsValidation(Set<Contact> contacts, String login, String loggedUserSortedAttributes) {
        if (contacts == null) return;
        List<CustomizedGroup> userCGroups = groupUtils.getCustomizedGroups(login, loggedUserSortedAttributes);
        for (Contact contact : contacts) contactValidation(contact, login, userCGroups);
    }

    private void contactValidation(Contact contact, String login, List<CustomizedGroup> userCGroups) {
        if (login.equals(contact.getOwner())) {
            // ok
        } else if (!Collections.disjoint(contact.getSharedWith(), userCGroups)) {
            // ok
        } else {
            throw new InvalidParameterException("user " + login + " can not send to contact " + contact.getId());
        }
    }

    public boolean checkAllowedSendContact(Integer contactId, List<CustomizedGroup> userCGroups) {
        return true;
    }

    private Contact convertFromUI(final UIContact contact, String user, String loggedUserSortedAttributes) {
        Contact c = new Contact();
        if (contact.id != null) {
            c = getContactById(contact.id);
            checkExistingContactOwner(c, user);
        }
        c.setLabel(contact.label);
        c.setPhones(String.join(" ", contact.phones));
        c.setSharedWith(convertFromUI_sharedWith(contact.sharedWith, user, loggedUserSortedAttributes));
        c.setOwner(user);
        return c;
    }
    
    private Set<CustomizedGroup> convertFromUI_sharedWith(List<String> shares, String user, String loggedUserSortedAttributes) {
        if (shares == null) return null;

        Map<String, CustomizedGroup> allowed_shares = new HashMap<>();
        for (CustomizedGroup cg : groupUtils.getCustomizedGroups(user, loggedUserSortedAttributes)) {
            allowed_shares.put(cg.getLabel(), cg);
        }
        Set<CustomizedGroup> r = new HashSet<>();
        for (String share : shares) {
            CustomizedGroup cg = allowed_shares.get(share);
            if (cg == null) throw new InvalidParameterException("invalid share " + share + " for user " + user);
            r.add(cg);
        }
        return r;
    }

    private UIContact convertToUI(Contact contact) {
        UIContact c = new UIContact();
        c.id = contact.getId();
        c.label = contact.getLabel();
        c.phones = contact.getPhonesList();
        c.sharedWith = contact.getSharedWith().stream().map(cg -> cg.getLabel()).collect(Collectors.toList());
        return c;
    }

    private void checkExistingContactOwner(Contact contact, String expected_owner) {
        if (!contact.getOwner().equals(expected_owner)) {
            logger.error("can not change contact owner: " + contact.getOwner() + " vs " + expected_owner);
            throw new InvalidParameterException("can not change contact owner");
        }
    }

    private Contact getContactById(final Integer id) {
        Contact c = daoService.getContactById(id);
        if (c == null) throw new InvalidParameterException("unknown id");
        return c;
    }
    
}
