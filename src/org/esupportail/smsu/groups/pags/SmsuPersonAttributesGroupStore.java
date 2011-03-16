package org.esupportail.smsu.groups.pags;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.jasig.portal.groups.pags.IPersonAttributesConfiguration;
import org.jasig.portal.groups.pags.IPersonTester;

@SuppressWarnings("unchecked")
public class SmsuPersonAttributesGroupStore {

	private Map groupDefinitions;
	private final Logger logger = new LoggerImpl(getClass());

	private Properties props;

	public SmsuPersonAttributesGroupStore() {
		try {
			props = new Properties();
			props.load(SmsuPersonAttributesGroupStore.class.getResourceAsStream("/properties/groups/pags.properties"));
			IPersonAttributesConfiguration config = getConfig(props.getProperty("org.jasig.portal.groups.pags.PersonAttributesGroupStore.configurationClass"));
			groupDefinitions = config.getConfig();
		} catch ( Exception e ) {
			String errorMsg = "PersonAttributeGroupStore.init(): " + "Problem initializing groups: " + e.getMessage();
			logger.error("Problem initializing groups.", e);
			throw new RuntimeException(errorMsg);
		}
	}

	public GroupDefinition getGroupDefinition(final String key) {
		if (logger.isDebugEnabled()) {
			logger.debug("Searching for group definition for key : [" + key + "]");
		}
		GroupDefinition gd = (GroupDefinition) groupDefinitions.get(key);
		if (logger.isDebugEnabled()) {
			if (gd != null) {
				logger.debug("Group definition found : " + gd.toString());
			} else {
				logger.debug("Group definition not found");
			}
		}
		return gd;
	}

	private IPersonAttributesConfiguration getConfig(final String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class configClass = Class.forName(className);
		Object o = configClass.newInstance();
		return (IPersonAttributesConfiguration) o;
	}

	public static class GroupDefinition {
		private String key;
		private String name;
		private String description;
		private List members;
		private List testGroups;
		public GroupDefinition() {
			members = new Vector();
			testGroups = new Vector();
		}

		public void setKey(final String key) {
			this.key = key;
		}
		public String getKey() {
			return key;
		}

		public void setName(final String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}

		public void setDescription(final String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		public void addMember(final String key) {
			members.add(key);
		}
		public boolean hasMember(final String key) {
			return members.contains(key);
		}
		public void addTestGroup(final TestGroup testGroup) {
			testGroups.add(testGroup);
		}
		
		public String toString() {
			return "GroupDefinition " + key + " (" + name + ")";
		}
		
		/*
		 *added by xphp8691 
		 */
		public List<TestGroup> getTestGroups() {
			return this.testGroups;
		}
	}

	public static class TestGroup {
		private List tests;

		public TestGroup() {
			tests = new Vector();
		}

		public void addTest(final IPersonTester test) {
			tests.add(test);
		}
		
		/*
		 * added by xphp8691
		 */
		public List getTests() {
			return this.tests;
		}
	}
}
