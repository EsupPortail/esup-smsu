package org.esupportail.smsu.services.wsgroups;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupWithSuperGroups {
	List<String> superGroups;
	
	public void setSuperGroups(List<String> superGroups) {
		this.superGroups = superGroups;
	}
}
