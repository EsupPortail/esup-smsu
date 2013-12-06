package org.esupportail.smsu.web.beans;

import java.util.List;

public class UINewMessage {
	public String recipientGroup;
	public List<String> recipientLogins;
	public List<String> recipientPhoneNumbers;	
	public String login;
	public String content;
	public String smsTemplate;
	public String senderGroup;
	public String serviceKey;
	public MailToSend mailToSend;
}
