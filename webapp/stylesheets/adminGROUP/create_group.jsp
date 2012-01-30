<%@include file="../_include.jsp"%>

<e:page stringsVar="msgs" menuItem="gestionGroupes"
	locale="#{sessionController.locale}"
	authorized="#{groupsManagerController.pageAuthorized}">

	<%@include file="../_navigation.jsp"%>

	<e:section value="#{msgs['GROUPE.NEW.TITLE']}" />

	<e:form id="groupForm">

	        <% boolean create_group = true; %>
                <%@include file="./_detail_or_create_group.jsp"%>

		<e:panelGrid columns="2">
			<e:commandButton value="#{msgs['GROUPE.SAVE']}"
				action="#{groupsManagerController.save}" />
			<e:commandButton value="#{msgs['GROUPE.PAGE.RETOUR.LISTE']}"
				action="#{groupsManagerController.comeback}" />
		</e:panelGrid>
	</e:form>
</e:page>
