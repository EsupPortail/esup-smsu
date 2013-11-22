<%@include file="_include.jsp"%>

<e:page stringsVar="msgs"
	locale="#{sessionController.locale}"
	authorized="#{servicesSmsuController.pageAuthorized}" footer="">

	<%@include file="_navigation.jsp"%>
	<e:section value="#{msgs['SERVICE.CREATE.TITLE']}"
		rendered="#{ empty servicesSmsuController.uiService.id}" />
	<e:section value="#{msgs['SERVICE.MODIFY.TITLE']}"
		rendered="#{not empty servicesSmsuController.uiService.id}" />

	<e:form id="createModifyServiceForm">

		<e:panelGrid columns="2">
			<e:outputLabel for="serviceName" value="#{msgs['SERVICE.NAME']}" />
			<t:panelGroup>
				<e:inputText id="serviceName"
					value="#{servicesSmsuController.uiService.name}" required="true"
					maxlength="16" />
				<e:message for="serviceName" />
			</t:panelGroup>

			<e:outputLabel for="serviceKey" value="#{msgs['SERVICE.KEY']}" />
			<t:panelGroup>
				<e:inputText id="serviceKey"
					value="#{servicesSmsuController.uiService.key}" required="true"
					maxlength="16" />
				<e:message for="serviceKey" />
			</t:panelGroup>
			<e:commandButton id="save" value="#{msgs['SERVICE.SAVE']}"
				action="#{servicesSmsuController.save}" />
			<e:commandButton id="cancel" value="#{msgs['SERVICE.CANCEL']}"
				immediate="true" action="navigationAdminService" />
		</e:panelGrid>

	</e:form>
</e:page>