package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Fonction;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Business layer concerning smsu service.
 *
 */
public class FonctionManager {
	
	@Autowired private DaoService daoService;
	
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Retrieve all the fonctions defined in smsu database.
	 * @return
	 */
	public List<String> getAllFonctions() {
		List<String> result = new ArrayList<String>();
		for (Fonction fct : daoService.getFonctions()) {
			if (toFonctionName(fct.getName()) != null) // filter obsolete Fonctions (eg: FCTN_APPROBATION_ENVOI)
				result.add(fct.getName());
		}
		return result;
	}

	static FonctionName toFonctionName(String name) {
		for (FonctionName n : FonctionName.values()) {
			if (name.equals(n.name())) return n;
		}
		return null;
	}

}
