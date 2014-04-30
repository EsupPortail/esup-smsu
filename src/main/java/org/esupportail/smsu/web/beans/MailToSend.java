package org.esupportail.smsu.web.beans;

import java.util.List;


public class MailToSend {

	private Boolean isMailToRecipients;
	private List<String> mailOtherRecipients;
	
	private String mailSubject;
	
	private String mailContent;
	private String mailTemplate;

	public MailToSend() {}

	public Boolean getIsMailToRecipients() {
		return isMailToRecipients;
	}

	public void setIsMailToRecipients(final Boolean isMailToRecipients) {
		this.isMailToRecipients = isMailToRecipients;
	}

	public List<String> getMailOtherRecipients() {
		return mailOtherRecipients;
	}

	public void setMailOtherRecipients(final List<String> mailOtherRecipients) {
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
