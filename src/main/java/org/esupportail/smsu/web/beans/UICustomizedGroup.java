package org.esupportail.smsu.web.beans;

import java.util.Map;

public class UICustomizedGroup {
	public Integer id;
	public String label;
	public boolean labelIsUserId;
	public String displayName;
	public long quotaSms;
	public long maxPerSms;
	public long consumedSms;
	public String role; 
	public String account;
	public Map<String, String> supervisors;
}
