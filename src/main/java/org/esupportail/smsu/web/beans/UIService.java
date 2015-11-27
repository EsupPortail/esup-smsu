package org.esupportail.smsu.web.beans;

import org.esupportail.smsu.business.ServiceManager;

public class UIService {
	
	public static final UIService CG_SERVICE = new UIService(-1, ServiceManager.SERVICE_SEND_FUNCTION_CG, "Aucun", false);
	
	public Integer id;
	public String key;
	public String name;
	public Boolean isDeletable;
	
	public UIService() {
		super();
	}

	public UIService(Integer id, String key, String name, Boolean isDeletable) {
		super();
		this.id = id;
		this.key = key;
		this.name = name;
		this.isDeletable = isDeletable;
	}
	
	
}
