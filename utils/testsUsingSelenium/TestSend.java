import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;

public class TestSend extends SeleneseTestCase {
	String baseURL = "http://localhost:8080/";

	int maxLengthMessage = 160;
	String biggestMessage = "0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 012345";
	String test_phone = "0607290787";
	String user1smsutest_phone = "0601010101";
	String user2smsutest_phone = "0701010101";
	String defaultPassword = "dfgdfg";
	String testService = "service1 de test";

	String openPagsTree = "tree:0:t2";
	String pagsLocator = "//span[text()='Groupes LDAP']";

	String timeout = "30000";

	String current_login = null;


	public void setUp() throws Exception {
		setUp(baseURL, "*chrome");
	}

	//*HELPER FUNCTIONS *************************************************************
	void clickAndWait(String s) {
		selenium.click(s);
		selenium.waitForPageToLoad(timeout);
	}
	void selectAndWait(String selectLocator, String optionLocator) {
		selenium.select(selectLocator, optionLocator);
		selenium.waitForPageToLoad(timeout);
	}

	boolean clickAndWaitIfPresent(String locator) {
		if (selenium.isElementPresent(locator)) {
			clickAndWait(locator);
			return true;
		} else
			return false;
	}

	String inputLocatorByValue(String valueString) {
		return "//input[@value='" + valueString + "']";
	}

	void forceLogout() {
		selenium.open("/");
		clickAndWaitIfPresent("navigationForm:login");		
		clickAndWaitIfPresent("navigationForm:logout");
		//selenium.deleteAllVisibleCookies();
		current_login = null;
	}

	void ensureLogin(String username) {
		if (!username.equals(current_login)) login(username);
	}
	void login(String username) {
		login(username, defaultPassword);
	}
	void login(String username, String password) {
		forceLogout();

		selenium.open("/");
		clickAndWait("navigationForm:login");
		selenium.type("username", username);
		selenium.type("password", password);
		clickAndWait("submit");
		current_login = username;
	}

	void sendSMSByPhone(String phone, String body) {
		clickAndWait("navigationForm:envoiSMS");
		selectAndWait("formGeneral:selectTypeRecipient", "label=Phones");
		selenium.type("formGeneral:phoneNumber", phone);
		clickAndWait(inputLocatorByValue("Ajouter"));
		selenium.type("formGeneral:SMSbody", body);
		clickAndWait("formGeneral:sendSMSButton");
	}

	void sendSMSByUser(String user, int nbMatches, String body) {
		sendSMSByUser(user, nbMatches, body, null);
	}

	void sendSMSByUser(String user, int nbMatches, String body, String service) {
		clickAndWait("navigationForm:envoiSMS");

		selenium.select("formGeneral:selectTypeRecipient", "label=Users");
		//no waitForPageToLoad since "Users" is the default

		if (service != null) selectAndWait("formGeneral:selectService", service);
		selenium.type("formGeneral:ldapUid", user);
		clickAndWait(inputLocatorByValue("Search"));

		for (int c = 0; c < nbMatches; c++)
			clickAndWait("formGeneral:recipientSearchList:" + c + ":add");

		selenium.type("formGeneral:SMSbody", body);
		clickAndWait("formGeneral:sendSMSButton");
	}

	void sendSMSByGroup(String[] pagsLocators, String body, String service) {
		clickAndWait("navigationForm:envoiSMS");
		selectAndWait("formGeneral:selectTypeRecipient", "label=User Group");
		if (service != null) selectAndWait("formGeneral:selectService", service);
		for (String locator : pagsLocators) clickAndWait(locator);
		selenium.type("formGeneral:SMSbody", body);
		clickAndWait("formGeneral:sendSMSButton");
	}

	void navigationForm_gestionRoles() {
		ensureLogin("adminsmsutest");
		clickAndWait("navigationForm:gestionRoles");
	}
	void navigationForm_gestionGroupes() {
		ensureLogin("adminsmsutest");
		clickAndWait("navigationForm:gestionGroupes");
	}
	void navigationForm_gestionServicesCP() {
		ensureLogin("adminsmsutest");
		clickAndWait("navigationForm:gestionServicesCP");
	}

	boolean clickDeleteRoleOrGroup(String name) {
		String tr = "//span[text()='" + name + "']/ancestor::tr";
		String deleteButton = "//input[contains(@id, ':delete')]";
		if (clickAndWaitIfPresent(tr + deleteButton))
			return true;
		else {
			trueOrFail(!selenium.isElementPresent(tr),
				   "expected " + name + " to have a delete button");
			return false;
		}
	}

	boolean mayDeleteRole(String name) {
		navigationForm_gestionRoles();
		return clickDeleteRoleOrGroup(name);
	}
	void createRole(String name, int[] fonctions) {
		navigationForm_gestionRoles();
		clickAndWait(inputLocatorByValue("Create role"));
		selenium.type("//input[contains(@name,':Name')]", name);
		for (int fonction : fonctions)
			selenium.check("//input[contains(@name,':validFonctions') and @value='" + fonction + "']");
		clickAndWait(inputLocatorByValue("Save"));
	}

	boolean mayDeleteGroup(String name) {
		navigationForm_gestionGroupes();
		return clickDeleteRoleOrGroup(name);
	}

	void startCreateGroup() {
		navigationForm_gestionGroupes();
		clickAndWait(inputLocatorByValue("Create group"));
	}

	void createGroup(String name, String[] supervisors, String role, int quota, int maxSmsPerSend) {
		startCreateGroup();
		selenium.type("groupForm:GName", name);
		finishCreateGroup(supervisors, role, quota, maxSmsPerSend);
	}

	void createGroup(String[] pagsLocators, String[] supervisors, String role, int quota, int maxSmsPerSend) {
		startCreateGroup();
		for (String locator : pagsLocators) clickAndWait(locator);
		finishCreateGroup(supervisors, role, quota, maxSmsPerSend);
	}

	void finishCreateGroup(String[] supervisors, String role, int quota, int maxSmsPerSend) {
		selenium.click("groupForm:availableAccounts"); // choose first account
		for (String supervisor : supervisors) {
			selenium.type("groupForm:ldapUid", supervisor);
			clickAndWait("//input[@value='Look for']");
			clickAndWait("groupForm:personSearchList:0:add");
		}
		selenium.type("groupForm:quota", "" + quota);
		selenium.type("groupForm:dest", "" + maxSmsPerSend);
		selenium.select("groupForm:selectRoleMenu", "label=" + role);
		clickAndWait(inputLocatorByValue("Save"));
		forbidResponse("An error occurred", "group creation");
	}   
	void resetConsumption(String groupNumber) {
		navigationForm_gestionGroupes();
		clickAndWait("groupsForm:data:0:detailPage");
		clickAndWait(inputLocatorByValue("Reset consumption"));
	}
	void addQuota(String groupNumber, int quotaAdd) {
		navigationForm_gestionGroupes();
		clickAndWait("groupsForm:data:" + groupNumber + ":detailPage");
		selenium.type("groupForm:quotaAdd", "" + quotaAdd);
		clickAndWait(inputLocatorByValue("Save"));
	}

	void adhesion(String phoneNumber) {
		clickAndWait("navigationForm:adhesion");
		selenium.type("formMembership:phoneNumber", phoneNumber);
		selenium.check("formMembership:validGeneralConditions");
		clickAndWait(inputLocatorByValue("Save the member details"));
		expectedResponse("Data stored.", "adhesion");
	}

	void trueOrFail(boolean b, String errorMsg) {
		if (!b) throw new AssertionError(errorMsg);
	}

	void expectedResponse(String text, String context) {
		trueOrFail(selenium.isTextPresent(text), "response '" + text + "' not found while testing '" + context + "'");
	}

	void forbidResponse(String text, String context) {
		trueOrFail(!selenium.isTextPresent(text), "reponse '" + text + "' is invalid while testing '" + context + "'");
	}

	void expectedValue(String expectedValue, String value) {
		trueOrFail(expectedValue.equals(value), "expected " + expectedValue + ", got " + value);
	}

	void createServiceCP(String serviceName, String serviceKey) {
		navigationForm_gestionServicesCP();
		clickAndWait("createServiceForm:createPage");
		selenium.type("createModifyServiceForm:serviceName", serviceName);
		selenium.type("createModifyServiceForm:serviceKey", serviceKey);
		clickAndWait("createModifyServiceForm:save");
	}

	//*******************************************************************************
	private void expectedResponseTheMessageIsSending(String context) {
		forbidResponse("Web Service error", context);
		expectedResponse("The message is sending.", context);
	}

	private void basicQuotaChecks(String senderGroupNumber) {
		resetConsumption(senderGroupNumber);
		sendSMSByPhone(test_phone, "test");
		expectedResponse("Quota error for group", "quota has been reset");

		addQuota(senderGroupNumber, 1);
		sendSMSByPhone(test_phone, "test");
		expectedResponseTheMessageIsSending("quota has been raised");
		sendSMSByPhone(test_phone, "test");
		expectedResponse("Quota error for group", "quota is 0");
	}
	private void longMessageChecks(String senderGroupNumber) {
		assertTrue(biggestMessage.length() == maxLengthMessage);

		addQuota(senderGroupNumber, 2);
		sendSMSByPhone(test_phone, "x" + biggestMessage);
		expectedResponse("The message is too long", "too long message");
		sendSMSByPhone(test_phone, biggestMessage);
		expectedResponseTheMessageIsSending("max length message");
		sendSMSByPhone(user2smsutest_phone, biggestMessage.replace("0", "é"));
		expectedResponseTheMessageIsSending("max length message with accents");
	}
	private void messageTags(String senderGroupNumber) {
		addQuota(senderGroupNumber, 3);
		sendSMSByUser("smsutest", 1, "test <EXP_XX>");
		expectedResponse("The tag <EXP_XX> does not exist", "<EXP_*>");

		sendSMSByUser("smsutest", 1, "test EXP_NOM:<EXP_NOM> EXP_GROUPE_NOM:<EXP_GROUPE_NOM>");
		expectedResponseTheMessageIsSending("<EXP_*>");

		sendSMSByUser("smsutest", 1, "test DEST_NOM:<DEST_NOM> DEST_PRENOM:<DEST_PRENOM>");
		expectedResponseTheMessageIsSending("<DEST_*>");
	}
	void sendAdminTests() {
		ensureLogin("adminsmsutest");
		basicQuotaChecks("0");
		longMessageChecks("0");
		messageTags("0");
	}

	void checkAdhesion(String user, String[] services) {
		ensureLogin(user);
		clickAndWait("navigationForm:adhesion");
		trueOrFail(selenium.isChecked("formMembership:validGeneralConditions"), 
			   user + " validGeneralConditions");
		for (String service : services) {
			trueOrFail(selenium.isChecked("//*[@id='formMembership:validParticularConditions']//input[@value='" + testService + "']"),
				   user + " " + service);
		}
	}

	//*******************************************************************************
	
	void create_roleUserOnly() {
		mayDeleteRole("roleUserOnly");
		createRole("roleUserOnly", new int[] { 1 });
		forbidResponse("An error occurred", "roleUserOnly creation");
		createRole("roleUserOnly", new int[] { 1 });
		expectedResponse("An error occurred", "role already exists");
	}

	void create_roleUserAndGroup() {
		mayDeleteRole("roleUserAndGroup");
		createRole("roleUserAndGroup", new int[] { 1, 2, 12 });
		forbidResponse("An error occurred", "roleUserAndGroup creation");
	}
	
	void createGroup_user1smsutest_with_roleUserOnly() {
		mayDeleteGroup("User1 Smsutest");
		create_roleUserOnly();
		createGroup("user1smsutest", new String[] { "Admin Smsutest" }, "roleUserOnly", 999999, 1);
	}

	void createGroup_user2smsutest_with_roleUserAndGroup() {
		mayDeleteGroup("User2 Smsutest");
		create_roleUserAndGroup();
		createGroup("user2smsutest", new String[] { "Admin Smsutest" }, "roleUserAndGroup", 999999, 2);
	}
	
	void createGroup_pags() {
		startCreateGroup();
		createGroup(new String[] { "treeForm:" + openPagsTree, pagsLocator },
			    new String[] { "Admin Smsutest" }, "SUPER_ADMIN", 999999, 1);
	}

	void createAndTestUser1smsutest() {
		createGroup_user1smsutest_with_roleUserOnly();

		ensureLogin("user1smsutest");
		adhesion(user1smsutest_phone);
		clickAndWait("navigationForm:envoiSMS");
		for (String typ : selenium.getSelectOptions("formGeneral:selectTypeRecipient")) {
			if (typ.equals("Users"))
				; // ok
			else throw new AssertionError("user1smsutest should not be allowed to enter " + typ);
		}
		sendSMSByUser("smsutest", 2, "test moderation");
		expectedResponse("The message is sending for approval", "many recipients");
	}

	void searchSMS_checkFirstRow(String expectedState, String expectedContent) {

		clickAndWait("navigationForm:suiviEnvois");
		clickAndWait("searchSMS:send");

		// go to result last page:
		clickAndWaitIfPresent("messagesForm:data:_idJsp49");
		// sort to get new messages first:
		clickAndWait("//span[text()='Date sending']");

		expectedValue(expectedState, selenium.getTable("messagesForm:data:tbody_element.0.1"));

		clickAndWait("messagesForm:data:0:detailPage");
		expectedResponse(expectedContent, "detail page of sent message");
	}

	void cancelAllMessagesToApprove() {
		clickAndWait("navigationForm:approbationEnvoi");
		while (clickAndWaitIfPresent("approvalForm:data:0:cancel"));	
	}

	void createAndTestUser2smsutest(String service) {
		createGroup_user2smsutest_with_roleUserAndGroup();
		createGroup_pags();
		cancelAllMessagesToApprove();

		ensureLogin("user2smsutest");
		clickAndWait("navigationForm:envoiSMS");
		for (String typ : selenium.getSelectOptions("formGeneral:selectTypeRecipient")) {
			if (typ.equals("Users") || typ.equals("User Group"))
				; // ok
			else throw new AssertionError("user2smsutest should not be allowed to enter " + typ);
		}
		String testMsg = "testWithApproval" + (service == null ? "" : service) + "-" + Math.random();
		sendSMSByGroup(new String[] { "formGeneral:" + openPagsTree, pagsLocator }, testMsg, service);
		expectedResponse("The message is sending for approval", "group send with moderation");
		searchSMS_checkFirstRow("In approval", testMsg);

		ensureLogin("adminsmsutest");
		clickAndWait("navigationForm:approbationEnvoi");
		forbidResponse("An error occurred", "approbationEnvoi");
		clickAndWait("approvalForm:data:0:validate");
		expectedResponse("No messages to approve", "all messages validated");

		ensureLogin("user2smsutest");
		searchSMS_checkFirstRow("Sent", testMsg);
	}

	public void testSend() throws Exception {
		createServiceCP(testService, testService);
		// by default, user2smsutest should have CG + testService (cf test-ldap-users.ldif)
		checkAdhesion("user2smsutest", new String[] { testService });

		sendAdminTests();
		createAndTestUser1smsutest();
		createAndTestUser2smsutest(null);

		sendSMSByUser("smsutest", 1, "test service", testService);
		createAndTestUser2smsutest(testService);
	}
}
