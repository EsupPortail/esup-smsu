package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.web.beans.UIService;
import org.springframework.beans.factory.annotation.Autowired;


public class ServiceManager {
	
	@Autowired private DaoService daoService;
	
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
 
	public void updateUIService(final UIService service) {
		daoService.updateService(convertFromUI(service));
	}
	
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
