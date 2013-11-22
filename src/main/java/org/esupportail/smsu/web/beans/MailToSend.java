package org.esupportail.smsu.web.beans;

/**
 * @author xphp8691
 *
 */
public class MailToSend {

	
	private Boolean isMailToRecipients;
	
	private String mailOtherRecipients;
	
	private String mailSubject;
	
	private String mailContent;
	
	private String mailTemplate;

	/**
	 * @param isMailToRecipients
	 * @param mailContent
	 * @param mailOtherRecipients
	 * @param mailSubject
	 */
	public MailToSend(final Boolean isMailToRecipients, final  String mailContent,
			final String mailOtherRecipients, final  String mailSubject, final String mailTemplate) {
		super();
		this.isMailToRecipients = isMailToRecipients;
		this.mailContent = mailContent;
		this.mailOtherRecipients = mailOtherRecipients;
		this.mailSubject = mailSubject;
		this.mailTemplate = mailTemplate;
	}

	public Boolean getIsMailToRecipients() {
		return isMailToRecipients;
	}

	public void setIsMailToRecipients(final Boolean isMailToRecipients) {
		this.isMailToRecipients = isMailToRecipients;
	}

	public String getMailOtherRecipients() {
		return mailOtherRecipients;
	}

	public String[] getMailOtherRecipientsList() {
		if (mailOtherRecipients.equals("")) 
			return new String[] {};
		else
			return mailOtherRecipients.split(",");
	}

	public void setMailOtherRecipients(final String mailOtherRecipients) {
		this.mailOtherRecipients = mailOtherRecipients;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(final String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(final String mailContent) {
		this.mailContent = mailContent;
	}
	
	public String getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(final String mailTemplate) {
		this.mailTemplate = mailTemplate;
	}
	
}
