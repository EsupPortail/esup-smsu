package org.esupportail.smsu.business;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.services.ldap.LdapUtils;

import javax.inject.Inject;

/**
 * Business layer concerning smsu service.
 *
 */
public class FonctionManager {
	
	@Inject private DaoService daoService;
	
	@Inject private ServiceManager serviceManager;
	@Inject private LdapUtils ldapUtils;
	
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Retrieve all the fonctions defined in smsu database.
	 * @return
	 */
	public Set<String> getAllFonctions() {
		Set<String> result = new HashSet<>();
		for (Fonction fct : daoService.getFonctions()) {
			result.add(fct.getName());
        }
        if (ldapUtils.disabled) {
            result.remove("FCTN_SMS_ENVOI_ADH");
            result.remove("FCTN_SMS_REQ_LDAP_ADH");
            result.remove("FCTN_SMS_ENVOI_GROUPES");
        }
		result.addAll(serviceManager.getAllAddonServicesSendFctn());
		result.addAll(serviceManager.getAllAddonServicesAdhFctn());
		return result;
	}

}
