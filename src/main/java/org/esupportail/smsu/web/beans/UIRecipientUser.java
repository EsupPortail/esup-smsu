package org.esupportail.smsu.web.beans;

public class UIRecipientUser {
	public String id;
	public String name;
	public boolean noSMS;

	public UIRecipientUser(String id, String name, boolean noSMS) {
		this.id = id;
		this.name = name;
		this.noSMS = noSMS;
	}
}
