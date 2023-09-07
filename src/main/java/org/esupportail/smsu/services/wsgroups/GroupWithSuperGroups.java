package org.esupportail.smsu.services.wsgroups;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupWithSuperGroups {
	List<String> superGroups;
	
	public void setSuperGroups(List<String> superGroups) {
		this.superGroups = superGroups;
	}
}
