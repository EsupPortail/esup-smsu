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
			String badMail = validateOthersMails(sendSMSController.getMailOtherRecipientsList());
			if (badMail != null) {
				addErrorMessage(null, "SERVICE.FORMATMAIL.WRONG", badMail);
				return null;
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

		if (!contentValidation(content)) return null;

		if (!recipientValidation()) return null;

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

				try {
					message = getDomainService().composeMessage(uiRecipients, login, content,
							smsTemplate, userGroup, serviceId, mail);
				} catch (CreateMessageException e) {
				    addFormattedError(null, e.toI18nString(getI18nService()));
				    return null;
				}

				String errorMsgKey = getDomainService().treatMessage(message);
				if (errorMsgKey != null) {
				    addErrorMessage(null, errorMsgKey);
				    return null;
				}

				sendSMSController.setIsShowMsgsUsingMessageStatus(message.getStateAsEnum());
				resetControllers();
				return "envoiOK";
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
	private Boolean contentValidation(final String content) {
		Integer contentSize = content.length();
		logger.debug("taille de message : " + contentSize.toString());
		logger.debug("message : " + content);
		if (contentSize == 0) {
			addErrorMessage(null, "SENDSMS.MESSAGE.EMPTYMESSAGE");
			return false;
		} else if (contentSize > smsMaxSize) {
			addErrorMessage(null, "SENDSMS.MESSAGE.MESSAGETOOLONG");
			return false;
		} else {
			return true;
		}		
	}

	/**
	 * recipient validation.
	 */
	private Boolean recipientValidation() {
		List<UiRecipient> uiRecipients = smsRecipientController.getRecipients();
		if (uiRecipients.isEmpty()) {
			addErrorMessage(null, "SENDSMS.MESSAGE.RECIPIENTSMANDATORY");
			return false;
		}  else {
			return true;
		}
	}

	/**
	 * recipient validation.
	 */
	private String validateOthersMails(final String[] mails) {
		for (String mail : mails) {
			if (logger.isDebugEnabled()) logger.debug("mail validateOthersMails is :" + mail);

			if (!smtpServiceUtils.checkInternetAdresses(mail)) {
				logger.info("validateOthersMails: " + mail + " is invalid");
				return mail;
			}
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
