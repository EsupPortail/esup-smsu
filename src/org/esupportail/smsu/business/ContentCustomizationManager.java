package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;
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
	 * tag start.
	 */
	private static final String CS_START_TAG = "<";

	/**
	 * tag end.
	 */
	private static final String CS_END_TAG = ">";

	/**
	 * sender name tag.
	 */
	private static final String CS_EXP_NOM_TAG = CS_START_TAG + "EXP_NOM" + CS_END_TAG;

	/**
	 * sender phone tag.
	 */
	private static final String CS_EXP_TEL_RAPPEL_TAG = CS_START_TAG + "EXP_TEL_RAPPEL" + CS_END_TAG;

	/**
	 * sender group tag.
	 */
	private static final String CS_EXP_GROUP_NOM_TAG = CS_START_TAG + "EXP_GROUPE_NOM" + CS_END_TAG;

	/**
	 * recipient last name tag.
	 */
	private static final String CS_DEST_NOM_TAG = CS_START_TAG + "DEST_NOM" + CS_END_TAG;

	/**
	 * recipient name tag.
	 */
	private static final String CS_DEST_PRENOM_TAG = CS_START_TAG + "DEST_PRENOM" + CS_END_TAG;

	/**
	 * ldap recipient start tag.
	 */
	private static final String CS_DEST_LDAP_STARTING_TAG = CS_START_TAG + "DEST_LDAP_";

	/**
	 * ldap sender start tag.  
	 */
	private static final String CS_EXP_LDAP_STARTING_TAG = CS_START_TAG + "EXP_LDAP_";

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
	private List<String> extractExpTags(final String content) {
		List<String> tagList = new ArrayList<String>();
		Pattern tagPattern = Pattern.compile("<EXP[a-zA-Z_]+>");
		Matcher matcher = tagPattern.matcher(content);

		while (matcher.find()) {
			if (!tagList.contains(matcher.group())) {
				logger.debug("New Exp tag found : " + matcher.group());
				tagList.add(matcher.group());
			}
		}

		return tagList;
	}

	/**
	 * @param content
	 * @return the list of recipient tags found in the content.
	 */
	private List<String> extractDestTags(final String content) {
		List<String> tagList = new ArrayList<String>();
		Pattern tagPattern = Pattern.compile("<DEST[a-zA-Z_]+>");
		Matcher matcher = tagPattern.matcher(content);

		while (matcher.find()) {
			if (!tagList.contains(matcher.group())) {
				logger.debug("New Dest tag found : " + matcher.group());
				tagList.add(matcher.group());
			}
		}

		return tagList;
	}

	/**
	 * @param tag
	 * @return true if the tag is a sender first name tag.
	 */
	Boolean isExpNomTag(final String tag) {
		if (tag.equals(CS_EXP_NOM_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param tag
	 * @return true if the tag is a sender group tag.
	 */
	Boolean isExpGroupNomTag(final String tag) {
		if (tag.equals(CS_EXP_GROUP_NOM_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param tag
	 * @return true if the tag is a recipient last name tag.
	 */
	Boolean isDestNomTag(final String tag) {
		if (tag.equals(CS_DEST_NOM_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param tag
	 * @return true if the tag is a recipient first name tag.
	 */
	Boolean isDestPrenomTag(final String tag) {
		if (tag.equals(CS_DEST_PRENOM_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param tag
	 * @return true if the tag is a sender phone tag.
	 */
	Boolean isExpTelRappelTag(final String tag) {
		if (tag.equals(CS_EXP_TEL_RAPPEL_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param tag
	 * @return true if the tag is a recipient ldap tag. 
	 */
	Boolean isDestLdapTag(final String tag) {
		if (tag.contains(CS_DEST_LDAP_STARTING_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param tag
	 * @return true if the tag is a sender ldap tag.
	 */
	Boolean isExpLdapTag(final String tag) {
		if (tag.contains(CS_EXP_LDAP_STARTING_TAG)) {
			return true;
		}

		return false;
	}

	/**
	 * @param content
	 * @param destUid
	 * @return the content customized with the recipient data.
	 */
	public String customizeDestContent(final String content,
			final String destUid) {
		String cutomizedContent = content;
		List<String> tags = extractDestTags(content);
		String destLastName;
		String destFirstName;
		String customAttribut;

		if (!tags.isEmpty()) {
			for (String tag : tags) {

				if (isDestNomTag(tag)) {
					if (destUid != null) {
						try {
							destLastName = ldapUtils.getUserLastNameByUid(destUid);
						} catch (LdapUserNotFoundException e) {
							final StringBuilder message = new StringBuilder();
							message.append("Recipient name not found for user with id : [");
							message.append(destUid);
							message.append("]");
							final String messageStr = message.toString();
							logger.warn(messageStr, e);
							destLastName = defaultNotFoundData;
						}
					} else {
						final StringBuilder message = new StringBuilder();
						message.append("Recipient has no ID default data used");
						final String messageStr = message.toString();
						logger.debug(messageStr);
						destLastName = defaultNotFoundData;
					}
					cutomizedContent = cutomizedContent.replaceAll(tag, destLastName);
				}

				if (isDestPrenomTag(tag)) {
					if (destUid != null) {
						try {
							destFirstName = ldapUtils.getUserFirstNameByUid(destUid);
						} catch (LdapUserNotFoundException e) {
							final StringBuilder message = new StringBuilder();
							message.append("Recipient first name not found for user with id : [");
							message.append(destUid);
							message.append("]");
							final String messageStr = message.toString();
							logger.warn(messageStr, e);
							destFirstName = defaultNotFoundData;
						}
					} else {
						final StringBuilder message = new StringBuilder();
						message.append("Recipient has no ID default data used");
						final String messageStr = message.toString();
						logger.debug(messageStr);
						destFirstName = defaultNotFoundData;
					}
					cutomizedContent = cutomizedContent.replaceAll(tag, destFirstName);
				}

				if (isDestLdapTag(tag)) {

					String ldapAttribut = getLdapDestAttribut(tag);
					if (destUid != null) {
						try {
							List<String> values = ldapUtils.getLdapAttributesByUidAndName(destUid,
									ldapAttribut);
							customAttribut = "";
							if (!values.isEmpty()) {
								customAttribut = values.get(0);
							}
						} catch (LdapUserNotFoundException e) {
							final StringBuilder message = new StringBuilder();
							message.append("Recipient custom data not found for user with id : [");
							message.append(destUid);
							message.append("]");
							final String messageStr = message.toString();
							logger.warn(messageStr, e);
							customAttribut = defaultNotFoundData;
						}
					} else {
						final StringBuilder message = new StringBuilder();
						message.append("Recipient has no ID default data used");
						final String messageStr = message.toString();
						logger.debug(messageStr);
						customAttribut = defaultNotFoundData;
					}
					cutomizedContent = cutomizedContent.replaceAll(tag, customAttribut);
				}
			}
		}

		logger.debug("customized content :" + cutomizedContent);
		return cutomizedContent;
	}

	/**
	 * @param content
	 * @param expGroupName
	 * @param expUid
	 * @return the content customized with the sender data.
	 * @throws LdapUserNotFoundException 
	 */
	public String customizeExpContent(final String content,
			final String expGroupName,
			final String expUid) throws LdapUserNotFoundException {
		String cutomizedContent = content;
		List<String> tags = extractExpTags(content);
		String customAttribut;

		if (!tags.isEmpty()) {
			for (String tag : tags) {
				try {

					if (isExpGroupNomTag(tag)) {
						// a sending group can be a user name
						String expGroupDisplayName = null;
						try {
							expGroupDisplayName = ldapUtils.getUserDisplayNameByUserUid(expGroupName);
						} catch (LdapUserNotFoundException e) {
							if (logger.isDebugEnabled()) {
								logger.debug("User not found : " + expGroupName);
							}
							expGroupDisplayName = ldapUtils.getGroupNameByUid(expGroupName);
							if (expGroupDisplayName == null) {
								logger.debug("Group not found : " + expGroupName);
								expGroupDisplayName = expGroupName;
							}	
						}
						 
						if (logger.isDebugEnabled()) {
							logger.debug("Group name used :" + expGroupDisplayName);
						}
						cutomizedContent = cutomizedContent.replaceAll(tag, expGroupDisplayName);
					}

					if (isExpNomTag(tag)) {
						String expName = ldapUtils.getUserLastNameByUid(expUid);
						cutomizedContent = cutomizedContent.replaceAll(tag, expName);
					}

					if (isExpTelRappelTag(tag)) {
						String expTel = ldapUtils.getUserPagerByUid(expUid);
						cutomizedContent = cutomizedContent.replaceAll(tag, expTel);
					}

					if (isExpLdapTag(tag)) {
						String ldapAttribut = getLdapExpAttribut(tag);
						List<String> values = ldapUtils.getLdapAttributesByUidAndName(expUid,
								ldapAttribut);
						customAttribut = "";
						if (!values.isEmpty()) {
							customAttribut = values.get(0);
						}

						cutomizedContent = cutomizedContent.replaceAll(tag, customAttribut);
					}
				} catch (LdapUserNotFoundException e) {
					final StringBuilder message = new StringBuilder();
					message.append("Unable to find the user with id : [");
					message.append(expUid);
					message.append("]");
					final String messageStr = message.toString();
					logger.warn(messageStr, e);
					throw new LdapUserNotFoundException(messageStr, e);
				}
			}
		}

		logger.debug("customized content :" + cutomizedContent);
		return cutomizedContent;
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

	/**
	 * @param tag
	 * @return the Recipient LDAP attribute to search.
	 */
	private String getLdapDestAttribut(final String tag) {
		return StringUtils.substringBetween(tag, CS_DEST_LDAP_STARTING_TAG, CS_END_TAG);
	}

	/**
	 * @param tag
	 * @return the sender LDAP Attribute to search.
	 */
	private String getLdapExpAttribut(final String tag) {
		return StringUtils.substringBetween(tag, CS_EXP_LDAP_STARTING_TAG, CS_END_TAG);
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
