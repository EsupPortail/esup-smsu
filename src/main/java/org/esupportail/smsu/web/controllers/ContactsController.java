package org.esupportail.smsu.web.controllers;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.apache.log4j.Logger;
import org.esupportail.smsu.web.AuthAndRoleAndMiscFilter;
import org.esupportail.smsu.web.beans.UIContact;
import org.esupportail.smsu.business.ContactManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/contacts")
@RolesAllowed("FCTN_CONTACT_CREATE")
public class ContactsController {

    @SuppressWarnings("unused")
    private final Logger logger = Logger.getLogger(getClass());

    @Inject private ContactManager contactManager;

    @RequestMapping(method = RequestMethod.GET)
    public List<UIContact> getContacts(HttpServletRequest request) {
        return contactManager.getContacts(request.getRemoteUser());
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public void save(@RequestBody UIContact contact, HttpServletRequest request) {        
        if (contact.id != null) throw new InvalidParameterException("no id expected");
        saveOrUpdate(contact, request);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}")
    public void update(@PathVariable("id") int id, @RequestBody UIContact contact, HttpServletRequest request) {
        contact.id = id;
        saveOrUpdate(contact, request);
    }

    private void saveOrUpdate(UIContact contact, HttpServletRequest request) {
        if (!request.isUserInRole("FCTN_CONTACT_SHARE")) contact.sharedWith = null;
        contactManager.addOrUpdateContact(contact, request.getRemoteUser(), AuthAndRoleAndMiscFilter.get_loggedUserSortedAttributes(request));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id:\\d+}")
    public void delete(@PathVariable("id") int id, HttpServletRequest request)  {
        contactManager.deleteContact(id, request.getRemoteUser());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/search_sendContacts")
    public Map<Integer, String> search_sendContacts(@RequestParam("token") String token, HttpServletRequest request) {
        return contactManager.searchOwnerOrSharedContacts(token, request.getRemoteUser(), AuthAndRoleAndMiscFilter.get_loggedUserSortedAttributes(request));
    }

}
