package org.esupportail.smsu.web.beans;

import java.util.List;

public class UICustomizedGroup {
	public Integer id;
	public String label;
	public boolean labelIsUserId;
	public long quotaSms;
	public long maxPerSms;
	public long consumedSms;
	public String role; 
	public String account;
	public List<String> supervisors;
}
