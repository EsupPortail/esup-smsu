<%@include file="../_include.jsp"%>

<e:outputLabel for="selectTypeRecipient"
	value="#{msgs['SENDSMS.LABEL.RECIPIENTTYPE']}"
	for="selectTypeRecipient" />

<e:selectOneMenu id="selectTypeRecipient" onchange="submit();"
	binding="#{smsRecipientController.selectTypeDest}"
	valueChangeListener="#{smsRecipientController.modifTypeDest}"
	value="#{smsRecipientController.recipientType}">
	<f:selectItems value="#{smsRecipientController.destTypeOptions}" />
</e:selectOneMenu>


<t:panelGroup binding="#{smsRecipientController.groupPanelGrid}"
	colspan="2">
	<%--	<e:form id="formAddGroup">--%>
	<e:outputLabel value="#{msgs['SENDSMS.LABEL.SELECTRECIPIENTGROUP']}"
		for="tree" />
	<f:verbatim>
		<br>
	</f:verbatim>

	<%--	</e:form>--%>

	<h:panelGroup rendered="true">
		<t:tree2 id="tree" value="#{smsRecipientController.treeModel}"
			var="node" varNodeToggler="t" clientSideToggle="false"
			showRootNode="true">
			<f:facet name="group">
				<h:panelGroup>
					<h:commandLink immediate="true"
						action="#{smsRecipientController.addGroupRecipient}"
						actionListener="#{t.setNodeSelected}">
						<e:text value="#{node.description}" />
						<t:updateActionListener
							property="#{smsRecipientController.selectedRecipientGroup}"
							value="#{node.description}" />
					</h:commandLink>

				</h:panelGroup>
			</f:facet>
		</t:tree2>
	</h:panelGroup>
</t:panelGroup>

<t:panelGroup binding="#{smsRecipientController.ldapSearchPanelGrid}"
	colspan="2">
	<e:inputText id="ldapUid" value="#{usersSearchController.ldapUid}" />
	<e:commandButton value="#{msgs['SENDSMS.LABEL.SEARCH']}"
		action="#{usersSearchController.searchUser}">
		<t:updateActionListener value="#{sendSMSController.selectedService}"
			property="#{usersSearchController.service}" />
	</e:commandButton>
	<f:verbatim>
		<br>
	</f:verbatim>
	<e:message for="ldapUid" />
</t:panelGroup>

<t:panelGroup binding="#{smsRecipientController.ldapRequestPanelGrid}"
	colspan="2">
	<e:inputText id="ldapFilter"
		value="#{usersSearchController.ldapFilter}" />

	<e:commandButton value="#{msgs['SENDSMS.LABEL.SEARCH']}"
		action="#{usersSearchController.searchLdapWithFilter}" />
	<f:verbatim>
		<br>
	</f:verbatim>
	<e:message for="ldapFilter" />
</t:panelGroup>


<t:panelGroup colspan="2">
	<e:dataTable var="recipient" value="#{usersSearchController.ldapUsers}"
		rendered="#{not empty usersSearchController.ldapUsers}"
		id="recipientSearchList" rowIndexVar="variable">
		<t:column>
			<f:facet name="header">
				<e:text value="#{msgs['SENDSMS.LABEL.SEARCHRESULT']}" />
			</f:facet>
			<e:text value="#{recipient.displayName}" />
		</t:column>
		<t:column>
			<e:commandButton id="add" value="#{msgs['SENDSMS.LABEL.ADD']}"
				action="#{smsRecipientController.selectRecipient}">
				<t:updateActionListener value="#{recipient}"
					property="#{smsRecipientController.selectedRecipient}" />
			</e:commandButton>
		</t:column>
	</e:dataTable>
</t:panelGroup>

<t:panelGroup colspan="2">
	<e:dataTable var="listRecipient"
		value="#{usersSearchController.ldapRequestUsers}"
		rendered="#{not empty usersSearchController.ldapRequestUsers}"
		id="listRecipientSearchList" rowIndexVar="variable">
		<t:column>
			<f:facet name="header">
				<e:text value="#{msgs['SENDSMS.LABEL.SEARCHRESULT']}" />
			</f:facet>
			<e:text value="#{listRecipient.displayName}" />
		</t:column>
	</e:dataTable>
	<e:panelGrid columns="2"
		rendered="#{not empty usersSearchController.ldapRequestUsers}">
		<e:outputLabel
			value="#{msgs['SENDSMS.LABEL.NBVALIDRECIPIENT']} : #{usersSearchController.nbAvailableUsersInTheList}"
			for="addList" />
		<e:commandButton id="addList"
			value="#{msgs['SENDSMS.LABEL.VALIDATE']}"
			action="#{smsRecipientController.addRecipientList}">
			<t:updateActionListener
				value="#{usersSearchController.ldapValidUsers}"
				property="#{smsRecipientController.recipientList}" />
		</e:commandButton>
	</e:panelGrid>
</t:panelGroup>

<t:panelGroup
	binding="#{smsRecipientController.phoneNumberListPanelGrid}"
	colspan="2">
	<%--	<e:form id="formAddPhoneNumber">--%>
	<e:inputText id="phoneNumber"
		value="#{smsRecipientController.phoneNumberToAdd}" maxlength="10" />

	<e:commandButton value="Ajouter"
		action="#{smsRecipientController.addPhoneNumber}" />
	<%--	</e:form>--%>
</t:panelGroup>

<t:panelGroup colspan="2">
	<%--	<e:form id="formRecipients">--%>
	<e:dataTable id="recipients" var="recipients"
		value="#{smsRecipientController.recipients}">
		<t:column>
			<f:facet name="header">
				<e:text value="#{msgs['SENDSMS.LABEL.RECIPIENTS']}" />
			</f:facet>
			<e:text value="#{recipients.displayName}" />
		</t:column>
		<t:column>
			<e:commandButton value="#{msgs['SENDSMS.LABEL.DELETERECIPIENT']}"
				action="#{smsRecipientController.deleteRecipient}">
				<t:updateActionListener value="#{recipients}"
					property="#{smsRecipientController.recipientToDelete}" />
			</e:commandButton>
		</t:column>
	</e:dataTable>
	<%--	</e:form>--%>
</t:panelGroup>
