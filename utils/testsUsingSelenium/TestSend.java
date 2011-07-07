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

	String openPagsTree = "tree:0:t2";
	String pagsLocator = "//span[text()='Groupes LDAP']";

	String timeout = "30000";

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
		clickAndWait("navigationForm:envoiSMS");

		selenium.select("formGeneral:selectTypeRecipient", "label=Users");
		//no waitForPageToLoad since "Users" is the default

		selenium.type("formGeneral:ldapUid", user);
		clickAndWait(inputLocatorByValue("Search"));

		for (int c = 0; c < nbMatches; c++)
			clickAndWait("formGeneral:recipientSearchList:" + c + ":add");

		selenium.type("formGeneral:SMSbody", body);
		clickAndWait("formGeneral:sendSMSButton");
	}

	void sendSMSByGroup(String[] pagsLocators, String body) {
		clickAndWait("navigationForm:envoiSMS");
		selectAndWait("formGeneral:selectTypeRecipient", "label=User Group");
		for (String locator : pagsLocators) clickAndWait(locator);
		selenium.type("formGeneral:SMSbody", body);
		clickAndWait("formGeneral:sendSMSButton");
	}

	void deleteRole(String roleNumber) {
		clickAndWait("navigationForm:gestionRoles");
		clickAndWait("rolesForm:data:" + roleNumber + ":delete");
	}
	void mayDeleteRole(String roleNumber) {
		try { deleteRole(roleNumber); } catch (SeleniumException e) { }
	}
	void createRole(String name, int[] fonctions) {
		clickAndWait("navigationForm:gestionRoles");
		clickAndWait(inputLocatorByValue("Create role"));
		selenium.type("//input[contains(@name,':Name')]", name);
		for (int fonction : fonctions)
			selenium.check("//input[contains(@name,':validFonctions') and @value='" + fonction + "']");
		clickAndWait(inputLocatorByValue("Save"));
	}

	void deleteGroup(String groupNumber) {
		clickAndWait("navigationForm:gestionGroupes");
		clickAndWait("groupsForm:data:" + groupNumber + ":delete");
	}
	void mayDeleteGroup(String groupNumber) {
		try { deleteGroup(groupNumber); } catch (SeleniumException e) { }
	}

	void startCreateGroup() {
		clickAndWait("navigationForm:gestionGroupes");
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
		clickAndWait("navigationForm:gestionGroupes");
		clickAndWait("groupsForm:data:0:detailPage");
		clickAndWait(inputLocatorByValue("Reset consumption"));
	}
	void addQuota(String groupNumber, int quotaAdd) {
		clickAndWait("navigationForm:gestionGroupes");
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

	//*******************************************************************************
	private void expectedResponseTheMessageIsSending(String context) {
		forbidResponse("Web Service error", context);
		expectedResponse("The message is sending.", context);
	}

	private void basicQuotaChecks(String senderGroupNumber) {
		resetConsumption(senderGroupNumber);
		sendSMSByPhone(test_phone, "test");
		expectedResponse("Group quota error", "quota has been reset");

		addQuota(senderGroupNumber, 1);
		sendSMSByPhone(test_phone, "test");
		expectedResponseTheMessageIsSending("quota has been raised");
		sendSMSByPhone(test_phone, "test");
		expectedResponse("Group quota error", "quota is 0");
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
		login("adminsmsutest");
		basicQuotaChecks("0");
		longMessageChecks("0");
		messageTags("0");
	}

	//*******************************************************************************
	
	void create_roleUserOnly() {
		mayDeleteRole("0");
		createRole("roleUserOnly", new int[] { 1 });
		forbidResponse("An error occurred", "roleUserOnly creation");
		createRole("roleUserOnly", new int[] { 1 });
		expectedResponse("An error occurred", "role already exists");
	}

	void create_roleUserAndGroup() {
		mayDeleteRole("0");
		createRole("roleUserAndGroup", new int[] { 1, 2, 12 });
		forbidResponse("An error occurred", "roleUserAndGroup creation");
	}
	
	void createGroup_user1smsutest_with_roleUserOnly() {
		mayDeleteGroup("1");
		create_roleUserOnly();
		createGroup("user1smsutest", new String[] { "Admin Smsutest" }, "roleUserOnly", 999999, 1);
	}

	void createGroup_user2smsutest_with_roleUserAndGroup() {
		mayDeleteGroup("1");
		create_roleUserAndGroup();
		createGroup("user2smsutest", new String[] { "Admin Smsutest" }, "roleUserAndGroup", 999999, 2);
	}
	
	void createGroup_pags() {
		startCreateGroup();
		createGroup(new String[] { "treeForm:" + openPagsTree, pagsLocator },
			    new String[] { "Admin Smsutest" }, "SUPER_ADMIN", 999999, 1);
	}

	void createAndTestUser1smsutest() {
		login("adminsmsutest");
		createGroup_user1smsutest_with_roleUserOnly();

		login("user1smsutest");
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

	void createAndTestUser2smsutest() {
		login("adminsmsutest");
		createGroup_user2smsutest_with_roleUserAndGroup();
		createGroup_pags();
		cancelAllMessagesToApprove();

		login("user2smsutest");
		clickAndWait("navigationForm:envoiSMS");
		for (String typ : selenium.getSelectOptions("formGeneral:selectTypeRecipient")) {
			if (typ.equals("Users") || typ.equals("User Group"))
				; // ok
			else throw new AssertionError("user2smsutest should not be allowed to enter " + typ);
		}
		String testMsg = "testWithApproval-" + Math.random();
		sendSMSByGroup(new String[] { "formGeneral:" + openPagsTree, pagsLocator }, testMsg);
		expectedResponse("The message is sending for approval", "group send with moderation");
		searchSMS_checkFirstRow("In approval", testMsg);

		login("adminsmsutest");
		clickAndWait("navigationForm:approbationEnvoi");
		forbidResponse("An error occurred", "approbationEnvoi");
		clickAndWait("approvalForm:data:0:validate");
		expectedResponse("No messages to approve", "all messages validated");

		login("user2smsutest");
		searchSMS_checkFirstRow("Sent", testMsg);
	}

	public void testSend() throws Exception {
		sendAdminTests();
		createAndTestUser1smsutest();
		createAndTestUser2smsutest();
	}
}
