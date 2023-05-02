package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsu.business.GroupManager;
import org.esupportail.smsu.configuration.SmsuApplication;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.web.beans.UICustomizedGroup;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A bean to manage files.
 */
@RestController
@RequestMapping(value = SmsuApplication.REST_ROOT_URI + "/groups")
@RolesAllowed("FCTN_GESTION_GROUPE")
public class GroupsManagerController {
	
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());

	@Inject private LdapUtils ldapUtils;
	@Inject private DomainService domainService;
	@Inject private GroupManager groupManager;

	@GetMapping
	public List<UICustomizedGroup> allGroups() {
		return groupManager.getAllGroups();
	}
	
	@PostMapping
	public void save(@RequestBody UICustomizedGroup uiCGroup, HttpServletRequest request) {
		checkMandatoryUIParameters(uiCGroup);
		if (groupManager.existsCustomizedGroupLabel(uiCGroup.label)) {
			throw new InvalidParameterException("GROUPE.LABEL.EXIST.ERROR.MESSAGE");
		}
		groupManager.addCustomizedGroup(uiCGroup, request);
	}
	
	@PutMapping("/{id:\\d+}")
	public void update(@PathVariable("id") int id, @RequestBody UICustomizedGroup uiCGroup, HttpServletRequest request) {
		uiCGroup.id = id;
		checkMandatoryUIParameters(uiCGroup);
		if (groupManager.existsCustomizedGroupLabelWithOthersIds(uiCGroup.label, uiCGroup.id)) {
			throw new InvalidParameterException("GROUPE.LABEL.EXIST.ERROR.MESSAGE");
		}
		groupManager.updateCustomizedGroup(uiCGroup, request);	
	}

	@DeleteMapping("/{id:\\d+}")
	public void delete(@PathVariable("id") int id) {
		groupManager.deleteCustomizedGroup(id);
	}
	
	@GetMapping("/accounts")
	public List<String> getAccounts() {
		return domainService.getAccounts();
	}

	@GetMapping("/search")
	@RolesAllowed({"FCTN_GESTION_GROUPE","FCTN_SMS_ENVOI_GROUPES"})
	public List<UserGroup> search(@RequestParam("token") String token) {
		return ldapUtils.searchGroupsByName(token);
	}
	

	//////////////////////////////////////////////////////////////
	private void checkMandatoryUIParameters(UICustomizedGroup uiCGroup) {
		if (StringUtils.isBlank(uiCGroup.label)) {
			throw new InvalidParameterException("GROUPE.LABEL.ERROR.MESSAGE");
		} else if (StringUtils.isBlank(uiCGroup.account)) {
			throw new InvalidParameterException("GROUPE.ACCOUNT.ERROR.MESSAGE");
		} else if (StringUtils.isBlank(uiCGroup.role)) {
			throw new InvalidParameterException("GROUPE.ROLE.ERROR.MESSAGE");
		}
	}

}
