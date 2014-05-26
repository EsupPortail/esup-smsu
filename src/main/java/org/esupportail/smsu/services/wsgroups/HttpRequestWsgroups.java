package org.esupportail.smsu.services.wsgroups;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.services.GroupUtils;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.smsuapi.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class HttpRequestWsgroups {

	private String wsgroupsURL;
	
	private static final String DEFAULT_CACHE_NAME = HttpRequestWsgroups.class.getName();

	@Autowired private CacheManager cacheManager;

	private Cache cache;
	
	private final Logger logger = new LoggerImpl(getClass());

	public boolean inUse() {
		return !StringUtils.isEmpty(wsgroupsURL);
	}

	private Cache getCache() {
		if (cache == null) {
    		if (!cacheManager.cacheExists(DEFAULT_CACHE_NAME)) {
    			cacheManager.addCache(DEFAULT_CACHE_NAME);
    		}
    		cache = cacheManager.getCache(DEFAULT_CACHE_NAME);
    	}
		return cache;
	}
	
	private String requestRaw(String params) {
		String cooked_url = wsgroupsURL + "/" + params;
    	try {
    		HttpURLConnection conn = HttpUtils.openConnection(cooked_url);
    		return HttpUtils.requestGET(conn);    		
    	} catch (HttpException e) {
    		logger.error(e);
    		return null;
		}
	}

    private String cachedRequestRaw(String params) {
    	Element elt = getCache().get(params);
    	if (elt != null) {
    		return (String) elt.getObjectValue();
    	}
    	
    	String json = requestRaw(params);
    	getCache().put(new Element(params, json));
    	return json;
    }

    private <A> A request(String params, TypeReference<A> typeRef) {
    	String json = cachedRequestRaw(params);
    	if (json == null) return null;
    	try {
			return (new ObjectMapper()).readValue(json, typeRef);
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }
    
    private String cook_params(String name, String paramName, String paramVal) {
    	return name + "?" + paramName + "=" + HttpUtils.urlencode(paramVal);
    }

    private String cook_params(String name, String paramName, String paramVal, String paramName2, String paramVal2) {
    	return cook_params(name, paramName, paramVal) + "&" + paramName2 + "=" + HttpUtils.urlencode(paramVal2);
    }

    public UserGroupWsgroups getGroup(String id) {
    	return request(cook_params("getGroup", "key", id), new TypeReference<UserGroupWsgroups>() {});
    }

	public String getGroupDisplayName(GroupUtils groupUtils, final String id) {
		UserGroup group = getGroup(id);
		return group != null ? group.name : null;
	}
    
    public List<UserGroupWsgroups> getUserGroups(String login) {
    	return request(cook_params("userGroupsId", "uid", login), 
    		       new TypeReference<List<UserGroupWsgroups>>() {});
    }

    public List<String> getMemberIds(String groupId) {
    	return request(cook_params("groupUsers", "key", groupId, "attr", "uid"),
    			new TypeReference<List<String>>() {});
    }

	public void setWsgroupsURL(String wsgroupsURL) {
		this.wsgroupsURL = wsgroupsURL;
	}    
}