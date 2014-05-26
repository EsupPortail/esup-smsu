package org.esupportail.smsu.services;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.wsgroups.HttpRequestWsgroups;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupUtils {
	@Autowired private LdapUtils ldapUtils;
	@Autowired private DaoService daoService;
	@Autowired private HttpRequestWsgroups wsgroups;
	
	private final Logger logger = new LoggerImpl(getClass());


    private String getGroupDisplayName_(final String id) {
    	if (wsgroups.inUse()) {
			return wsgroups.getGroupDisplayName(this, id);
    	} else {
    		return ldapUtils.getGroupNameByIdOrNull(id);
    	}
	}
    
	private List<? extends UserGroup> getUserGroups(String login) {
		if (wsgroups.inUse()) {
			return wsgroups.getUserGroups(login);
		} else {
			return ldapUtils.getUserGroupsByUid(login);
		}
	}

	public List<String> getMemberIds(String groupId) {
		if (wsgroups.inUse()) {
			return wsgroups.getMemberIds(groupId); 
		} else {
			return ldapUtils.getMemberIds(groupId);
		}
	}

	public String getGroupPathByLabel(String label) {
		if (wsgroups.inUse()) {
			// TODO? https://wsgroups.univ-paris1.fr/getSuperGroups?key=groups-mati01418C02&depth=3
			return label;
		} else {
			// flat groups for LDAP
			return label;
		}
	}
	
	public String getGroupDisplayName(CustomizedGroup cg) {
		return getGroupDisplayName(cg.getLabel());
	}
	public String getGroupDisplayName(BasicGroup cg) {
		return getGroupDisplayName(cg.getLabel());
	}
	public String getGroupDisplayName(final String id) {
		String displayName;
		try {
			displayName = ldapUtils.getUserDisplayNameByUserUid(id);
		} catch (LdapUserNotFoundException e) {
			displayName = getGroupDisplayName_(id);
		}
		return displayName != null ? displayName : id;
	}
    
	public List<UserGroup> getUserGroupsPlusSelfGroup(String login) {
		List<UserGroup> groups = new ArrayList<UserGroup>();
		try {
		    groups.addAll(getUserGroups(login));
		} catch (Exception e) {
		    logger.debug("" + e, e); // nb: exception already logged in SmsuCachingUportalServiceImpl
		    // go on, things can still work using only the self group
		}
		UserGroup selfGroup = new UserGroup(login, ldapUtils.getUserDisplayName(login));
		groups.add(selfGroup);
		return groups;
	}
	
	public List<CustomizedGroup> getCustomizedGroups(String login) {
		List<CustomizedGroup> l = new ArrayList<CustomizedGroup>();

		for (UserGroup group : getUserGroupsPlusSelfGroup(login)) {
			logger.debug("group login is: " + group.id);
			CustomizedGroup grp = daoService.getCustomizedGroupByLabel(group.id);
			if (grp != null) l.add(grp);
		}
		return l;
	}
	
}
