package org.esupportail.smsu.business;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.client.ListPhoneNumbersInBlackListClient;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;

/**
 * Business layer concerning smsu notification invalid mail.
 *
 */
public class NotificationByMailForInvalidPhoneManager {
	
	
	/**
	 * {@link i18nService}.
	 */
	private I18nService i18nService;
	
	/**
	 *  {@link ldapUtils}.
	 */
	private LdapUtils ldapUtils;
	
	/**
	 * {@link smtpServiceUtils}.
	 */
	private SmtpServiceUtils smtpServiceUtils;
	
	/**
	 * {@link listPhoneNumbersInBlackListClient}.
	 */
	private ListPhoneNumbersInBlackListClient listPhoneNumbersInBlackListClient;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	///////////////////////////////////////
	//  constructor
	//////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public NotificationByMailForInvalidPhoneManager() {
		
	}
	
	///////////////////////////////////////
	//  Private method
	//////////////////////////////////////
	/**
	 * Get list of phone numbers in blacklist.
	 * @return return set of phone numbers 
	 */
	private Set<String> getListePhoneNumberInBlackList() {
		if (logger.isDebugEnabled()) {
			logger.debug("Request getListePhoneNumberInBlackList in " 
					+ "NotificationByMailForInvalidPhoneManager");
		}
		
		Set<String> retVal = null;
		try {
		 retVal = listPhoneNumbersInBlackListClient.getListPhoneNumbersInBlackList(); 
		 if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(500);
				sb.append("Response for getListPhoneNumbersInBlackList request " 
						+ "in NotificationByMailForInvalidPhoneManager: ");
				for (String nb : retVal) {
				sb.append(" - phone number in blacklist = ").append(nb);	
				}
				logger.debug(sb.toString());
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Probl�me de connextion Web Service !!!");
			}
		}
		
		return retVal;
	}
	
	/**
	 * retrieve mail based on uid.
	 * @param uid
	 * @return
	 */
	private String getMail(final String uid) {
		if (logger.isDebugEnabled()) {
			logger.debug("Get the mail for user:  " + uid);
		}
		String mail = null;
		try {
			mail = ldapUtils.getUserEmailAdressByUid(uid);
			if (logger.isDebugEnabled()) {
				logger.debug("The mail is:  " + mail);
			}
		} catch (LdapUserNotFoundException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
		
		return mail;
	}

	///////////////////////////////////////
	//  Public method
	//////////////////////////////////////
	/**
	 * sendMails method called by Quartz task.
	 * @return
	 */
	public void sendMails() {
		if (logger.isDebugEnabled()) {
			logger.debug("Enter to sendMails method");
		}
		// 1 - Retrieve the list of phone numbers in blacklist 
		Set<String> listPhones = getListePhoneNumberInBlackList();
		
			for (String phoneNumber : listPhones) {
				if (logger.isDebugEnabled()) {
					logger.debug("search ldapUser for phone number:" + phoneNumber);
				}
			// 2 - Retrieve the ldapUser 	
			List<LdapUser>  list = ldapUtils.searchLdapUsersByPhoneNumber(phoneNumber);
    		if (!list.isEmpty()) {
    		String uid = list.get(0).getId();
    		String mail = getMail(uid);
			    		if (mail != null) {
			    		final List<String> toList = new LinkedList<String>();
			        	toList.add(mail);
			    		// 3 - Send mail
			        	String subject = getI18nService().getString("MSG.SUBJECT.MAIL.TO.INVALIDPHONE", 
			        					 getI18nService().getDefaultLocale());
			        	
			    		String textBody = getI18nService().getString("MSG.TEXTBOX.MAIL.TO.INVALIDPHONE",
			    					  getI18nService().getDefaultLocale(), phoneNumber);
			        	
			        	smtpServiceUtils.sendMessage(toList, null, subject, textBody);
			    		} else {
			    		logger.debug("mail is null");	
			    		}
    		} else {
    		logger.debug("not user found for number phone: " + phoneNumber);
    		}
			}	
	}

	///////////////////////////////////////
	//  setters for spring objects
	//////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}
	
	/**
	 * @param listPhoneNumbersInBlackListClient
	 */
	public void setListPhoneNumbersInBlackListClient(
			final ListPhoneNumbersInBlackListClient listPhoneNumbersInBlackListClient) {
		this.listPhoneNumbersInBlackListClient = listPhoneNumbersInBlackListClient;
	}

	/**
	 * @param smtpServiceUtils
	 */
	public void setSmtpServiceUtils(final SmtpServiceUtils smtpServiceUtils) {
		this.smtpServiceUtils = smtpServiceUtils;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of i18nService
	//////////////////////////////////////////////////////////////
	/**
	 * Set the i18nService.
	 * @param i18nService
	 */
	public void setI18nService(final I18nService i18nService) {
		this.i18nService = i18nService;
	}

	public I18nService getI18nService() {
		return i18nService;
	}

}
