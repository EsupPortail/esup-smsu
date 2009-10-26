<%@include file="../_include.jsp"%>
<%@ page
	import="org.esupportail.smsu.web.controllers.GroupsManagerController"%>
<%@ page import="org.esupportail.commons.utils.BeanUtils"%>
<%@ page
	import="org.esupportail.smsu.domain.beans.fonction.FonctionName"%>

<e:page stringsVar="msgs" menuItem="gestionGroupes"
	locale="#{sessionController.locale}"
	authorized="#{groupsManagerController.pageAuthorized}">
	<%@include file="../_navigation.jsp"%>
	<script type="text/javascript">
	function selectAccountFromAvailable(value) {
		document.getElementById("groupForm:AName").value = value;
	}
</script>
	<e:section value="#{msgs['GROUPE.UPDATE.TITLE']}" />

	<%
		GroupsManagerController groupsManagerController = (GroupsManagerController) BeanUtils
				.getBean("groupsManagerController");
	%>
	<!--<e:messages showDetail="true" showSummary="true"/>-->
	<e:form id="treeForm">
		<e:panelGrid columns="2">
			<e:outputLabel value="#{msgs['GROUPE.SELECTGROUP']}" for="tree" />
			<h:panelGroup rendered="true">
				<t:tree2 id="tree" value="#{groupsManagerController.treeModel}"
					var="node" varNodeToggler="t" clientSideToggle="false"
					showRootNode="true">
					<f:facet name="group">
						<h:panelGroup>
							<h:commandLink immediate="true"
								action="#{groupsManagerController.selectGroup}"
								actionListener="#{t.setNodeSelected}">
								<e:text value="#{node.description}" />
								<t:updateActionListener
									property="#{groupsManagerController.selectedGroupFromTree}"
									value="#{node.identifier}" />
							</h:commandLink>

						</h:panelGroup>
					</f:facet>
				</t:tree2>
			</h:panelGroup>
		</e:panelGrid>
	</e:form>
	<e:form id="groupForm">

		<e:panelGrid columns="4">

			<e:outputLabel value="#{msgs['GROUPE.NAME']}" for="GDisplayName"/>
			<h:panelGroup>
				<e:inputText id="GDisplayName"
					value="#{groupsManagerController.groupDisplayName}" disabled="true" maxlength="30" size="30" style="border:none;">
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			
			<e:outputLabel value="#{msgs['GROUPE.ID']}" for="GName"/>
			<h:panelGroup>
				<e:inputText id="GName"
					value="#{groupsManagerController.group.label}" maxlength="30">
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="GName" />
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>

			<e:outputLabel value="#{msgs['ACCOUNT.NAME']}" />
			<h:panelGroup>



				<e:inputText id="AName"
					value="#{groupsManagerController.account.label}" maxlength="30">
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="AName" />
			</h:panelGroup>


			<e:outputLabel for="availableAccounts"
				value="#{msgs['ACCOUNT.AVAILABLE']}"
				rendered="#{not empty groupsManagerController.availableAccounts}" />
			<h:selectOneListbox id="availableAccounts"
				rendered="#{not empty groupsManagerController.availableAccounts}"
				onclick="selectAccountFromAvailable(this.options[this.selectedIndex].text)">
				<f:selectItems value="#{groupsManagerController.availableAccounts}" />
			</h:selectOneListbox>






			<%
				if (groupsManagerController
						.isFieldAuthorized(FonctionName.FCTN_GESTION_ROLES_AFFECT
								.toString())) {
			%>
			<e:outputLabel value="#{msgs['ROLE.SELECT']}" for="selectRoleMenu" />
			<h:panelGroup>
				<e:selectOneMenu id="selectRoleMenu"
					value="#{groupsManagerController.role.id}">
					<f:selectItems
						value="#{groupsManagerController.selectRoleListItems}" />
				</e:selectOneMenu>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="selectRoleMenu" />
			</h:panelGroup>
			<%
				} else {
			%>
			<e:outputLabel value="#{msgs['ROLE.DISPLAY.SELECT']}"
				for="selectRoleMenu" />
			<e:inputText value="#{groupsManagerController.role.name}"
				disabled="true" style="background-color:#cecece;"/>
			<%
				}
			%>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<%
				if (groupsManagerController
						.isFieldAuthorized(FonctionName.FCTN_GESTIONS_RESPONSABLES
								.toString())) {
			%>
			<e:outputLabel value="#{msgs['GROUPE.SEARCH.SUPERVISORS.LDAP']}" />
			<h:panelGroup>
				<%--	<e:form id="formSearchUser">--%>
				<e:inputText id="ldapUid" value="#{usersSearchController.ldapUid}" />
				<e:commandButton value="#{msgs['GROUPE.SEARCH']}"
					action="#{usersSearchController.searchLdap}">
				</e:commandButton>
				<e:dataTable var="person"
					value="#{usersSearchController.ldapPersons}"
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
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<%
				}
			%>
			<e:outputLabel value="" />
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
						<%
							if (groupsManagerController
									.isFieldAuthorized(FonctionName.FCTN_GESTIONS_RESPONSABLES
											.toString())) {
						%>
						<e:commandButton value="#{msgs['GROUPE.DELETE']}"
							action="#{groupsManagerController.deletePerson}">
							<t:updateActionListener value="#{persons}"
								property="#{groupsManagerController.personToDelete}" />
						</e:commandButton>
						<%
							}
						%>
					</t:column>
				</e:dataTable>
				<%--	</e:form>--%>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>


			<e:outputLabel value="#{msgs['GROUPE.QUOTA.SMS']}" />
			<e:inputText id="quota"
				value="#{groupsManagerController.group.quotaSms}" maxlength="10"
				disabled="true" style="background-color:#cecece;"/>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<%
				if (groupsManagerController
						.isFieldAuthorized(FonctionName.FCTN_GESTION_QUOTAS
								.toString())) {
			%>
			<e:outputLabel value="#{msgs['GROUPE.QUOTA.SMS.ADD']}" />
			<h:panelGroup>
				<e:inputText id="quotaAdd"
					value="#{groupsManagerController.addQuotaSms}" maxlength="10">
					<t:validateRegExpr pattern='\d{0,10}' />
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="quotaAdd" />
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<%
				}
			%>

			<e:outputLabel value="#{msgs['GROUPE.QUOTA.ORDER']}" />
			<%
				if (groupsManagerController
						.isFieldAuthorized(FonctionName.FCTN_GESTION_QUOTAS
								.toString())) {
			%>
			<h:panelGroup>
				<e:inputText id="dest"
					value="#{groupsManagerController.group.quotaOrder}" maxlength="10">
					<t:validateRegExpr pattern='\d{0,10}' />
				</e:inputText>
				<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
				<e:message for="dest" />
			</h:panelGroup>
			<%
				} else {
			%>
			<e:inputText value="#{groupsManagerController.group.quotaOrder}"
				maxlength="10" disabled="true" style="background-color:#cecece;" />
			<%
				}
			%>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<e:outputLabel value="#{msgs['GROUPE.QUOTA.CONSUMEDSMS']}"
				for="consumedSms" />
			<e:inputText value="#{groupsManagerController.group.consumedSms}"
				maxlength="10" disabled="true" style="background-color:#cecece;" />
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
			<h:panelGroup>
				<f:verbatim> </f:verbatim>
			</h:panelGroup>
		</e:panelGrid>

		<f:verbatim>
			<br />
			<br />
		</f:verbatim>

		<h:panelGrid columns="3">

			<e:commandButton value="#{msgs['GROUPE.SAVE']}"
				action="#{groupsManagerController.update}" />

			<e:commandButton value="#{msgs['GROUPE.PAGE.RETOUR.LISTE']}"
				action="#{groupsManagerController.comeback}" />
				
			<e:commandButton value="#{msgs['GROUPE.RESET']}"
				action="#{groupsManagerController.resetConsumption}" />
		</h:panelGrid>
	</e:form>
</e:page>