package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import org.apache.log4j.Logger;
import org.esupportail.smsu.business.GroupManager;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.web.beans.UICustomizedGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * A bean to manage files.
 */
@RequestMapping(value = "/groups")
@RolesAllowed("FCTN_GESTION_GROUPE")
public class GroupsManagerController {
	
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());

	@Autowired private LdapUtils ldapUtils;
	@Autowired private DomainService domainService;
	@Autowired private GroupManager groupManager;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<UICustomizedGroup> allGroups() {
		return groupManager.getAllGroups();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void save(@RequestBody UICustomizedGroup uiCGroup, HttpServletRequest request) {
		checkMandatoryUIParameters(uiCGroup);
		if (groupManager.existsCustomizedGroupLabel(uiCGroup.label)) {
			throw new InvalidParameterException("GROUPE.LABEL.EXIST.ERROR.MESSAGE");
		}
		groupManager.addCustomizedGroup(uiCGroup, request);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}")
	public void update(@PathVariable("id") int id, @RequestBody UICustomizedGroup uiCGroup, HttpServletRequest request) {
		uiCGroup.id = id;
		checkMandatoryUIParameters(uiCGroup);
		if (groupManager.existsCustomizedGroupLabelWithOthersIds(uiCGroup.label, uiCGroup.id)) {
			throw new InvalidParameterException("GROUPE.LABEL.EXIST.ERROR.MESSAGE");
		}
		groupManager.updateCustomizedGroup(uiCGroup, request);	
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id:\\d+}")
	public void delete(@PathVariable("id") int id) {
		groupManager.deleteCustomizedGroup(id);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/accounts")
	@ResponseBody
	public List<String> getAccounts() {
		return domainService.getAccounts();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/search")
	@ResponseBody
	@RolesAllowed({"FCTN_GESTION_GROUPE","FCTN_SMS_ENVOI_GROUPES"})
	public List<UserGroup> search(@RequestParam(value = "token", required = false) String token) {
		return ldapUtils.searchGroupsByName(token);
	}
	

	//////////////////////////////////////////////////////////////
	private void checkMandatoryUIParameters(UICustomizedGroup uiCGroup) {
		if (!StringUtils.hasText(uiCGroup.label.trim())) {
			throw new InvalidParameterException("GROUPE.LABEL.ERROR.MESSAGE");
		} else if (!StringUtils.hasText(uiCGroup.account.trim())) {
			throw new InvalidParameterException("GROUPE.ACCOUNT.ERROR.MESSAGE");
		} else if (!StringUtils.hasText(uiCGroup.role)) {
			throw new InvalidParameterException("GROUPE.ROLE.ERROR.MESSAGE");
		}
	}

}
