<%@include file="../_include.jsp"%>

<e:page stringsVar="msgs" menuItem="gestionGroupes"
	locale="#{sessionController.locale}"
	authorized="#{groupsManagerController.pageAuthorized}">

	<%@include file="../_navigation.jsp"%>

	<e:section value="#{msgs['GROUPE.UPDATE.TITLE']}" />

	<e:form id="groupForm">

	        <% boolean create_group = false; %>
                <%@include file="./_detail_or_create_group.jsp"%>

		<e:panelGrid columns="3">

			<e:commandButton value="#{msgs['GROUPE.SAVE']}"
				action="#{groupsManagerController.update}" />

			<e:commandButton value="#{msgs['GROUPE.PAGE.RETOUR.LISTE']}"
				action="#{groupsManagerController.comeback}" />
				
			<e:commandButton value="#{msgs['GROUPE.RESET']}"
				action="#{groupsManagerController.resetConsumption}" />
		</e:panelGrid>
	</e:form>
</e:page>
