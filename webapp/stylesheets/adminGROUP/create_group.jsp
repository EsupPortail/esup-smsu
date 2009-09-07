<%@include file="../_include.jsp"%>
<e:page stringsVar="msgs" menuItem="gestionGroupes" locale="#{sessionController.locale}" 
authorized="#{groupsManagerController.pageAuthorized}">
	<%@include file="../_navigation.jsp"%>

	<e:section value="#{msgs['GROUPE.NEW.TITLE']}" />

	<e:form id="groupForm">
		<e:panelGrid columns="2" >

			<e:outputLabel 
				value="#{msgs['GROUPE.NAME']}" />
			<h:panelGroup>
				<e:inputText id="GName"
					value="#{groupsManagerController.group.label}" maxlength="30"
					>
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="GName" />
			</h:panelGroup>
			
			<e:outputLabel 
				value="#{msgs['ACCOUNT.NAME']}" />
			<h:panelGroup>
				<e:inputText id="AName"
					value="#{groupsManagerController.account.label}" maxlength="30"
					>
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="AName" />
			</h:panelGroup>
			
			<e:outputLabel value="#{msgs['ROLE.SELECT']}" for="selectRoleMenu" />
			<h:panelGroup>
				<e:selectOneMenu id="selectRoleMenu" value="#{groupsManagerController.role.id}">
					<f:selectItems
						value="#{groupsManagerController.selectRoleListItems}" />
				</e:selectOneMenu>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="selectRoleMenu"/>	
			</h:panelGroup>
			
			<e:outputLabel 
				value="#{msgs['GROUPE.SEARCH.SUPERVISORS.LDAP']}" />
			<h:panelGroup>
					<%--	<e:form id="formSearchUser">--%>
					<e:inputText id="ldapUid" value="#{usersSearchController.ldapUid}" />
					<e:commandButton value="#{msgs['GROUPE.SEARCH']}"
						action="#{usersSearchController.searchLdap}">
					</e:commandButton>
					<e:dataTable var="person" value="#{usersSearchController.ldapPersons}"
						rendered="#{not empty usersSearchController.ldapPersons}"
						id="personSearchList" rowIndexVar="variable">
						<t:column>
							<f:facet name="header">
								<e:text value="#{msgs['GROUPE.SEARCH']}" />
							</f:facet>
							<e:text value="#{person.displayName}" />
						</t:column>
						<t:column>
							<e:commandButton id="add" value="Ajouter" 
								action="#{groupsManagerController.selectPerson}">
								<t:updateActionListener value="#{person}"
									property="#{groupsManagerController.selectedPerson}" />
							</e:commandButton>
						</t:column>
					</e:dataTable>
					<%--	</e:form>--%>
			</h:panelGroup>
			
			<e:outputLabel 
				value="" />
			<h:panelGroup>
						<%--	<e:form id="formpersons">--%>
						<e:dataTable id="persons" var="persons"
							value="#{groupsManagerController.persons}">
							<t:column>
								<f:facet name="header">
									<e:text value="#{msgs['GROUPE.SUPERVISORS']}" />
								</f:facet>
								<e:text value="#{persons.displayName}" />
							</t:column>
							<t:column>
								<e:commandButton value="#{msgs['GROUPE.DELETE']}"
									action="#{groupsManagerController.deletePerson}">
									<t:updateActionListener value="#{persons}"
										property="#{groupsManagerController.personToDelete}" />
								</e:commandButton>
							</t:column>
						</e:dataTable>
						<%--	</e:form>--%>
			</h:panelGroup>
			
			<e:outputLabel 
				value="#{msgs['GROUPE.QUOTA.SMS']}" />
			<h:panelGroup>	
				<e:inputText id="quota" value="#{groupsManagerController.group.quotaSms}" maxlength="10">
				<t:validateRegExpr pattern='\d{0,10}' />
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="quota"/>	
			</h:panelGroup>
		
			
			<e:outputLabel 
				value="#{msgs['GROUPE.QUOTA.ORDER']}" />
			<h:panelGroup>
			<e:inputText id="dest" value="#{groupsManagerController.group.quotaOrder}" maxlength="10">
			<t:validateRegExpr pattern='\d{0,10}' />
			</e:inputText>
			<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
			<e:message for="dest"/>	

			</h:panelGroup>
			
			
		</e:panelGrid>
	
		<f:verbatim><br/><br/></f:verbatim>
		<h:panelGrid columns="2" >
		<e:commandButton value="#{msgs['GROUPE.SAVE']}" action="#{groupsManagerController.save}" />
		<e:commandButton value="#{msgs['GROUPE.PAGE.RETOUR.LISTE']}"	action="navigationAdminGroups" immediate="true"/>			
		</h:panelGrid>
		
	</e:form>
	
</e:page>