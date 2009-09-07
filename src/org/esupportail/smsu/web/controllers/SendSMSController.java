package org.esupportail.smsu.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UISelectBoolean;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.component.html.ext.HtmlPanelGroup;
import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.dao.beans.Template;
import org.esupportail.smsu.domain.DomainService;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;


/**
 * @author xphp8691 
 *
 */
public class SendSMSController extends AbstractContextAwareController {

	/**
	 * serial version UID.
	 */
	private static final long serialVersionUID = 7353870024238458511L;

	/**
	 * const.
	 */
	private static final String CS_NONE = "none";
	
	
	/**
	 * the selected service.
	 */
	private String selectedService;

	/**
	 * the selected user group.
	 */
	private String selectedUserGroup;

	/**
	 * the selected SMS model.
	 */
	private String selectedSmsModel;

	/**
	 * the SMS prefix.
	 */
	private String smsPrefix;

	/**
	 * the SMS body.
	 */	
	private HtmlInputTextarea smsBody;

	/**
	 * the SMS suffix.
	 */
	private String smsSuffix;

	/**
	 * the selected Mail model.
	 */
	private String selectedMailModel;

	/**
	 * the Mail prefix.
	 */
	private String mailPrefix;

	/**
	 * the Mail body.
	 */	
	private HtmlInputTextarea mailBody;

	/**
	 * the Mail suffix.
	 */
	private String mailSuffix;
	
	/**
	 * the Mail recipients.
	 */
	private String mailOtherRecipients;

	/**
	 * show the recipients check box & label.
	 */
	private Boolean isShow = true;

	/**
	 * show the msg send to back office.
	 */
	private Boolean isShowMsgSending = false;

	/**
	 * show the msg waiting for approval.
	 */
	private Boolean isShowMsgWainting = false;

	/**
	 * show the Send Mail check box & label.
	 */
	private Boolean isCheckBoxSendMailShow = false;
	
	/**
	 * show the msg no recipient found.
	 */
	private Boolean isShowMsgNoRecipientFound = false;

	/**
	 * the Mail subject.
	 */
	private String mailSubject;
	/**
	 * LDAP utils.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * rights list.
	 */
	private List<String> rights = new ArrayList<String>();
	
	/**
	 * the select item for sms templates.
	 */
	private List<SelectItem> smsTemplateOptions;

	/**
	 * the select item for userGroupsOptions.
	 */
	private List<SelectItem> userGroupsOptions;
	
	/**
	 * the select item for serviceOptions.
	 */
	private List<SelectItem> serviceOptions;

	/**
	 * the mailPanelGrid component.
	 */
	private HtmlPanelGroup mailPanelGrid;

	/**
	 * the checkbox component.
	 */
	private UISelectBoolean checkbox;
	
	/**
	 * the checkboxRecipients component.
	 */
	private UISelectBoolean checkboxRecipients;

	/**
	 * {@link SmsRecipientController}.
	 */
	private SmsRecipientController smsRecipientController;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Constructor.
	 */
	public SendSMSController() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Acces control method 
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
		// rights to enter
		this.rights.add(FonctionName.FCTN_SMS_ENVOI_ADH.toString());
		this.rights.add(FonctionName.FCTN_SMS_ENVOI_GROUPES.toString());
		this.rights.add(FonctionName.FCTN_SMS_ENVOI_NUM_TEL.toString());
		this.rights.add(FonctionName.FCTN_SMS_REQ_LDAP_ADH.toString());
		
		return getDomainService().checkRights(currentUser.getFonctions(), this.rights);
	}
	
	//////////////////////////////////////////////////////////////
	// Enter method (for Initialazation)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 */
	public String enter() {
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		
		if (getCurrentUser().getFonctions().contains(FonctionName.FCTN_SMS_AJOUT_MAIL.name())) {
			this.isCheckBoxSendMailShow = true;
		} 
		init();
		return "navigationSendSMS";
	}

	//////////////////////////////////////////////////////////////
	// Init method
	//////////////////////////////////////////////////////////////
	/**
	 * init function.
	 */
	private void init() {
		initSmsTemplateOptions();
		initUserGroupsOptions();
		initServiceOptions();
		smsRecipientController.init();
		mailPanelGrid = new HtmlPanelGroup();
		mailBody = new HtmlInputTextarea();
		mailPanelGrid.setRendered(false);
		checkbox = new UISelectBoolean();
		
		String defaultService = "";
		if (serviceOptions.size() > 0) {
			defaultService = serviceOptions.get(0).getValue().toString();
		}
		this.setSelectedService(defaultService);
		this.setSelectedSmsModel(null);
		this.setSelectedUserGroup(null);
		this.setSmsPrefix("");
		this.setSmsSuffix("");
		smsBody = new HtmlInputTextarea();
		this.getSmsBody().setValue(null);
		selectedSmsModel = CS_NONE;
		selectedMailModel = CS_NONE;
		smsRecipientController.setRecipientType(null);
		smsRecipientController.getRecipients().clear();
		smsRecipientController.setPhoneNumberToAdd(null);
		
	}
	
	/**
	 * @return the navigation result of the send action.
	 */
	public String send() {
		return null;
	}

	//////////////////////////////////////////////////////////////
	// Principal methods 
	//////////////////////////////////////////////////////////////
	/**
	 * @param ValueChangeEvent
	 */
	public void modifSmsModel(final ValueChangeEvent e) {
		this.setSelectedSmsModel((String) e.getNewValue());

		if (!selectedSmsModel.equals(CS_NONE)) {
			DomainService domainService = getDomainService();
			Integer id;
			try {
				id = Integer.parseInt(this.selectedSmsModel);
				Template tpl = domainService.getTemplateById(id);
				smsPrefix = tpl.getHeading();
				smsBody.setValue(tpl.getBody());
				smsSuffix = tpl.getSignature();
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}

		} else {
			smsPrefix = "";
			smsBody.setValue("");
			smsSuffix = "";
		}
	}
	
	/**
	 * @param ValueChangeEvent
	 */
	public void modifMailModel(final ValueChangeEvent e) {
		this.setSelectedMailModel((String) e.getNewValue());

		if (!selectedMailModel.equals(CS_NONE)) {
			DomainService domainService = getDomainService();
			Integer id;
			try {
				id = Integer.parseInt(this.selectedMailModel);
				Template tpl = domainService.getTemplateById(id);
				mailPrefix = tpl.getHeading();
				mailBody.setValue(tpl.getBody());
				mailSuffix = tpl.getSignature();
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}

		} else {
			mailPrefix = "";
			mailBody.setValue("");
			mailSuffix = "";
		}
	}
	
	
	/**
	 * showMailPanel function.
	 */
	public void showMailPanel() {
		if (this.checkbox.isSelected()) {
		mailPanelGrid.setRendered(true);
		this.mailSubject = getI18nService().getString("MSG.SUBJECT.MAIL", getI18nService().getDefaultLocale());	
		this.selectedMailModel = this.selectedSmsModel;
		this.setMailPrefix(smsPrefix);
		logger.debug("value of smsBody is: " + smsBody.getValue());
		this.mailBody.setValue(smsBody.getValue());
		logger.debug("value of mailBody is : " + mailBody.getValue());
		this.setMailSuffix(smsSuffix);
		// canceled function, because ergonomy problem, than isShow is always true
		//this.isShow = false;
//		List<UiRecipient> uiRecipients = smsRecipientController.getRecipients();
//		if (uiRecipients != null ) {
//			for (UiRecipient recipient : uiRecipients) {
//				if (recipient instanceof SingleUserRecipient) {
//					this.isShow = true;
//				} else if (recipient instanceof GroupRecipient) {
//					this.isShow = true;
//					}
//				}
//			}
		} else {
		this.mailSubject = null;
		this.selectedMailModel = null;
		this.setMailPrefix(null);
		this.setMailSuffix(null);
		mailPanelGrid.setRendered(false);	
		}
	}
	
	//////////////////////////////////////////////////////////////
	// Private tools methods 
	//////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	private void initSmsTemplateOptions() {
		Integer iId;
		String sId;
		String label;
		this.smsTemplateOptions = new ArrayList<SelectItem>();
		List<Template> listTemplates;
		I18nService tradService = getI18nService();
		String noneLabel = tradService.getString("SENDSMS.LABEL.NONE");
		SelectItem option = new SelectItem(CS_NONE, noneLabel);

		// no template option.
		this.smsTemplateOptions.add(option);

		listTemplates = getDomainService().getTemplates();

		for (Template tpl : listTemplates) {
			iId = tpl.getId();
			sId = iId.toString();
			label = tpl.getLabel();
			option = new SelectItem(sId, label);
			this.smsTemplateOptions.add(option);
		}
	}
	
	/**
	 * userGroupsOptions initialization.
	 */
	private void initUserGroupsOptions() {
		userGroupsOptions = new ArrayList<SelectItem>();
		SelectItem option;

		try {
			User user = getCurrentUser();
			String uid = user.getId();
			List<UserGroup> listUserGroups = ldapUtils.getUserGroupsByUid(uid);

			option = new SelectItem(uid, user.getDisplayName());
			userGroupsOptions.add(option);
			
			for (UserGroup group : listUserGroups) {
				option = new SelectItem(group.getLdapId(), group.getLdapName());
				userGroupsOptions.add(option);
			}
		} catch (Exception e) {
			addErrorMessage(null, "SENDSMS.MESSAGE.INITUSERGROUPSERROR");
		}
	}

	/**
	 * service options init.
	 */
	private void initServiceOptions() {
		List<Service> listServices;
		serviceOptions = new ArrayList<SelectItem>();
		SelectItem option;
		Integer iSvcId;
		String sSvcId;
		String svcLabel;
		I18nService tradService = getI18nService();
		String noneLabel = tradService.getString("SENDSMS.LABEL.NONE");
		option = new SelectItem(CS_NONE, noneLabel);
		serviceOptions.add(option);
		
		listServices = getDomainService().getServices();

		for (Service cService : listServices) {
			iSvcId = cService.getId();
			sSvcId = iSvcId.toString();
			svcLabel = cService.getName();

			option = new SelectItem(sSvcId, svcLabel);
			serviceOptions.add(option);
		}
	}

	//////////////////////////////////////////////////////////////
	// Others 
	//////////////////////////////////////////////////////////////
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of mailPanelGrid
	//////////////////////////////////////////////////////////////
	
	public HtmlPanelGroup getMailPanelGrid() {
		return mailPanelGrid;
	}

	public void setMailPanelGrid(final HtmlPanelGroup mailPanelGrid) {
		this.mailPanelGrid = mailPanelGrid;
	}

	/**
	 * @param checkbox the checkbox to set
	 */
	public void setCheckbox(final UISelectBoolean checkbox) {
		this.checkbox = checkbox;
	}

	/**
	 * @return the checkbox
	 */
	public UISelectBoolean getCheckbox() {
		return checkbox;
	}

	/**
	 * @param mailPrefix the mailPrefix to set
	 */
	public void setMailPrefix(final String mailPrefix) {
		this.mailPrefix = mailPrefix;
	}

	/**
	 * @return the mailPrefix
	 */
	public String getMailPrefix() {
		return mailPrefix;
	}

	/**
	 * @param mailBody the mailBody to set
	 */
	public void setMailBody(final HtmlInputTextarea mailBody) {
		this.mailBody = mailBody;
	}

	/**
	 * @return the mailBody
	 */
	public HtmlInputTextarea getMailBody() {
		return mailBody;
	}

	/**
	 * @param mailSuffix the mailSuffix to set
	 */
	public void setMailSuffix(final String mailSuffix) {
		this.mailSuffix = mailSuffix;
	}

	/**
	 * @return the mailSuffix
	 */
	public String getMailSuffix() {
		return mailSuffix;
	}

	/**
	 * @param selectedMailModel the selectedMailModel to set
	 */
	public void setSelectedMailModel(final String selectedMailModel) {
		this.selectedMailModel = selectedMailModel;
	}

	/**
	 * @return the selectedMailModel
	 */
	public String getSelectedMailModel() {
		return selectedMailModel;
	}

	/**
	 * @param mailOtherRecipients the mailOtherRecipients to set
	 */
	public void setMailOtherRecipients(final String mailOtherRecipients) {
		this.mailOtherRecipients = mailOtherRecipients;
	}

	/**
	 * @return the mailOtherRecipients
	 */
	public String getMailOtherRecipients() {
		return mailOtherRecipients;
	}

	/**
	 * @param mailSubject the mailSubject to set
	 */
	public void setMailSubject(final String mailSubject) {
		this.mailSubject = mailSubject;
	}

	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mailSubject;
	}

	/**
	 * @param checkboxRecipients the checkboxRecipients to set
	 */
	public void setCheckboxRecipients(final UISelectBoolean checkboxRecipients) {
		this.checkboxRecipients = checkboxRecipients;
	}

	/**
	 * @return the checkboxRecipients
	 */
	public UISelectBoolean getCheckboxRecipients() {
		return checkboxRecipients;
	}

	public SmsRecipientController getSmsRecipientController() {
		return smsRecipientController;
	}

	public void setSmsRecipientController(
			final SmsRecipientController smsRecipientController) {
		this.smsRecipientController = smsRecipientController;
	}

	/**
	 * @param isShow the isShow to set
	 */
	public void setIsShow(final Boolean isShow) {
		this.isShow = isShow;
	}

	/**
	 * @return the isShow
	 */
	public Boolean getIsShow() {
		return isShow;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of selectedService
	//////////////////////////////////////////////////////////////
 	/**
	 * @param selectedService
	 */
	public void setSelectedService(final String selectedService) {
		this.selectedService = selectedService;
	}

	/**
	 * @return the selected service.
	 */
	public String getSelectedService() {
		return selectedService;
	}

	///////////////////////////////////////////////////////
	//  Setter of parameter selectedUserGroup
	///////////////////////////////////////////////////////
	/**
	 * @param selectedUserGroup
	 */
	public void setSelectedUserGroup(final String selectedUserGroup) {
		this.selectedUserGroup = selectedUserGroup;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @return the LDAP utils
	 */
	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}

	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @return the selected user group.
	 */
	public String getSelectedUserGroup() {
		return selectedUserGroup;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of selectedSmsModel
	//////////////////////////////////////////////////////////////
	/**
	 * @param selectedSmsModel
	 */
	public void setSelectedSmsModel(final String selectedSmsModel) {
		this.selectedSmsModel = selectedSmsModel;
	}

	/**
	 * @return selectedSmsModel
	 */
	public String getSelectedSmsModel() {
		return selectedSmsModel;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smsPrefix
	//////////////////////////////////////////////////////////////
	/**
	 * @param smsPrefix
	 */
	public void setSmsPrefix(final String smsPrefix) {
		this.smsPrefix = smsPrefix;
	}

	/**
	 * @return smsPrefix
	 */
	public String getSmsPrefix() {
		return smsPrefix;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smsSuffix
	//////////////////////////////////////////////////////////////
	/**
	 * @param smsSuffix
	 */
	public void setSmsSuffix(final String smsSuffix) {
		this.smsSuffix = smsSuffix;
	}

	/**
	 * @return smsSuffix
	 */
	public String getSmsSuffix() {
		return smsSuffix;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smsBody
	//////////////////////////////////////////////////////////////
	/**
	 * @return smsBody.
	 */
	public HtmlInputTextarea getSmsBody() {
		return smsBody;
	}

	/**
	 * @param smsBody
	 */
	public void setSmsBody(final HtmlInputTextarea smsBody) {
		this.smsBody = smsBody;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smsTemplateOptions
	//////////////////////////////////////////////////////////////
	/**
	 * @param smsTemplateOptions
	 */
	public void setSmsTemplateOptions(final List<SelectItem> smsTemplateOptions) {
		this.smsTemplateOptions = smsTemplateOptions;
	}

	/**
	 * @return the template options.
	 */
	public List<SelectItem> getSmsTemplateOptions() {
		return smsTemplateOptions;
	}
	
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of userGroupsOptions
	//////////////////////////////////////////////////////////////
	/**
	 * @param userGroupsOptions
	 */
	public void setUserGroupsOptions(final List<SelectItem> userGroupsOptions) {
		this.userGroupsOptions = userGroupsOptions;
	}
	
	/**
	 * @return userGroupsOptions
	 */
	public List<SelectItem> getUserGroupsOptions() {
		return userGroupsOptions;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of serviceOptions
	//////////////////////////////////////////////////////////////
	/**
	 * @param serviceOptions
	 */
	public void setServiceOptions(final List<SelectItem> serviceOptions) {
		this.serviceOptions = serviceOptions;
	}

	/**
	 * @return serviceOptions
	 */
	public List<SelectItem> getServiceOptions() {
		return serviceOptions;
	}

	/**
	 * @param isCheckBoxSendMailShow the isCheckBoxSendMailShow to set
	 */
	public void setIsCheckBoxSendMailShow(final Boolean isCheckBoxSendMailShow) {
		this.isCheckBoxSendMailShow = isCheckBoxSendMailShow;
	}

	/**
	 * @return the isCheckBoxSendMailShow
	 */
	public Boolean getIsCheckBoxSendMailShow() {
		return isCheckBoxSendMailShow;
	}

	/**
	 * @param isShowMsgSending the isShowMsgSending to set
	 */
	public void setIsShowMsgSending(final Boolean isShowMsgSending) {
		this.isShowMsgSending = isShowMsgSending;
	}

	/**
	 * @return the isShowMsgSending
	 */
	public Boolean getIsShowMsgSending() {
		return isShowMsgSending;
	}

	/**
	 * @param isShowMsgWainting the isShowMsgWainting to set
	 */
	public void setIsShowMsgWainting(final Boolean isShowMsgWainting) {
		this.isShowMsgWainting = isShowMsgWainting;
	}

	/**
	 * @return the isShowMsgWainting
	 */
	public Boolean getIsShowMsgWainting() {
		return isShowMsgWainting;
	}

	public void setIsShowMsgNoRecipientFound(final Boolean isShowMsgNoRecipientFound) {
		this.isShowMsgNoRecipientFound = isShowMsgNoRecipientFound;
	}

	public Boolean getIsShowMsgNoRecipientFound() {
		return isShowMsgNoRecipientFound;
	}

}
