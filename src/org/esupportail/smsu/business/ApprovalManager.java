package org.esupportail.smsu.business;


import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIMessage;



/**
 * Business layer concerning smsu service.
 *
 */
public class ApprovalManager {

	/**
	 * const.
	 */
	private static final String NONE = "Aucun";
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * ldap service.
	 */
	private LdapUtils ldapUtils;

	/**
	 * {@link DaoService}.
	 */
	private SendSmsManager sendSmsManager;

	/**
	 * displayName.
	 */
	private String displayNameAttributeAsString;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public ApprovalManager() {
		super();
	}



	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * @param idUser
	 * @return the UI messages.
	 */
	public List<UIMessage> getApprovalMessages(final String idUser) {
		List<UIMessage> uimessages = new ArrayList<UIMessage>();
		List<Message> messages = daoService.getApprovalMessages();

		List<String> displayNameList = new ArrayList<String>();
		List<LdapUser> ldapUserList = new ArrayList<LdapUser>();

		if (messages != null) {
			Person user = daoService.getPersonByLogin(idUser);
			for (Message mess : messages) {
				if (!displayNameList.contains(mess.getUserUserLabel())) {
					displayNameList.add(mess.getUserUserLabel());
				}
			}

			ldapUserList = ldapUtils.getUsersByUids(displayNameList);


			for (Message mess : messages) {

				// Retrieve supervisors
				if (mess.getSupervisors().contains(user)) { 

					String displayName = NONE;
					String groupName;

					// 1 - Retrieve displayName
					Boolean testVal = true;
					LdapUser ldapUser;
					int i = 0;

					while ((i < ldapUserList.size()) && testVal) {
						ldapUser = ldapUserList.get(i);
						logger.debug("ldapUser.getId is: " + ldapUser.getId());
						logger.debug("mess.getUserUserLabel is: " + mess.getUserUserLabel());
						if (ldapUser.getId().equals(mess.getUserUserLabel())) {
							displayName = ldapUser.getAttribute(displayNameAttributeAsString);
							logger.debug("displayName is: " + displayName);
							testVal = false;
						}
						i++;
					}


					if (displayName.equals(NONE)) {
						displayName = mess.getUserUserLabel();
					} else {
						displayName = displayName + "  (" + mess.getUserUserLabel() + ")"; 
					}

					String groupLabel = mess.getGroupRecipient().getLabel();
					//getUserGroupLabel();
					 
					try {
						groupName = ldapUtils.getUserDisplayNameByUserUid(groupLabel);
					} catch (LdapUserNotFoundException e) {
						
						groupName = ldapUtils.getGroupNameByUid(groupLabel);
						if (groupName == null) {
							groupName = groupLabel;
						}	
					}
					
					// add UI message to list
					uimessages.add(new UIMessage(displayName, groupName, mess));
				}    
			}
		}
		return uimessages;
	}

	/**
	 * Update the State of the message.
	 * @param uiMessage
	 */
	public void updateUIMessage(final UIMessage uiMessage) {
		Message message = daoService.getMessageById(uiMessage.getId());
		message.setStateAsEnum(MessageStatus.CANCEL);
		daoService.updateMessage(message);

	}

	/**
	 * Treat the UI message.
	 * @param uiMessage
	 */
	public void treatUIMessage(final UIMessage uiMessage)	 {
		Message message = daoService.getMessageById(uiMessage.getId());
		sendSmsManager.treatApprovalMessage(message);

	}

	///////////////////////////////////////
	//  setter for spring object daoService
	//////////////////////////////////////	
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	//////////////////////////////////////////////////////////////
	// Setter of spring object ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	//////////////////////////////////////////////////////////////
	// Setter of spring object sendSmsManager
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setSendSmsManager(final SendSmsManager sendSmsManager) {
		this.sendSmsManager = sendSmsManager;
	}



	/**
	 * @param displayNameAttributeAsString the displayNameAttributeAsString to set
	 */
	public void setDisplayNameAttributeAsString(
			final String displayNameAttributeAsString) {
		this.displayNameAttributeAsString = displayNameAttributeAsString;
	}



	/**
	 * @return the displayNameAttributeAsString
	 */
	public String getDisplayNameAttributeAsString() {
		return displayNameAttributeAsString;
	}


}
