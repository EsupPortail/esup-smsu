package org.esupportail.smsu.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.wsgroups.HttpRequestWsgroups;
import javax.inject.Inject;

public class GroupUtils {
	@Inject public LdapUtils ldapUtils;
	@Inject private DaoService daoService;
	@Inject private HttpRequestWsgroups wsgroups;
	public boolean customizedGroupsUseAttrsRegex;
	
	private final Logger logger = Logger.getLogger(getClass());


    private String getGroupDisplayName_(final String id) {
        if (customizedGroupsUseAttrsRegex) {
            return id;
        }
    	if (wsgroups.inUse()) {
			return wsgroups.getGroupDisplayName(this, id);
    	} else {
    		return ldapUtils.getGroupNameByIdOrNull(id);
    	}
	}
    
	private List<? extends UserGroup> getUserGroups(String login) {
		if (customizedGroupsUseAttrsRegex) {
		    return Collections.emptyList();
		}
		if (wsgroups.inUse()) {
			logger.debug("Use ws groups and not ldap groups");
			return wsgroups.getUserGroups(login);
		} else {
			logger.debug("Use ldap groups and not ws groups");
			return ldapUtils.getUserGroupsByUid(login);
		}
	}

	private List<String> getMemberIds(String groupId) {
		if (wsgroups.inUse()) {
			return wsgroups.getMemberIds(groupId); 
		} else {
			return ldapUtils.getMemberIds(groupId);
		}
    }

    public List<LdapUser> getMemberIds(String groupId, String serviceKey) {
        if (!wsgroups.inUse()) {
            try {
                return ldapUtils.getGroupUsers(groupId, serviceKey);
            } catch (LdapUtils.NoMemberOfOverlay e) {
                // fallback to 2-pass solution
            }
        }
        List<String> uids = getMemberIds(groupId);
        if (uids == null) return null;
        logger.debug("found " + uids.size() + " users in group " + groupId);

        List<LdapUser> users = ldapUtils.getConditionFriendlyLdapUsersFromUid(uids, serviceKey);
        logger.debug("found " + uids.size() + " users in group " + groupId + " and " + users.size() + " users having pager+CG");
        return users;
    }

	public Map<String,List<String>> group2parents(String label) {
		if (wsgroups.inUse()) {
			return wsgroups.group2parents(label);
		} else {
			// flat groups for LDAP
			return singletonMap(label, GroupUtils.<String>emptyList());
		}
	}
	
	public static <A> List<A> emptyList() {
		return new LinkedList<>();
	}
	
	public static <A,B> Map<A, B> singletonMap(A a, B b) {
		Map<A, B> o = new HashMap<>();
		o.put(a, b);
		return o;
	}

	public String getGroupDisplayName(CustomizedGroup cg) {
		return getGroupDisplayName(cg.getLabel());
	}
	public String getGroupDisplayName(BasicGroup cg) {
		return getGroupDisplayName(cg.getLabel());
	}
	public String getGroupDisplayName(final String id) {
		if (customizedGroupsUseAttrsRegex) {
		    CustomizedGroup cg = daoService.getCustomizedGroupByLabel(id);
		    return cg != null ? cg.getDisplayName() : id;
		}

		String displayName;
		try {
			displayName = ldapUtils.getUserDisplayNameByUserUid(id);
		} catch (LdapUserNotFoundException e) {
			displayName = getGroupDisplayName_(id);
		}
		return displayName != null ? displayName : id;
	}
    
	public List<UserGroup> getUserGroupsPlusSelfGroup(String login) {
		List<UserGroup> groups = new ArrayList<>();
		try {
		    groups.addAll(getUserGroups(login));
		} catch (Exception e) {
		    logger.debug("" + e, e); // nb: exception already logged in SmsuCachingUportalServiceImpl
		    // go on, things can still work using only the self group
		}
		UserGroup selfGroup = new UserGroup(login, ldapUtils.getUserDisplayName(login));
		groups.add(selfGroup);
		logger.debug("GroupsPlusSelf for " + login + " : " + groups);
		return groups;
	}
	
	public List<CustomizedGroup> getCustomizedGroups(String login, String loggedUserSortedAttributes) {
		List<CustomizedGroup> l = new ArrayList<>();

		if (customizedGroupsUseAttrsRegex) {
		    for (CustomizedGroup cg: daoService.getAllCustomizedGroups()) {
		        if (regex_matches_loggedUserSortedAttributes(cg.getLabel(), loggedUserSortedAttributes)) l.add(cg);
		    }
		    return l;
		}

		for (UserGroup group : getUserGroupsPlusSelfGroup(login)) {
			logger.debug("group login is: " + group.id);
			CustomizedGroup grp = daoService.getCustomizedGroupByLabel(group.id);
			if (grp != null) l.add(grp);
		}
		return l;
	}
	
    private boolean regex_matches_loggedUserSortedAttributes(String regex, String loggedUserSortedAttributes) {
        boolean b = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL).matcher(loggedUserSortedAttributes).find();
        logger.debug("regex_matches_loggedUserSortedAttributes: " + regex + " => " + b);
        return b;
    }

    public void setCustomizedGroupsUseAttrsRegex(boolean customizedGroupsUseAttrsRegex) {
        this.customizedGroupsUseAttrsRegex = customizedGroupsUseAttrsRegex;
    }
}
