/**
 * ESUP-Portail esup-smsu - Copyright (c) 2006-2012 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu
 */
package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.esupportail.smsu.domain.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/accounts")
public class AccountsController {

	//private final Logger logger = new LoggerImpl(this.getClass());

	@Autowired private DomainService domainService;

	@GET
	@Produces("application/json")
	public List<String> getAccounts() {
		return domainService.getAccounts();
	}

}
