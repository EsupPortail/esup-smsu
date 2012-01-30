package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.portal.ws.client.PortalGroupHierarchy;
import org.esupportail.smsu.dao.beans.Account;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.GroupPaginator;
import org.esupportail.smsu.web.beans.GroupPerson;
import org.esupportail.smsu.web.beans.UIPerson;
import org.esupportail.smsu.web.beans.UIRole;

/**
 * A bean to manage files.
 */
public class GroupsManagerController extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1149078913806276304L;
	
	/**
	 * The group.
	 */
	private CustomizedGroup group;
	
	/**
	 * The account.
	 */
	private Account account;
	
	/**
	 * The role.
	 */
	private UIRole role;
	
	/**
	 * add quota of sms.
	 */
	private String addQuotaSms;
	
	/**
	 * isShowCreateButton.
	 */
	private Boolean isShowCreateButton = false;
		
	/**
	 * list items of RolesList.
	 */
	private List<SelectItem> selectRoleListItems;
	
	/**
	 * list of available accounts
	 */
	private List<SelectItem> availableAccounts;
	
	/**
	 * selectedPerson.
	 */
	private UIPerson selectedPerson;

	/**
	 * Boolean grpAlreadySelected.
	 */
	private Boolean grpAlreadySelected = false;

	/**
	 * 
	 */
	private String selectedGroupFromTree;
	
	/**
	 * persons(supervisors) list.
	 */
	private List<UIPerson> persons = new ArrayList<UIPerson>();

	/**
	 * a person(supervisor) to delete.
	 */
	private UIPerson personToDelete;

	/**
	 * list of the roles.
	 */
	private List<UIRole> allRoles;
	
	/**
	 * {@link UsersSearchController}.
	 */
	private UsersSearchController usersSearchController;
	
	/**
	 * The group paginator.
	 */
	private GroupPaginator paginator;
	/**
	 * The tree model passed to JSP pages.
	 */
	private TreeModelBase treeModel;

	/**
	 * the ldap service.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public GroupsManagerController() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// Access control method
	//////////////////////////////////////////////////////////////
	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isPageAuthorized() {
		//an access control is required for this page.
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return currentUser.getFonctions().contains(FonctionName.FCTN_GESTION_GROUPE.name());

	}
	
	/**
	 * @return true if the current user is allowed to view the page.
	 */
	public boolean isFieldAuthorized(final String fct) {
		//an access control is required for this page.
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return currentUser.getFonctions().contains(fct);

	}
	//////////////////////////////////////////////////////////////
	// Enter method (for Init)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 */
	public String enter()  {
		// rights to enter
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		// initialize data in the page
		init();
		return "navigationAdminGroups";
	}
	
	//////////////////////////////////////////////////////////////
	// Init methods 
	//////////////////////////////////////////////////////////////
	/**
	 * initialize data in the page.
	 */
	private void init()  {
	 	paginator = new GroupPaginator(getDomainService(), ldapUtils);
	 	//treeModel = new TreeModelBase(getRootNode());
		// recuperer la liste des roles
		initSelectRoleListItems();
		// initialize the available accounts list
		initAvailableAccounts();
		if (getCurrentUser().getFonctions().contains(FonctionName.FCTN_GESTION_ROLES_AFFECT.name())) {
			this.isShowCreateButton = true;
		} 
	}

	
	/**
	 * initialize roles in the page.
	 */
	private void initSelectRoleListItems() {
		selectRoleListItems = new ArrayList<SelectItem>();
		SelectItem option = new SelectItem(0, "");
		selectRoleListItems.add(option);
		
		this.allRoles = getDomainService().getAllRoles();
			for (UIRole role : allRoles) {
				option = new SelectItem(role.getId(), role.getName());
				selectRoleListItems.add(option);
			}
	}

	/**
	 * initialize the available accounts list.
	 */
	private void initAvailableAccounts() {
		availableAccounts = new ArrayList<SelectItem>();
		List<Account> accounts = getDomainService().getAccounts();
		for (Account curAccount : accounts) {
			SelectItem option = new SelectItem(curAccount.getId().toString(), curAccount.getLabel());
			availableAccounts.add(option);
		}
	}
	//////////////////////////////////////////////////////////////
	// reset method 
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractContextAwareController#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		paginator = new GroupPaginator(getDomainService(), ldapUtils);
		
	}
	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	
	public void selectGroup() {
		if (this.selectedGroupFromTree != null) {
			group.setLabel(this.selectedGroupFromTree);
		}
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
	 * save action.
	 * @return A String
	 */
	public String save() {
		if (!checkMandatoryUIParameters()) { return null;
		} else 	if (getDomainService().checkCustomizedGroupLabel(group.getLabel())) {
			addErrorMessage(
					"groupForm:GName",
					"GROUPE.LABEL.EXIST.ERROR.MESSAGE",
					null);
			return null;
		} else {
		getDomainService().addCustomizedGroup(group, role, account, persons);
		return "navigationAdminGroups";
		}
	}
	
	/**
	 * update action.
	 * @return A String
	 */
	public String update() {
		if (!checkMandatoryUIParameters()) { return null;
		} else if (getDomainService().checkCustomizedGroupLabelWithOthersIds(group.getLabel(), group.getId())) {
			addErrorMessage(
					"groupForm:GName",
					"GROUPE.LABEL.EXIST.ERROR.MESSAGE",
					null);
			return null;
		} else 	if ((addQuotaSms == null) || (addQuotaSms.equals(""))) {
					 getDomainService().updateCustomizedGroup(group, role, account, 
							 Long.parseLong("0"), persons);
				} else {
					 getDomainService().updateCustomizedGroup(group, role, account, 
							 Long.parseLong(addQuotaSms), persons);	
				}
		  return "navigationAdminGroups";
	}
	
	/**
	 * delete action and reload paginator.
	 * @return A String
	 */
	public String delete()  {
		getDomainService().deleteCustomizedGroup(group);
		reset();
		return "navigationAdminGroups";
	}
	
	//////////////////////////////////////////////////////////////
	// Tools methods
	//////////////////////////////////////////////////////////////
	private Boolean checkMandatoryUIParameters() {
		Boolean inc = true;
		if ((group.getLabel().trim().equals("")) || (group.getLabel().trim() == null)) {
			addErrorMessage(
					"groupForm:GName",
					"GROUPE.LABEL.ERROR.MESSAGE",
					null);
			        inc =  false;
		} else if ((account.getLabel().trim().equals("")) || (account.getLabel().trim() == null)) {
			addErrorMessage(
					"groupForm:AName",
					"GROUPE.ACCOUNT.ERROR.MESSAGE",
					null);
					inc =  false;
		} else if (role.getId() == 0) {
			addErrorMessage(
					"groupForm:selectRoleMenu",
					"GROUPE.ROLE.ERROR.MESSAGE",
					null);
					inc =  false;
		}
	
		return inc;
	}
	
	
	/**
	 * return action and reload paginator.
	 * @return A String
	 */
	public String comeback()  {
		reset();
		return "navigationAdminGroups";
	}
	
	public String resetConsumption() {
		group.setConsumedSms(Long.parseLong("0"));
		group.setQuotaSms(Long.parseLong("0"));
		getDomainService().updateCustomizedGroup(group);
		return "";
	}
	/**
	 * create action.
	 * @return A String
	 */
	public String create() {
		usersSearchController.setLdapPersons(null);
		usersSearchController.setLdapUid(null);
		persons = new ArrayList<UIPerson>();
		this.group = new CustomizedGroup();
		this.group.setQuotaSms(new Long(0));
		this.group.setQuotaOrder(new Long(1));
		this.account = new Account();
		this.role = new UIRole();
		
		return "navigationCreateGroup";
	}
	
	/**
	 * display action.
	 * @return A String
	 */
	public String display() {
		usersSearchController.setLdapPersons(null);
		usersSearchController.setLdapUid(null);
		persons = getDomainService().getPersonsByIdCustomizedGroup(group.getId());
				
		this.account = new Account();
		this.account.setId(group.getAccount().getId());
		this.account.setLabel(group.getAccount().getLabel());
		
		this.role = new UIRole();
		this.role.setId(group.getRole().getId());
		this.role.setName(group.getRole().getName());
		
		this.addQuotaSms = null;
	
		return "navigationDetailGroup";
	}
	
	/**
	 * add a person to persons.
	 */
	public void selectPerson() {
		if (!this.persons.contains(this.selectedPerson)) {
			this.persons.add(this.selectedPerson);
		}
	}

	/**
	 * delete a person from persons.
	 */
	public void deletePerson() {
		if (this.persons.contains(this.personToDelete)) {
			this.persons.remove(this.personToDelete);
			GroupPerson group = new GroupPerson();
			if (this.personToDelete.getClass().equals(group.getClass())) {
				this.grpAlreadySelected = false;
			}
		}
	}

	//////////////////////////////////////////////////////////////
	// Others
	//////////////////////////////////////////////////////////////
	/**
	 * @return the paginator
	 */
	public GroupPaginator getPaginator() {
		return paginator;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of group
	//////////////////////////////////////////////////////////////
	/**
	 * @param group the group to set
	 */
	public void setGroup(final CustomizedGroup group) {
		this.group = group;
	}

	/**
	 * @return the group
	 */
	public CustomizedGroup getGroup() {
		return group;
	}
	
	//////////////////////////////////////////////////////////////
	// Setter of usersSearchController
	//////////////////////////////////////////////////////////////
	/**
	 * @param usersSearchController
	 */
	public void setUsersSearchController(final UsersSearchController usersSearchController) {
		this.usersSearchController = usersSearchController;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of account
	//////////////////////////////////////////////////////////////
	/**
	 * @param account the account to set
	 */
	public void setAccount(final Account account) {
		this.account = account;
	}

	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of persons
	//////////////////////////////////////////////////////////////
	public List<UIPerson> getPersons() {
		return persons;
	}

	public void setPersons(final List<UIPerson> persons) {
		this.persons = persons;
	}
	//////////////////////////////////////////////////////////////
	// Getter and Setter of role
	//////////////////////////////////////////////////////////////
	/**
	 * @param role the role to set
	 */
	public void setRole(final UIRole role) {
		this.role = role;
	}

	/**
	 * @return the role
	 */
	public UIRole getRole() {
		return role;
	}

	private Long getLongNewValue(javax.faces.event.ValueChangeEvent e) {
		Object o = e.getNewValue();
		if (o == null)
			return new Long(0);
		else if (o instanceof Long)
			return (Long) o;
		else {
			String s = (String) o;
			return s.equals("") ? new Long(0) : Long.parseLong(s);
		}
	}

	public void addQuotaSmsValueChanged(javax.faces.event.ValueChangeEvent e) {
		setAddQuotaSms((String) e.getNewValue());
	}
	public void quotaOrderValueChanged(javax.faces.event.ValueChangeEvent e) {
		group.setQuotaOrder(getLongNewValue(e));
	}
	public void quotaSmsValueChanged(javax.faces.event.ValueChangeEvent e) {
		group.setQuotaSms(getLongNewValue(e));
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of addQuotaSms
	//////////////////////////////////////////////////////////////
	/**
	 * @param addQuotaSms the addQuotaSms to set
	 */
	public void setAddQuotaSms(final String addQuotaSms) {
		this.addQuotaSms = addQuotaSms;
	}

	/**
	 * @return the addQuotaSms
	 */
	public String getAddQuotaSms() {
		return addQuotaSms;
	}

	public String getGroupDisplayName() {
		String displayName;
		try {
			displayName = ldapUtils.getUserDisplayNameByUserUid(group.getLabel());
		} catch (LdapUserNotFoundException e) {
			displayName = ldapUtils.getGroupNameByUid(group.getLabel());
			if (displayName == null) {
				logger.debug("Group not found : " + group.getLabel());
				displayName = group.getLabel();
			}	
		}
		return displayName;
	}
	//////////////////////////////////////////////////////////////
	// Getter and Setter of grpAlreadySelected
	//////////////////////////////////////////////////////////////
	/**
	 * @param grpAlreadySelected
	 */
	public void setGrpAlreadySelected(final Boolean grpAlreadySelected) {
		this.grpAlreadySelected = grpAlreadySelected;
	}

	/**
	 * @return grpAlreadySelected
	 */
	public Boolean getGrpAlreadySelected() {
		return grpAlreadySelected;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of personToDelete
	//////////////////////////////////////////////////////////////
	/**
	 * @return the personToDelete
	 */
	public UIPerson getPersonToDelete() {
		return personToDelete;
	}

	/**
	 * @param personToDelete the personToDelete to set
	 */
	public void setPersonToDelete(final UIPerson personToDelete) {
		this.personToDelete = personToDelete;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of selectedPerson
	//////////////////////////////////////////////////////////////
	/**
	 * @return the selectedPerson
	 */
	public UIPerson getSelectedPerson() {
		return selectedPerson;
	}
	
	/**
	 * @param selectedPerson the selectedPerson to set
	 */
	public void setSelectedPerson(final UIPerson selectedPerson) {
		this.selectedPerson = selectedPerson;
	}


	//////////////////////////////////////////////////////////////
	// Getter of selectedRoleListItems
	//////////////////////////////////////////////////////////////
	/**
	 * @return selectRecipientListItems
	 */
	public List<SelectItem> getSelectRoleListItems() {
		return selectRoleListItems;
	} 
	
	//////////////////////////////////////////////////////////////
	// Getter of allRoles
	//////////////////////////////////////////////////////////////
	/**
	 * @return the allRoles
	 */
	public List<UIRole> getAllRoles() {
		return allRoles;
	}

	/**
	 * @param isShowCreateButton the isShowCreateButton to set
	 */
	public void setIsShowCreateButton(final Boolean isShowCreateButton) {
		this.isShowCreateButton = isShowCreateButton;
	}

	/**
	 * @return the isShowCreateButton
	 */
	public Boolean getIsShowCreateButton() {
		return isShowCreateButton;
	}

	public void setSelectedGroupFromTree(final String selectedGroupFromTree) {
		this.selectedGroupFromTree = selectedGroupFromTree;
	}

	public String getSelectedGroupFromTree() {
		return selectedGroupFromTree;
	}

	public void setTreeModel(final TreeModelBase treeModel) {
		this.treeModel = treeModel;
	}

	public TreeModelBase getTreeModel() {
		if (treeModel == null) {
			treeModel = new TreeModelBase(getRootNode());
		}
		return treeModel;
	}

	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}

	public void setAvailableAccounts(final List<SelectItem> availableAccounts) {
		this.availableAccounts = availableAccounts;
	}

	public List<SelectItem> getAvailableAccounts() {
		return availableAccounts;
	}

	
	
	
}
