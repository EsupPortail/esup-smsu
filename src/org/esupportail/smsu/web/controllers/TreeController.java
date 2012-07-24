/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 */
package org.esupportail.smsu.web.controllers;

import java.util.List;

import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.esupportail.portal.ws.client.PortalGroupHierarchy;
import org.esupportail.smsu.services.ldap.LdapUtils;

/**
 * @author xphp8691
 *
 */
public class TreeController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -5568857986030068711L;

	/**
	 * The tree model passed to JSP pages.
	 */
	private TreeModelBase treeModel;

	/**
	 * the ldap service.
	 */
	private LdapUtils ldapUtils;

	/**
	 * Bean constructor.
	 */
	public TreeController() {
		super();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	/**
	 * @see org.esupportail.example.web.controllers.AbstractDomainAwareBean#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		treeModel = null;
	}

	/**
	 * JSF callback.
	 * @return a string.
	 */
	public String refreshTree() {
		treeModel = new TreeModelBase(getRootNode());
		return null;
	}

	/**
	 * @return the root node.
	 */
	private TreeNode getRootNode() {
		PortalGroupHierarchy groupHierarchy = ldapUtils.getPortalGroupHierarchy();

		TreeNode rootNode = getChildrenNodes(groupHierarchy);

		return rootNode;
	}
	
	/**
	 * @param groupHierarchy
	 * @return the children nodes of a portal group hierarchy.
	 */
	@SuppressWarnings("unchecked")
	private TreeNodeBase getChildrenNodes(final PortalGroupHierarchy groupHierarchy) {
		String groupDescription = groupHierarchy.getGroup().getName();
		String groupIdentifer = groupHierarchy.getGroup().getId();
		List<PortalGroupHierarchy> childs = groupHierarchy.getSubHierarchies(); 
		 
		Boolean isGroupLeaf = true;
		if (childs != null) {
			isGroupLeaf = false;
		}
		TreeNodeBase node = new TreeNodeBase("group", groupDescription, groupIdentifer, isGroupLeaf);
		
		if (!isGroupLeaf) {
			for (PortalGroupHierarchy child : childs) {
				TreeNodeBase childNode = getChildrenNodes(child);
				node.getChildren().add(childNode);
			}
		}
		
		return node;
	}

	/**
	 * @return the tree of the projects
	 */
	public TreeModel getTreeModel() {
		if (treeModel == null) {
			treeModel = new TreeModelBase(getRootNode());
		}
		return treeModel;
	}

	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}

	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}


}
