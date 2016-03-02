package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.web.beans.UIService;
import org.springframework.beans.factory.annotation.Autowired;


public class ServiceManager {
	
	public static final String SERVICE_SEND_FUNCTION_PREFIX = "FCTN_SMS_ENVOI_SERVICE_";
	public static final String SERVICE_SEND_FUNCTION_CG = SERVICE_SEND_FUNCTION_PREFIX + "CG";
	
	public static final String SERVICE_ADH_FUNCTION_PREFIX = "FCTN_SMS_ADHESION_SERVICE_";
	
	@Autowired private DaoService daoService;
	
	@Autowired private SecurityManager securityManager;
	
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * retrieve all the service defined in smsu database.
	 */
	public List<UIService> getAllUIServices() {
		List<UIService> allUiServices = new ArrayList<UIService>();
		for (Service service : daoService.getServices()) {
			allUiServices.add(convertToUI(service));
		}
		return allUiServices;
	}
	
	/**
	 * retrieve services defined in smsu database that this user can use to send mail to.
	 */
	public List<UIService> getUIServicesSendFctn(String login) {
		List<UIService> allUiServices = new ArrayList<UIService>();
		Set<String> allowedFonctions = securityManager.loadUserRightsByUsername(login);
		if(allowedFonctions.contains(SERVICE_SEND_FUNCTION_CG)) {
			allUiServices.add(UIService.CG_SERVICE);
		}
		for (Service service : daoService.getServices()) {			
			if(allowedFonctions.contains(SERVICE_SEND_FUNCTION_CG) ||
					allowedFonctions.contains(SERVICE_SEND_FUNCTION_PREFIX + service.getKey().toUpperCase())) {
				allUiServices.add(convertToUI(service));
			}
		}
		return allUiServices;
	}
	
	/**
	 * retrieve services defined in smsu database that this user can use to register to (adhésion).
	 */
	public List<UIService> getUIServicesAdhFctn(String login) {
		List<UIService> allUiServices = new ArrayList<UIService>();
		Set<String> allowedFonctions = securityManager.loadUserRightsByUsername(login);
		for (Service service : daoService.getServices()) {			
			if(allowedFonctions.contains(SERVICE_ADH_FUNCTION_PREFIX + service.getKey().toUpperCase())) {
				allUiServices.add(convertToUI(service));
			}
		}
		return allUiServices;
	}
		
	public List<String> getAllAddonServicesSendFctn() {
		List<String> result = new ArrayList<String>();
		result.add(SERVICE_SEND_FUNCTION_CG);
		for (Service service : daoService.getServices()) {
			result.add(SERVICE_SEND_FUNCTION_PREFIX + service.getKey().toUpperCase());
		}
		return result;
	}
	
	
	public List<String> getAllAddonServicesAdhFctn() {
		List<String> result = new ArrayList<String>();
		for (Service service : daoService.getServices()) {
			result.add(SERVICE_ADH_FUNCTION_PREFIX + service.getKey().toUpperCase());
		}
		return result;
	}
 
	public void updateUIService(final UIService service) {
		daoService.updateService(convertFromUI(service));
	}
	
	public void addUIService(final UIService service) {
		
		// add also in the same time a corresponding service send function ... 
		String fonctionName = SERVICE_SEND_FUNCTION_PREFIX + service.key.toUpperCase();
		Fonction fonction = new Fonction();
		fonction.setName(fonctionName);
		daoService.addFonction(fonction);
		
		// add also in the same time a corresponding service adh function ... 
		String adhFonctionName = SERVICE_ADH_FUNCTION_PREFIX + service.key.toUpperCase();
		Fonction adhFonction = new Fonction();
		adhFonction.setName(adhFonctionName);
		daoService.addFonction(adhFonction);
		
		daoService.addService(convertFromUI(service));
	}
	
	public void deleteUIService(final int id) {
		Service service2Delete =  daoService.getServiceById(id);
		
		String fonctionName2Delete = SERVICE_SEND_FUNCTION_PREFIX + service2Delete.getKey().toUpperCase();
		Fonction fonction = daoService.getFonctionByName(fonctionName2Delete);
		if(fonction != null)
			daoService.deleteFonction(fonction);
		
		String adhFonctionName2Delete = SERVICE_ADH_FUNCTION_PREFIX + service2Delete.getKey().toUpperCase();
		Fonction adhFonction = daoService.getFonctionByName(adhFonctionName2Delete);
		if(adhFonction != null)
			daoService.deleteFonction(adhFonction);
		
		daoService.deleteService(service2Delete);
	}

	/**
	 * @param key
	 * @param id 
	 * @return true if no other service has the same key or if key unmodified
	 * 				AND key is not equals to ServiceManager.SERVICE_SEND_FUNCTION_CG 
	 */
	public Boolean isKeyAvailable(final String key, final Integer id) {
		Service existingService = daoService.getServiceByKey(key);
		return (existingService == null || id != null && id.equals(existingService.getId()))
				&& !ServiceManager.SERVICE_SEND_FUNCTION_CG.equals(key);
	}
	
	/**
	 * @param name
	 * @param id 
	 * @return true if no other service has the same name or if name unmodified
	 */
	public Boolean isNameAvailable(final String name, final Integer id) {
		Service existingService = daoService.getServiceByName(name);
		return existingService == null 
				|| id != null && id.equals(existingService.getId());
	}

	private UIService convertToUI(Service service) {
		UIService result = new UIService();
		result.id = service.getId();
		result.name = service.getName();
		result.key = service.getKey();
		result.isDeletable = canServiceBeDeleted(service);
		return result;
	}
 
	private Service convertFromUI(final UIService service) {
		return new Service(service.id, service.name.trim(), service.key);
	}

	/**
	 * @param service 
	 * @return true if the service can be deleted.
	 */
	private Boolean canServiceBeDeleted(final Service service) {
		List<Message> listMessages = daoService.getMessagesByService(service);
		return listMessages.isEmpty();
	}


}
