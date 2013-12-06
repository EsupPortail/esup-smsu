package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.web.beans.UIService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Business layer concerning smsu service.
 *
 */
public class ServiceManager {
	
	@Autowired private DaoService daoService;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	public Map<String, String> getUIServices() {
		Map<String, String> result = new HashMap<String,String>();
		for (Service service : daoService.getServices())
			result.put(service.getKey(), service.getName());
		return result;
	}

	/**
	 * retrieve all the service defined in smsu database.
	 * @return
	 */
	public List<UIService> getAllUIServices() {
		List<UIService> allUiServices = new ArrayList<UIService>();
		for (Service service : daoService.getServices()) {
			allUiServices.add(convertToUI(service));
		}
		return allUiServices;
	}
 
	public void updateUIService(final UIService service) {
		daoService.updateService(convertFromUI(service));
	}
	
	/**
	 * @param service
	 */
	public void addUIService(final UIService service) {
		daoService.addService(convertFromUI(service));
	}
	
	public void deleteUIService(final int id) {
		daoService.deleteService(daoService.getServiceById(id));
	}

	/**
	 * @param key
	 * @param id 
	 * @return true if no other service has the same key or if key unmodified
	 */
	public Boolean isKeyAvailable(final String key, final Integer id) {
		Service existingService = daoService.getServiceByKey(key);
		return existingService == null 
				|| id != null && id.equals(existingService.getId());
	}
	
	/**
	 * @param name
	 * @param id 
	 * @return true if no other service has the same key.
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
		List<Message> listMessages = new ArrayList<Message>();
		listMessages = daoService.getMessagesByService(service);
		
		if (listMessages.isEmpty()) {
			return true;
		}
		
		return false;
	}

	////////////////////////////////////////
	//  setter for spring object daoService
	////////////////////////////////////////
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

}
