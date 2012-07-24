/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.HashSet;
import java.util.Date;
import java.util.Set;

import org.apache.myfaces.component.html.ext.HtmlPanelGroup;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.UnknownIdentifierMessageException;
import org.esupportail.smsu.web.beans.MessagePaginator;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.ws.remote.beans.TrackInfos;

/**
 * A bean to manage user preferences.
 * @param <DomaineService>
 */
public class MessagesController<DomaineService> extends AbstractContextAwareController {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2503649603430502319L;

	/**
	 * const.
	 */
	private static final String CS_NONE = "none";
	
	/**
	 * The message paginator.
	 */
	private MessagePaginator paginator;

	/**
	 * The message beginDate.
	 */
	private Date beginDate;

	/**
	 * The message endDate.
	 */
	private Date endDate;

	/**
	 * The userGroupId.
	 */
	private Integer userGroupId;

	/**
	 * The userAccountId.
	 */
	private Integer userAccountId;

	/**
	 * The userServiceId.
	 */
	private String userServiceId;

	/**
	 * The userTemplateId.
	 */
	private Integer userTemplateId;

	/**
	 * The userUserId.
	 */
	private String userUserId;

	/**
	 * The UI message.
	 */
	private UIMessage message;

	/**
	 * the list search panel grid component.
	 */
	private HtmlPanelGroup listSearchPanelGrid;

	/**
	 * rights list.
	 */
	private Set<FonctionName> rights = new HashSet<FonctionName>();

	private String recipientsText;

	/**
	 * The count of black list recipients backListDestCount.
	 */
	private String backListDestCount;

	/**
	 * The count of snet SMS sentSMSCount.
	 */
	private String sentSMSCount;

	/**
	 * A logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(this.getClass());

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public MessagesController() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Access Management
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
		this.rights.add(FonctionName.FCTN_SUIVI_ENVOIS_ETABL);
		this.rights.add(FonctionName.FCTN_SUIVI_ENVOIS_UTIL);
		return getDomainService().checkRights(currentUser.getFonctions(), this.rights);
	}


	//////////////////////////////////////////////////////////////
	// Enter method (for Initialazation)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 */
	public String enter()  {
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		// initialize data in the page
		init();
		return "TrackingSend";
	}

	//////////////////////////////////////////////////////////////
	// Init methods 
	//////////////////////////////////////////////////////////////
	/**
	 * initialize data in the page.
	 */
	private void init()  {
		listSearchPanelGrid = new HtmlPanelGroup();
		listSearchPanelGrid.setRendered(false);
	}

	//////////////////////////////////////////////////////////////
	// Others
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractContextAwareController#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		paginator = new MessagePaginator(getDomainService());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + hashCode();
	}

	//////////////////////////////////////////////////////////////
	// Validation method
	//////////////////////////////////////////////////////////////
	/**
	 * action.
	 * @return the outcome "ListSend",  if the beginDate < endDate 
	 */
	public String listSend() {
		if (beginDate != null && endDate != null) {
			if (beginDate.getTime() > endDate.getTime()) {
				addErrorMessage(
						"searchSMS:dates",
						"SEND.SEARCH.DATES.ERROR",
						null);

				return null;
			}
		}

		paginator.setCurrentPage(0);
		if (paginator.getVisibleItems() == null) {
			paginator.forceReload();
		}

		listSearchPanelGrid.setRendered(true);
		return "TrackingSend"; 
	}

	/**
	 * For treatments.
	 * @return the paginator
	 */
	public MessagePaginator getPaginator() {
		return paginator;
	}

	public String displayDetails() {
		this.recipientsText = null;
		this.backListDestCount = null;
		this.sentSMSCount = null;

		try {
			if (MessageStatus.SENT.name().equals(message.getStateAsEnum().name())) {
				computeDetailsSentMessage();
			}
			computeMoreDetails();
		} catch (Exception e) {
			addErrorMessage("TT", "WS.ERROR.MESSAGE", null);
		}	

		return "detailSend";
	}

	private void computeDetailsSentMessage() {
		try {
			TrackInfos infos = getDomainService().getTrackInfos(message.getId());
			this.backListDestCount = infos.getNbDestBlackList().toString();
			this.sentSMSCount = infos.getNbSentSMS().toString();
		} catch (UnknownIdentifierApplicationException e) {
			addErrorMessage("TT", "WS.ERROR.APPLICATION.MESSAGE", null);
		} catch (UnknownIdentifierMessageException e) {
			addErrorMessage("TT", "WS.ERROR.MESSAGE.MESSAGE", null);
		}
	}

	private void computeMoreDetails() {
		Message mess = getDomainService().getMessage(message.getId());
		this.recipientsText = computeRecipientsText(mess.getRecipients());
	}

	private String computeRecipientsText(Set<Recipient> recipients) {
		if (recipients == null) return "";
		String t = null;
		for (Recipient r : recipients)
		    t = (t == null ? "" : t + ", ") + r.getLogin() + ":" + r.getPhone();
		return t;
	}

	//////////////////////////////////////////////
	public String getRecipientsText() {
		return this.recipientsText;
	}

	//////////////////////////////////////////////////////
	// Getter of WS parameter backListDestCount
	//////////////////////////////////////////////////////
	/**
	 * @return the backListDestCount
	 */
	public String getBackListDestCount() {
		return this.backListDestCount;
	}

	///////////////////////////////////////////////////////
	//  Getter of WS parameter sentSMSCount
	///////////////////////////////////////////////////////
	/**
	 * @return the sentSMSCount
	 */
	public String getSentSMSCount() {
		return this.sentSMSCount;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of beginDate
	//////////////////////////////////////////////////////////////
	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate 
	 */
	public void setBeginDate(final Date beginDate) {

		this.beginDate = beginDate;
		paginator.setBeginDate(formatDateDao(beginDate));
	}

	private Date formatDateDao(final Date date) {
		return date == null ? null : getDomainService().formatDateDao(date);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of endDate
	//////////////////////////////////////////////////////////////
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
		paginator.setEndDate(formatDateDao(endDate));
	}



	//////////////////////////////////////////////////////////////
	// Getter and Setter of userGroupId
	//////////////////////////////////////////////////////////////
	/**
	 * @return the userGroupId
	 */
	public Integer getUserGroupId() {
		return this.userGroupId;
	}

	/**
	 * @param groupId the userGroupId to set
	 */
	public void setUserGroupId(final Integer groupId) {
		this.userGroupId = groupId;
		paginator.setUserGroupId(userGroupId);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of listSearchPanelGrid
	//////////////////////////////////////////////////////////////
	/**
	 * @param listSearchPanelGrid the listSearchPanelGrid to set
	 */
	public void setListSearchPanelGrid(final HtmlPanelGroup listSearchPanelGrid) {
		this.listSearchPanelGrid = listSearchPanelGrid;
	}

	/**
	 * @return the listSearchPanelGrid
	 */
	public HtmlPanelGroup getListSearchPanelGrid() {
		return listSearchPanelGrid;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userAccountId
	//////////////////////////////////////////////////////////////
	/**
	 * @return the userAccountId
	 */
	public Integer getUserAccountId() {
		return userAccountId;
	}

	/**
	 * @param userAccountId the userAccountId to set
	 */
	public void setUserAccountId(final Integer userAccountId) {
		this.userAccountId = userAccountId;
		paginator.setUserAccountId(userAccountId);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userServiceId
	//////////////////////////////////////////////////////////////
	/**
	 * @return the getUserServiceId
	 */
	public String getUserServiceId() {
		return userServiceId;
	}

	/**
	 * @param userServiceId the userServiceId to set
	 */
	public void setUserServiceId(final String userServiceId) {
		if (!userServiceId.equals(CS_NONE)) {
			this.userServiceId = userServiceId;
		} else {
			this.userServiceId = null;
		}
		Integer intuserServiceId = null;
		if (this.userServiceId != null) {
			intuserServiceId = Integer.valueOf(this.userServiceId);
		}
		paginator.setUserServiceId(intuserServiceId);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userTemplateId
	//////////////////////////////////////////////////////////////
	/**
	 * @return the userTemplateId
	 */
	public Integer getUserTemplateId() {
		return userTemplateId;
	}

	/**
	 * @param userTemplateId the userTemplateId to set
	 */
	public void setUserTemplateId(final Integer userTemplateId) {
		this.userTemplateId = userTemplateId;
		paginator.setUserTemplateId(userTemplateId);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of userUserId
	//////////////////////////////////////////////////////////////
	/**
	 * @return the userUserId
	 */
	public String getUserUserId() {
		return userUserId;
	}

	/**
	 * @param userUserId the userUserId to set
	 */
	public void setUserUserId(final String userUserId) {
		this.userUserId = userUserId;
		paginator.setUserUserId(userUserId);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of message
	//////////////////////////////////////////////////////////////
	/**
	 * @return the message
	 */
	public UIMessage getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final UIMessage message) {
		this.message = message;
	}







}

