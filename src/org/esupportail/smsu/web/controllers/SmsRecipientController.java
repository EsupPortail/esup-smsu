package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.component.html.ext.HtmlPanelGroup;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.web.beans.TreeModelBase;
import org.esupportail.portal.ws.client.PortalGroupHierarchy;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.GroupRecipient;
import org.esupportail.smsu.web.beans.PhoneNumberRecipient;
import org.esupportail.smsu.web.beans.UiRecipient;

/**
 * @author xphp8691
 *
 */
public class SmsRecipientController extends AbstractContextAwareController {


	/**
	 * serial version UID.
	 */
	private static final long serialVersionUID = 234380160181697485L;

	/**
	 * the select component.
	 */
	private HtmlSelectOneMenu selectTypeDest;

	/**
	 * the selected recipient type.
	 */
	private String recipientType;

	/**
	 * the group panel grid component.
	 */
	private HtmlPanelGroup groupPanelGrid;

	/**
	 * the ldap search panel grid component.
	 */
	private HtmlPanelGroup ldapSearchPanelGrid;

	/**
	 * the phone number ListPanelGridComponent.
	 */
	private HtmlPanelGroup phoneNumberListPanelGrid;

	/**
	 * The ldap panel grid component.
	 */
	private HtmlPanelGroup ldapRequestPanelGrid;

	/**
	 * the selected recipient group.
	 */
	private String selectedRecipientGroup;

	/**
	 * ldapUid.
	 */
	private String ldapUid;

	/**
	 * selectedRecipient.
	 */
	private UiRecipient selectedRecipient;

	/**
	 * recipientList.
	 */
	private List<UiRecipient> recipientList;

	/**
	 * recipient list.
	 */
	private List<UiRecipient> recipients;

	/**
	 * a recipient to delete.
	 */
	private UiRecipient recipientToDelete;

	/**
	 * a phone number to add.
	 */
	private String phoneNumberToAdd;

	/**
	 * the phone number validation pattern.
	 */
	private String phoneNumberPattern;

	/**
	 * the ldap service.
	 */
	private LdapUtils ldapUtils;


	/**
	 * destTypeOptions.
	 */
	private List<SelectItem> destTypeOptions;

	/**
	 * The tree model passed to JSP pages.
	 */
	private TreeModelBase treeModel;

	/**
	 * 
	 */
	private UsersSearchController userSearchController;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * the constructor.
	 */
	public SmsRecipientController() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * add a phone number to the recipient list.
	 */
	public void addPhoneNumber() {
		if (this.phoneNumberToAdd != null) {
			if (this.phoneNumberToAdd.length() > 0) {
				if (!this.phoneNumberPattern.trim().equals("")) {
					if (this.phoneNumberToAdd.matches(this.phoneNumberPattern)) {
						UiRecipient recToAdd = new PhoneNumberRecipient(this.phoneNumberToAdd,
								this.phoneNumberToAdd, null, this.phoneNumberToAdd);
						if (!this.recipients.contains(recToAdd)) {
							this.recipients.add(recToAdd);
						}
					} else {
						this.phoneNumberToAdd = "";
						logger.error("Error phone number");
						addErrorMessage(null, "SENDSMS.MESSAGE.PHONENUMBERERROR");
					}
				}
			}
		}
	}

	/**
	 * add a group to the recipient list.
	 */
	public void addGroupRecipient() {
		if (this.selectedRecipientGroup != null) {
			if (this.selectedRecipientGroup.length() > 0) {
				UiRecipient grpToAdd = new GroupRecipient(this.selectedRecipientGroup, 
						this.selectedRecipientGroup, null, null);
				List<UiRecipient> list = new ArrayList<UiRecipient>(); 
				list.addAll(this.recipients);
				for (UiRecipient uiRecipient : list) {
					if (uiRecipient.getClass().equals(GroupRecipient.class)) {
						this.recipients.remove(uiRecipient);
					}
				}

				this.recipients.add(grpToAdd);

			}
		}
	}

	/**
	 * add a recipient.
	 */
	public void selectRecipient() {
		if (!this.recipients.contains(this.selectedRecipient)) {
			this.recipients.add(this.selectedRecipient);
		}
	}

	/**
	 * add a list of recipients.
	 */
	public void addRecipientList() {
		for (UiRecipient recipient : this.recipientList) {
			if (!this.recipients.contains(recipient)) {
				this.recipients.add(recipient);
			}
		}
	}
	/**
	 * delete a recipient.
	 */
	public void deleteRecipient() {
		if (this.recipients.contains(this.recipientToDelete)) {
			this.recipients.remove(this.recipientToDelete);
			}
	}

	/**
	 * @param e 
	 */
	public void modifTypeDest(final ValueChangeEvent e) {

		groupPanelGrid.setRendered(false);
		ldapRequestPanelGrid.setRendered(false);
		ldapSearchPanelGrid.setRendered(false);
		phoneNumberListPanelGrid.setRendered(false);
		
		userSearchController.setLdapUsers(new ArrayList<UiRecipient>());
		userSearchController.setLdapRequestUsers(new ArrayList<UiRecipient>());
		userSearchController.setLdapValidUsers(new ArrayList<UiRecipient>());
		this.setRecipientType((String) e.getNewValue());

		if (recipientType.equals("t1")) {
			groupPanelGrid.setRendered(true);
		} else if (recipientType.equals("t2")) {
			ldapRequestPanelGrid.setRendered(true);
		} else if (recipientType.equals("t3")) {
			ldapSearchPanelGrid.setRendered(true);
		} else {
			phoneNumberListPanelGrid.setRendered(true);
		}
	}

	/**
	 * clear the recipient list.
	 * @param e 
	 */
	public void clearRecipients(final ValueChangeEvent e) {
		recipients.clear();
	}

	/**
	 * CALLED IN SENDSMSCONTROLLER.INIT.
	 */
	public void init() {
		initDestTypeOptions();
		groupPanelGrid = new HtmlPanelGroup();
		groupPanelGrid.setRendered(false);
		treeModel = new TreeModelBase(getRootNode());

		ldapSearchPanelGrid = new HtmlPanelGroup();
		ldapSearchPanelGrid.setRendered(false);

		phoneNumberListPanelGrid = new HtmlPanelGroup();
		phoneNumberListPanelGrid.setRendered(false);

		ldapRequestPanelGrid = new HtmlPanelGroup();
		ldapRequestPanelGrid.setRendered(false);

		userSearchController.setLdapUsers(new ArrayList<UiRecipient>());
		
		recipients = new ArrayList<UiRecipient>();
		
		if (!destTypeOptions.isEmpty()) {
			if (destTypeOptions.get(0).getValue().equals("t1")) {
				groupPanelGrid.setRendered(true);
			} else if (destTypeOptions.get(0).getValue().equals("t2")) {
				ldapRequestPanelGrid.setRendered(true);
			} else if (destTypeOptions.get(0).getValue().equals("t3")) {
				ldapSearchPanelGrid.setRendered(true);
			} else if (destTypeOptions.get(0).getValue().equals("t4")) {
				phoneNumberListPanelGrid.setRendered(true);
			} 
		}
	}


	/**
	 * initDestTypeOptions method.
	 */
	private void initDestTypeOptions() {
		//an access control is required for type options.
		User currentUser = getCurrentUser();
		destTypeOptions = new ArrayList<SelectItem>();
		SelectItem option;
		
		if (currentUser != null) {
			if (currentUser.getFonctions().contains(FonctionName.FCTN_SMS_ENVOI_ADH.name())) {
				option = new SelectItem("t3", this.getI18nService().getString("SENDSMS.LABEL.USERS"));
				destTypeOptions.add(option);
			}
			if (currentUser.getFonctions().contains(FonctionName.FCTN_SMS_ENVOI_NUM_TEL.name())) {
				option = new SelectItem("t4", this.getI18nService().getString("SENDSMS.LABEL.PHONENUMBERS"));
				destTypeOptions.add(option);
			}
			if (currentUser.getFonctions().contains(FonctionName.FCTN_SMS_ENVOI_GROUPES.name())) {
		option = new SelectItem("t1", this.getI18nService().getString("SENDSMS.LABEL.USERGROUP"));
		destTypeOptions.add(option);
			}
			if (currentUser.getFonctions().contains(FonctionName.FCTN_SMS_REQ_LDAP_ADH.name())) {
		option = new SelectItem("t2", this.getI18nService().getString("SENDSMS.LABEL.LDAP"));
		destTypeOptions.add(option);
			}			
		}
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
	public TreeModelBase getTreeModel() {
		return treeModel;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of selectTypeDest
	//////////////////////////////////////////////////////////////
	/**
	 * @return selectTypeDest
	 */
	public HtmlSelectOneMenu getSelectTypeDest() {
		return selectTypeDest;
	}

	/**
	 * @param selectTypeDest
	 */
	public void setSelectTypeDest(final HtmlSelectOneMenu selectTypeDest) {
		this.selectTypeDest = selectTypeDest;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of groupPanelGrid
	//////////////////////////////////////////////////////////////
	/**
	 * @return groupPanelGrid
	 */
	public HtmlPanelGroup getGroupPanelGrid() {
		return groupPanelGrid;
	}

	/**
	 * @param groupPanelGrid
	 */
	public void setGroupPanelGrid(final HtmlPanelGroup groupPanelGrid) {
		this.groupPanelGrid = groupPanelGrid;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapSearchPanelGrid
	//////////////////////////////////////////////////////////////
	/**
	 * @return ldapSearchPanelGrid
	 */
	public HtmlPanelGroup getLdapSearchPanelGrid() {
		return ldapSearchPanelGrid;
	}

	/**
	 * @param ldapSearchPanelGrid
	 */
	public void setLdapSearchPanelGrid(final HtmlPanelGroup ldapSearchPanelGrid) {
		this.ldapSearchPanelGrid = ldapSearchPanelGrid;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of phoneNumberListPanelGrid
	//////////////////////////////////////////////////////////////
	/**
	 * @return phoneNumberListPanelGrid
	 */
	public HtmlPanelGroup getPhoneNumberListPanelGrid() {
		return phoneNumberListPanelGrid;
	}  

	/**
	 * @param phoneNumberListPanelGrid
	 */
	public void setPhoneNumberListPanelGrid(final HtmlPanelGroup phoneNumberListPanelGrid) {
		this.phoneNumberListPanelGrid = phoneNumberListPanelGrid;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapRequestPanelGrid
	//////////////////////////////////////////////////////////////
	/**
	 * @return ldapRequestPanelGrid
	 */
	public HtmlPanelGroup getLdapRequestPanelGrid() {
		return ldapRequestPanelGrid;
	}

	/**
	 * @param ldapRequestPanelGrid
	 */
	public void setLdapRequestPanelGrid(final HtmlPanelGroup ldapRequestPanelGrid) {
		this.ldapRequestPanelGrid = ldapRequestPanelGrid;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of recipientType
	//////////////////////////////////////////////////////////////
	/**
	 * @return recipientType
	 */
	public String getRecipientType() {
		return recipientType;
	}

	/**
	 * @param recipientType
	 */
	public void setRecipientType(final String recipientType) {
		this.recipientType = recipientType;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of selectedRecipientGroup
	//////////////////////////////////////////////////////////////
	/**
	 * @param selectedRecipientGroup
	 */
	public void setSelectedRecipientGroup(final String selectedRecipientGroup) {
		this.selectedRecipientGroup = selectedRecipientGroup;
	}

	/**
	 * @return selectedRecipientGroup
	 */
	public String getSelectedRecipientGroup() {
		return selectedRecipientGroup;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUid
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUid
	 */
	public void setLdapUid(final String ldapUid) {
		this.ldapUid = ldapUid;
	}

	/**
	 * @return ldapUid
	 */
	public String getLdapUid() {
		return ldapUid;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of selectedRecipient
	//////////////////////////////////////////////////////////////
	/**
	 * @param selectedRecipient
	 */
	public void setSelectedRecipient(final UiRecipient selectedRecipient) {
		this.selectedRecipient = selectedRecipient;
	}

	/**
	 * @return selectedUser
	 */
	public UiRecipient getSelectedRecipient() {
		return selectedRecipient;
	}

	
	/**
	 * @param recipients
	 */
	public void setRecipients(final List<UiRecipient> recipients) {
		this.recipients = recipients;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of recipientToDelete
	//////////////////////////////////////////////////////////////
	/**
	 * @param recipientToDelete
	 */
	public void setRecipientToDelete(final UiRecipient recipientToDelete) {
		this.recipientToDelete = recipientToDelete;
	}

	/**
	 * @return recipientToDelete
	 */
	public UiRecipient getRecipientToDelete() {
		return recipientToDelete;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of phoneNumberToAdd
	//////////////////////////////////////////////////////////////
	/**
	 * @param phoneNumberToAdd
	 */
	public void setPhoneNumberToAdd(final String phoneNumberToAdd) {
		this.phoneNumberToAdd = phoneNumberToAdd;
	}

	/**
	 * @return phoneNumberToAdd
	 */
	public String getPhoneNumberToAdd() {
		return phoneNumberToAdd;
	}

	/**
	 * @return recipients
	 */
	public List<UiRecipient> getRecipients() {
		return recipients;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of phoneNumberPattern
	//////////////////////////////////////////////////////////////
	/**
	 * @param phoneNumberPattern
	 */
	public void setPhoneNumberPattern(final String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

	/**
	 * @return phoneNumberPattern
	 */
	public String getPhoneNumberPattern() {
		return phoneNumberPattern;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @return ldapUtils
	 */
	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of destTypeOptions
	//////////////////////////////////////////////////////////////
	/**
	 * @param destTypeOptions
	 */
	public void setDestTypeOptions(final List<SelectItem> destTypeOptions) {
		this.destTypeOptions = destTypeOptions;
	}

	/**
	 * @return destTypeOptions
	 */
	public List<SelectItem> getDestTypeOptions() {
		return destTypeOptions;
	}

	/**
	 * @param usersSearchController
	 */
	public void setUserSearchController(final UsersSearchController usersSearchController) {
		this.userSearchController = usersSearchController;
	}
	
	/**
	 * @return recipientList.
	 */
	public List<UiRecipient> getRecipientList() {
		return recipientList;
	}

	/**
	 * @param recipientList
	 */
	public void setRecipientList(final List<UiRecipient> recipientList) {
		this.recipientList = recipientList;
	}
}
