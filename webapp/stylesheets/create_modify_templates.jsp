<%@include file="_include.jsp"%>

<e:page stringsVar="msgs" locale="#{sessionController.locale}"
	authorized="#{templateManagerController.pageAuthorized}">

	<%@include file="_navigation.jsp"%>
	<e:section value="#{msgs['TEMPLATE.CREATE.TITLE']}"
		rendered="#{ empty templateManagerController.uiTemplate.id}" />
	<e:section value="#{msgs['TEMPLATE.MODIFY.TITLE']}"
		rendered="#{not empty templateManagerController.uiTemplate.id}" />

	<e:form id="createModifyTemplateForm">

		<e:panelGrid columns="2">
			<e:outputLabel for="templateLabel" value="#{msgs['TEMPLATE.LABEL']}" />
			<t:panelGroup>
				<e:inputText id="templateLabel"
					value="#{templateManagerController.uiTemplate.label}" required="true"
					maxlength="32" />
				<e:message for="templateLabel" />
			</t:panelGroup>

			<e:outputLabel for="templateHeading"
				value="#{msgs['TEMPLATE.HEADING']}" />
			<t:panelGroup>
				<e:inputText id="templateHeading"
					value="#{templateManagerController.uiTemplate.heading}"
					maxlength="50" />
				<e:message for="templateHeading" />
			</t:panelGroup>

			<e:outputLabel for="templateBody" value="#{msgs['TEMPLATE.BODY']}" />
			<t:panelGroup>
				<e:inputTextarea id="templateBody"
					binding="#{templateManagerController.templateBody}"
					style="width:319px;" />
				<e:message for="templateBody" />
			</t:panelGroup>

			<e:outputLabel for="templateSignature"
				value="#{msgs['TEMPLATE.SIGNATURE']}" />
			<t:panelGroup>
				<e:inputText id="templateSignature"
					value="#{templateManagerController.uiTemplate.signature}"
					maxlength="50" />
				<e:message for="templateSignature" />
			</t:panelGroup>

			<e:commandButton id="save" value="#{msgs['TEMPLATE.SAVE']}"
				action="#{templateManagerController.save}" />
			<e:commandButton id="cancel" value="#{msgs['TEMPLATE.CANCEL']}"
				immediate="true" action="navigationManageTemplates" />
		</e:panelGrid>

	</e:form>
</e:page>