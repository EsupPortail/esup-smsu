package org.esupportail.smsu.exceptions;

import org.esupportail.commons.services.i18n.I18nService;

public abstract class CreateMessageException extends Exception {

	private static final long serialVersionUID = 6792087453400066168L;
	
	abstract public String toI18nString(I18nService i18nService);

	static public class Wrapper extends CreateMessageException {

		private static final long serialVersionUID = 6792087453400066168L;
	
		private String errorMsg;
		public Exception previousException;

		public Wrapper(String errorMsg, Exception e) {
			this.errorMsg = errorMsg;
			this.previousException = e;
		}

		public String toString() {
			return errorMsg;
		}

		public String toI18nString(I18nService i18nService) {
			return toString();
		}
	}

	static public class GroupMaxSmsPerMessage extends CreateMessageException {
	
		private static final long serialVersionUID = 2515164261120013750L;

		public String toString() {
			return "CreateMessageException.GroupMaxSmsPerMessage";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.SENDERGROUPNDMAXSMSERROR",
						     i18nService.getDefaultLocale());
		}

	}

	static public class GroupQuotaException extends CreateMessageException {
	
		private static final long serialVersionUID = -6132084495675709441L;

		public String toString() {
			return "CreateMessageException.GroupQuotaException";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.SENDERGROUPQUOTAERROR", 
						     i18nService.getDefaultLocale());
		}

	}

	static public class UnknownCustomizedTag extends CreateMessageException {

		private static final long serialVersionUID = 6792087453400066168L;
	
		final private String tag;
		
		public UnknownCustomizedTag(final String tag) {
			this.tag = tag;
		}
		
		public String getTag() {
			return tag;
		}
	
		public String toString() {
			return "unknown tag <" + tag + ">";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.UNKNOWNCUSTOMIZEDTAG",
						     i18nService.getDefaultLocale(), tag);
		}

	}

	static public class CustomizedTagValueNotFound extends CreateMessageException {

		private static final long serialVersionUID = 6792087453400066168L;
	
		final private String tag;
		
		public CustomizedTagValueNotFound(final String tag) {
			this.tag = tag;
		}
		
		public String getTag() {
			return tag;
		}
	
		public String toString() {
			return "tag <" + tag + "> has no value";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.CUSTOMIZEDTAGVALUENOTFOUND",
						     i18nService.getDefaultLocale(), tag);
		}

	}

	static public class EmptyGroup extends CreateMessageException {
		
		private static final long serialVersionUID = 8476880753507079446L;

		final private String groupName;
		
		public EmptyGroup(final String groupName) {
			this.groupName = groupName;
		}
	
		public String toString() {
			return "group " + groupName + " is empty";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.EMPTYGROUP",
						     i18nService.getDefaultLocale(), groupName);
		}

	}

}
