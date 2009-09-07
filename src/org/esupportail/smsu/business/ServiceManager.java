package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Service;
import org.esupportail.smsu.web.beans.UIService;

/**
 * Business layer concerning smsu service.
 *
 */
public class ServiceManager {
	
	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	///////////////////////////////////////
	//  constructor
	//////////////////////////////////////
	/**
	 * constructor.
	 */
	public ServiceManager() {
		super();
	}

	///////////////////////////////////////
	//  Principal methods
	//////////////////////////////////////
	/**
	 * retrieve all the service defined in smsu database.
	 * @return
	 */
	public List<Service> getAllServices() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieve the smsu services from the database");
		}
		List<Service> allServices = daoService.getServices();
		return allServices;
	}
	
	/**
	 * retrieve all the service defined in smsu database.
	 * @return
	 */
	public List<UIService> getAllUIServices() {
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieve the smsu services from the database");
		}
		List<UIService> allUiServices = new ArrayList<UIService>();
		List<Service> allServices = daoService.getServices();
		
		for (Service service : allServices) {
			Boolean isDeletable = canServiceBeDeleted(service);
			UIService ui = new UIService(service.getId(), service.getName(), service.getKey(), isDeletable);
			allUiServices.add(ui);
		}
		return allUiServices;
	}


	/**
	 * @param service
	 */
	public void updateUIService(final UIService service) {
		final Service svc = new Service(service.getId(), service.getName().trim(), service.getKey());
		daoService.updateService(svc);
	}
	
	/**
	 * @param service
	 */
	public void addUIService(final UIService service) {
		final Service svc = new Service(service.getId(), service.getName().trim(), service.getKey());
		daoService.addService(svc);
	}
	
	/**
	 * @param service
	 */
	public void deleteUIService(final UIService service) {
		final Service svc = new Service(service.getId(), service.getName(), service.getKey());
		daoService.deleteService(svc);
	}
	
	/**
	 * @param name
	 * @return a service
	 */
	public Service getServiceByName(final String name) {
		return daoService.getServiceByName(name);
	}
	
	/**
	 * @param key
	 * @return a service
	 */
	public Service getServiceByKey(final String key) {
		return daoService.getServiceByKey(key);
	}
	
	/**
	 * @param key
	 * @param id 
	 * @return true if no other service has the same key.
	 */
	public Boolean isKeyAvailable(final String key, final Integer id) {
		Service service = getServiceByKey(key);
		Boolean bReturn = true;
		
		if (service != null) {
			if (id != null) {
				Integer tId = service.getId();
				if (!tId.equals(id)) {
					bReturn = false;
				}
			} else {
				bReturn = false;
			}
		}
		
		return bReturn;
	}
	
	/**
	 * @param name
	 * @param id 
	 * @return true if no other service has the same key.
	 */
	public Boolean isNameAvailable(final String name, final Integer id) {
		Service service = getServiceByName(name);
		Boolean bReturn = true;
		
		if (service != null) {
			if (id != null) {
				Integer tId = service.getId();
				if (!tId.equals(id)) {
					bReturn = false;
				}
			} else {
				bReturn = false;
			}
		}
		
		return bReturn;
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
