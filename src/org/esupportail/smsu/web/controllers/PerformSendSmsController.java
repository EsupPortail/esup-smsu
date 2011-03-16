package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.BackOfficeUnrichableException;
import org.esupportail.smsu.exceptions.InsufficientQuotaException;
import org.esupportail.smsu.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UiRecipient;

/**
 * @author xphp8691
 *
 */
public class PerformSendSmsController extends AbstractContextAwareController {

	/**
	 * the serial version UID.
	 */
	private static final long serialVersionUID = -1923133582589386149L;

	/**
	 * const.
	 */
	private static final String CS_NONE = "none";

	/**
	 * the recipient controller.
	 */
	private SmsRecipientController smsRecipientController;

	/**
	 * the send SMS controller.
	 */
	private SendSMSController sendSMSController;

	/**
	 * The message.
	 */
	private Message message;

	/**
	 * the SMS max size.
	 */
	private Integer smsMaxSize;

	/**
	 * the return string, used for navigation.
	 */
	private String strReturn;

	/**
	 * the SmtpServiceUtils Bean.
	 */
	private SmtpServiceUtils smtpServiceUtils;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public PerformSendSmsController() {
		super();
		message = new Message();
	}

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 * @return perform the sending action.
	 */
	public String sendSMSAction() {

		// validate others mails
		if (sendSMSController.getCheckbox().isSelected()) {
			if (!sendSMSController.getMailOtherRecipients().equals("")) {
				String retVal = validateOthersMails(sendSMSController.getMailOtherRecipients());
				if (retVal != null) {
					addErrorMessage(null, "SERVICE.FORMATMAIL.WRONG", retVal);
					return null;
				}
			}  
		}
		// by default, a SMS is considered as a sent one.
		message.setStateAsEnum(MessageStatus.IN_PROGRESS);

		////////////////get the group and set the account///////////////////
		// validateSelectedUserGroup();

		////////////////Content mail///////////////////
		String contentMail = sendSMSController.getMailPrefix() 
		+ " " + sendSMSController.getMailBody().getValue() 
		+ " " + sendSMSController.getMailSuffix();
		contentMail = contentMail.trim();


		////////////////Content validation///////////////////
		String content = sendSMSController.getSmsPrefix() 
		+ " " + sendSMSController.getSmsBody().getValue() 
		+ " " + sendSMSController.getSmsSuffix();
		content = content.trim();
		strReturn = contentValidation(content);

		if (strReturn != null) {

			////////////////Recipients validation///////////////////
			strReturn = recipientValidation();

			if (strReturn != null) {
				// récupération du user
				String login;
				User currentUser = getCurrentUser();
				if (currentUser != null) {
					login = currentUser.getId();
				} else {
					addErrorMessage(null, "SERVICE.CLIENT.NOTDEFINED");
					return null;
				}

				String smsTemplate = sendSMSController.getSelectedSmsModel();
				if (smsTemplate.equals(CS_NONE)) {
					smsTemplate = null;
				}
				String userGroup = sendSMSController.getSelectedUserGroup();

				Integer serviceId;
				if (!CS_NONE.equals(sendSMSController.getSelectedService())) {
					serviceId = Integer.parseInt(sendSMSController.getSelectedService());
				} else {
					serviceId = null;
				}

				List<UiRecipient> uiRecipients = smsRecipientController.getRecipients();

				MailToSend mail = null;
				
				if (sendSMSController.getCheckbox().isSelected()) {

					Boolean isMailToRecipient = sendSMSController.getCheckboxRecipients().isSelected();
					String otherRecipients = sendSMSController.getMailOtherRecipients();
					String mailSubject = sendSMSController.getMailSubject();
					String mailTemplate = sendSMSController.getSelectedMailModel();
					if (CS_NONE.equals(mailTemplate)) {
						mailTemplate = null;
					}
					mail = new MailToSend(isMailToRecipient, contentMail, otherRecipients, mailSubject, mailTemplate);
				}

				message = getDomainService().composeMessage(uiRecipients, login, content,
						smsTemplate, userGroup, serviceId, mail);

				try {

					String sendResult = getDomainService().treatMessage(message);
					if (sendResult.equals("FOQUOTAKO")) {
						addErrorMessage(null, "SENDSMS.MESSAGE.SENDERGROUPQUOTAERROR");
						strReturn = null;
					} else {
						if (sendResult.equals("FONBMAXFORCUSTOMIZEDGROUPERROR")) {
							addErrorMessage(null, "SENDSMS.MESSAGE.SENDERGROUPNDMAXSMSERROR");
							strReturn = null;
						} else {
							if (sendResult.equals("BOQUOTAKO")) {
								addErrorMessage(null, "SERVICE.CLIENT.QUOTAERROR");
								strReturn = null;
							} else {
								if ((message.getStateAsEnum() == MessageStatus.WAITING_FOR_SENDING)
										|| (message.getStateAsEnum() == MessageStatus.SENT)) {
									sendSMSController.setIsShowMsgSending(true);
									sendSMSController.setIsShowMsgWainting(false);
									sendSMSController.setIsShowMsgNoRecipientFound(false);
								} else {
									if (message.getStateAsEnum() == MessageStatus.WAITING_FOR_APPROVAL) {
										sendSMSController.setIsShowMsgSending(false);
										sendSMSController.setIsShowMsgWainting(true);
										sendSMSController.setIsShowMsgNoRecipientFound(false);
									} else {
										if (message.getStateAsEnum() == MessageStatus.NO_RECIPIENT_FOUND) {
											sendSMSController.setIsShowMsgSending(false);
											sendSMSController.setIsShowMsgWainting(false);
											sendSMSController.setIsShowMsgNoRecipientFound(true);
										}	
									}
								}
								strReturn = "envoiOK";
							}
						}
					}
				} catch (UnknownIdentifierApplicationException e) {
					logger.error("Application unknown", e);
					addErrorMessage(null, "WS.ERROR.APPLICATION");
					strReturn = null;
				} catch (InsufficientQuotaException e) {
					logger.error("Quota error", e);
					addErrorMessage(null, "WS.ERROR.QUOTA");
					strReturn = null;
				} catch (BackOfficeUnrichableException e) {
					logger.error("Unable connect to the back office", e);
					addErrorMessage(null, "WS.ERROR.MESSAGE");
					strReturn = null;
				} catch (LdapUserNotFoundException e1) {
					addErrorMessage(null, "SENDSMS.MESSAGE.LDAPUSERNOTFOUND");
					strReturn = null;
				}
			}
		}

		if (strReturn != null) {
			resetControllers();
		}

		return strReturn;
	}

	/**
	 * @param context
	 * @param componentToValidate
	 * @param value
	 * @throws ValidatorException
	 */
	public void validateService(final FacesContext context,
			final UIComponent componentToValidate,
			final Object value) throws ValidatorException {

		//		String strValue = (String) value;
		//
		//		if (strValue.trim().equals("")) {
		//			throw new ValidatorException(getFacesErrorMessage("SENDSMS.MESSAGE.SERVICEMANDATORY"));
		//		} else {
		//			Integer iService = Integer.parseInt(strValue);
		//			Service service = getDomainService().getServiceById(iService); 
		//			message.setService(service);
		//		}
	}

	/**
	 * Content validation.
	 */
	private String contentValidation(final String content) {
		Integer contentSize = content.length();
		String ret;
		logger.debug("taille de message : " + contentSize.toString());
		logger.debug("message : " + content);
		if (contentSize == 0) {
			addErrorMessage(null, "SENDSMS.MESSAGE.EMPTYMESSAGE");
			ret = null;	
		} else {
			if (contentSize > smsMaxSize) {
				addErrorMessage(null, "SENDSMS.MESSAGE.MESSAGETOOLONG");
				ret = null;	
			} else {
				ret = "contentValidated";
			}
		}		
		
		return ret;
	}

	/**
	 * recipient validation.
	 */
	private String recipientValidation() {
		List<UiRecipient> uiRecipients = smsRecipientController.getRecipients();
		String ret;
		if (uiRecipients.isEmpty()) {
			addErrorMessage(null, "SENDSMS.MESSAGE.RECIPIENTSMANDATORY");
			ret = null;
		}  else {
			ret = "contentValidated"; 
		}
		return ret; 
	}

	/**
	 * recipient validation.
	 */
	private String validateOthersMails(final String mails) {
		String[] others = mails.split(",");
		for (String mail : others) {
			if (logger.isDebugEnabled()) {
				logger.debug("mail validateOthersMails is :" + mail);
			}
			final Boolean retVal = smtpServiceUtils.checkInternetAdresses(mail);
			if (logger.isDebugEnabled()) {
				logger.debug("retVal validateOthersMails is :" + retVal);
			}
			if (!retVal) { return mail; }
		}
		return null;
	}

	/**
	 * reset the user interface.
	 */
	private void resetControllers() {
		String defaultService = "";
		if (sendSMSController.getServiceOptions().size() > 0) {
			defaultService = sendSMSController.getServiceOptions().get(0).getValue().toString();
		}
		sendSMSController.setSelectedService(defaultService);
		sendSMSController.setSelectedSmsModel(CS_NONE);
		sendSMSController.setSelectedUserGroup(null);
		sendSMSController.setSmsPrefix("");
		sendSMSController.setSmsSuffix("");
		sendSMSController.getSmsBody().setValue(null);
		sendSMSController.setMailPrefix(null);
		sendSMSController.setMailSuffix(null);
		sendSMSController.getMailBody().setValue(null);
		sendSMSController.setSelectedMailModel(CS_NONE);
		sendSMSController.setMailSubject(
				getI18nService().getString("MSG.SUBJECT.MAIL", getI18nService().getDefaultLocale()));
		sendSMSController.setMailOtherRecipients("");
		sendSMSController.getCheckbox().setSelected(false);

		smsRecipientController.setRecipientType(null);
		smsRecipientController.getRecipients().clear();
		smsRecipientController.setPhoneNumberToAdd(null);

	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of sendSMSController
	//////////////////////////////////////////////////////////////
	/**
	 * @param sendSMSController
	 */
	public void setSendSMSController(final SendSMSController sendSMSController) {
		this.sendSMSController = sendSMSController;
	}

	/**
	 * @return sendSMSController
	 */
	public SendSMSController getSendSMSController() {
		return sendSMSController;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smsRecipientController
	//////////////////////////////////////////////////////////////
	/**
	 * @param smsRecipientController
	 */
	public void setSmsRecipientController(final SmsRecipientController smsRecipientController) {
		this.smsRecipientController = smsRecipientController;
	}

	/**
	 * @return smsRecipientController.
	 */
	public SmsRecipientController getSmsRecipientController() {
		return smsRecipientController;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smsMaxSize
	//////////////////////////////////////////////////////////////
	/**
	 * @return the SMS max size.
	 */
	public Integer getSmsMaxSize() {
		return smsMaxSize;
	}

	/**
	 * @param smsMaxSize
	 */
	public void setSmsMaxSize(final Integer smsMaxSize) {
		this.smsMaxSize = smsMaxSize;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of message
	//////////////////////////////////////////////////////////////
	/**
	 * @param message
	 */
	public void setMessage(final Message message) {
		this.message = message;
	}

	/**
	 * @return the message.
	 */
	public Message getMessage() {
		return message;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of strReturn
	//////////////////////////////////////////////////////////////
	/**
	 * @return strReturn
	 */
	public String getStrReturn() {
		return strReturn;
	}

	/**
	 * @param strReturn
	 */
	public void setStrReturn(final String strReturn) {
		this.strReturn = strReturn;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of smtpServiceUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param smtpServiceUtils the smtpServiceUtils to set
	 */
	public void setSmtpServiceUtils(final SmtpServiceUtils smtpServiceUtils) {
		this.smtpServiceUtils = smtpServiceUtils;
	}

	/**
	 * @return the smtpServiceUtils
	 */
	public SmtpServiceUtils getSmtpServiceUtils() {
		return smtpServiceUtils;
	}

}
