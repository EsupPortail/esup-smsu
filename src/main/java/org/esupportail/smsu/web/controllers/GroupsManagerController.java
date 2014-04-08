package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
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
@Path("/groups")
@RolesAllowed("FCTN_GESTION_GROUPE")
public class GroupsManagerController {
	
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());

	@Autowired private LdapUtils ldapUtils;
	@Autowired private DomainService domainService;
	@Autowired private GroupManager groupManager;
	
//	TODO
	// private TreeNode getRootNode() {
//		PortalGroupHierarchy groupHierarchy = ldapUtils.getPortalGroupHierarchy();
//		TreeNode rootNode = getChildrenNodes(groupHierarchy);
//		return rootNode;
//	}

//	private TreeNodeBase getChildrenNodes(final PortalGroupHierarchy groupHierarchy) {
//		String groupDescription = groupHierarchy.getGroup().getName();
//		String groupIdentifer = groupHierarchy.getGroup().getId();
//		List<PortalGroupHierarchy> childs = groupHierarchy.getSubHierarchies(); 
//		 
//		Boolean isGroupLeaf = true;
//		if (childs != null) {
//			isGroupLeaf = false;
//		}
//		TreeNodeBase node = new TreeNodeBase("group", groupDescription, groupIdentifer, isGroupLeaf);
//		
//		if (!isGroupLeaf) {
//			for (PortalGroupHierarchy child : childs) {
//				TreeNodeBase childNode = getChildrenNodes(child);
//				node.getChildren().add(childNode);
//			}
//		}
//		
//		return node;
//	}

	@GET
	@Produces("application/json")
	public List<UICustomizedGroup> allGroups() {
		return groupManager.getAllGroups();
	}
	
	@POST
	public void save(UICustomizedGroup uiCGroup, @Context HttpServletRequest request) {
		checkMandatoryUIParameters(uiCGroup);
		if (groupManager.existsCustomizedGroupLabel(uiCGroup.label)) {
			throw new InvalidParameterException("GROUPE.LABEL.EXIST.ERROR.MESSAGE");
		}
		groupManager.addCustomizedGroup(uiCGroup, request);
	}
	
	@PUT
	@Path("/{id:\\d+}")
	public void update(@PathParam("id") int id, UICustomizedGroup uiCGroup, @Context HttpServletRequest request) {
		uiCGroup.id = id;
		checkMandatoryUIParameters(uiCGroup);
		if (groupManager.existsCustomizedGroupLabelWithOthersIds(uiCGroup.label, uiCGroup.id)) {
			throw new InvalidParameterException("GROUPE.LABEL.EXIST.ERROR.MESSAGE");
		}
		groupManager.updateCustomizedGroup(uiCGroup, request);	
	}

	@DELETE
	public void delete(int id)  {
		groupManager.deleteCustomizedGroup(id);
	}
	
	@GET
	@Produces("application/json")
	@Path("/accounts")
	public List<String> getAccounts() {
		return domainService.getAccounts();
	}

	@GET
	@Produces("application/json")
	@Path("/search")
	@RolesAllowed({"FCTN_GESTION_GROUPE","FCTN_SMS_ENVOI_GROUPES"})
	public List<UserGroup> search(@QueryParam("token") String token) {
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
