package org.esupportail.smsu.services.wsgroups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.esupportail.smsu.services.ldap.beans.UserGroup;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupWsgroups extends UserGroup {

		public UserGroupWsgroups() {
			super("", "");
		}
		
		public void setKey(String key) {
			this.id = key;
		}

		public void setName(String name) {
			this.name = name;
		}
}
