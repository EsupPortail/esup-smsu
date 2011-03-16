package org.esupportail.smsu.business;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.ldap.LdapUtils;

/**
 * @author xphp8691
 *
 */
public class ContentCustomizationManager {

	/**
	 * LDAP utils.
	 */
	private LdapUtils ldapUtils;

	/**
	 * defaultNotFoundData.
	 */
	private String defaultNotFoundData;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	///////////////////////////////////////
	//  constructor
	//////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public ContentCustomizationManager() {
		super();
	}

	///////////////////////////////////////
	//  Principal methods
	//////////////////////////////////////
	/**
	 * @param content
	 * @return the list of sender tags found in the content.
	 */
	private Set<String> extractExpTags(final String content) {
		Pattern tagPattern = Pattern.compile("<(EXP_[a-zA-Z_]+)>");	
		Set<String> tagList = findUniqueMatches1(tagPattern, content);
		
		for (String tag : tagList)
			logger.debug("New Exp tag found : " + tag);

		return tagList;
	}

	/**
	 * @param content
	 * @return the list of recipient tags found in the content.
	 */
	private Set<String> extractDestTags(final String content) {
		Pattern tagPattern = Pattern.compile("<(DEST_[a-zA-Z_]+)>");
		Set<String> tagList = findUniqueMatches1(tagPattern, content);
		
		for (String tag : tagList)
			logger.debug("New Dest tag found : " + tag);

		return tagList;
	}
	
	private Set<String> findUniqueMatches1(Pattern regex, String s) {
		Set<String> set = new TreeSet<String>();
		Matcher matcher = regex.matcher(s);
		while (matcher.find()) set.add(matcher.group(1));
		return set;
	}

	/**
	 * @param content
	 * @param destUid
	 * @return the content customized with the recipient data.
	 */
	public String customizeDestContent(String content, final String destUid) {

		for (String tag : extractDestTags(content)) {
			String tagRepl = null;
			try {
				if (destUid == null) {
					logger.info("Recipient has no ID default");
				} else {
					tagRepl = computeTagValue(tag, destUid);
				}
			} catch (LdapUserNotFoundException e) {
				logger.info("<" + tag + "> not found for user with id : [" + destUid + "]", e);
			}
			if (tagRepl == null) {
			    logger.info("using default data for <" + tag + ">");
			    tagRepl = defaultNotFoundData;
			}
			content = content.replaceAll("<" + tag + ">", tagRepl);
		}
		logger.debug("customized content :" + content);
		return content;
	}

	/**
	 * @param content
	 * @param expGroupName
	 * @param expUid
	 * @return the content customized with the sender data.
	 * @throws LdapUserNotFoundException 
	 */
	public String customizeExpContent(String content, final String expGroupName, final String expUid) throws LdapUserNotFoundException {
		Set<String> tags = extractExpTags(content);

		for (String tag : tags) {
			String tagRepl;
			try {
				if (tag.equals("EXP_GROUPE_NOM")) {
					tagRepl = expGroupName(expGroupName);
				} else {
					tagRepl = computeTagValue(tag, expUid);
				}
			} catch (LdapUserNotFoundException e) {
				String messageStr = "Unable to find the user with id : [" + expUid + "]";
				logger.warn(messageStr, e);
				throw new LdapUserNotFoundException(messageStr, e);
			}
			if (tagRepl != null)
				content = content.replaceAll("<" + tag + ">", tagRepl);
		}

		logger.debug("customized content :" + content);
		return content;
	}

	private String computeTagValue(String tag, String uid) throws LdapUserNotFoundException {
		String v;
		
		if (tag.equals("DEST_NOM") || tag.equals("EXP_NOM")) {
			v = ldapUtils.getUserLastNameByUid(uid);
		} else if (tag.equals("DEST_PRENOM")) {
			v = ldapUtils.getUserFirstNameByUid(uid);
		} else if (tag.equals("EXP_TEL_RAPPEL")) {
			v = ldapUtils.getUserPagerByUid(uid);
		} else {
			String ldapAttribut = StringUtils.substringAfter(tag, "DEST_LDAP_");
			if (ldapAttribut.equals(""))
				ldapAttribut = StringUtils.substringAfter(tag, "EXP_LDAP_");

			if (!ldapAttribut.equals("")) {
				v = ldapAttribute(uid, ldapAttribut);
			} else {
				logger.warn("Keeping unknown tag " + tag + " unchanged");
				v = null;
			}
		}
		return v;
	}

	private String expGroupName(final String expGroupName) {
		// a sending group can be a user name
		String groupName;
		try {
			groupName = ldapUtils.getUserDisplayNameByUserUid(expGroupName);
		} catch (LdapUserNotFoundException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("User not found : " + expGroupName);
			}
			groupName = ldapUtils.getGroupNameByUid(expGroupName);
			if (groupName == null) {
				logger.debug("Group not found : " + expGroupName);
				groupName = expGroupName;
			}	
		}
		 
		if (logger.isDebugEnabled()) {
			logger.debug("Group name used :" + groupName);
		}

		return groupName;
	}

	private String ldapAttribute(String uid, String ldapAttribut) throws LdapUserNotFoundException {
		List<String> values = ldapUtils.getLdapAttributesByUidAndName(uid, ldapAttribut);
		return values.isEmpty() ? "" : values.get(0);
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of ldapUtils
	//////////////////////////////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	/**
	 * @return ldapUtils
	 */
	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of defaultNotFoundData
	//////////////////////////////////////////////////////////////
	/**
	 * @return defaultNotFoundData
	 */
	public String getDefaultNotFoundData() {
		return defaultNotFoundData;
	}

	/**
	 * @param defaultNotFoundData
	 */
	public void setDefaultNotFoundData(final String defaultNotFoundData) {
		this.defaultNotFoundData = defaultNotFoundData;
	}


}
