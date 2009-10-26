<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="preferences" locale="#{sessionController.locale}" authorized="#{preferencesController.pageAuthorized}">
	<%@include file="_navigation.jsp"%>

 	<e:form id="preferencesForm">
		<e:section value="#{msgs['PREFERENCES.TITLE']}" />

		<e:messages />

		<e:panelGrid columns="2">
			<e:outputLabel for="locale" 
				value="#{msgs['PREFERENCES.TEXT.LANGUAGE']}" />
			<h:panelGroup>
				<e:selectOneMenu id="locale" onchange="submit();"
					value="#{preferencesController.locale}" converter="#{localeConverter}" >
					<f:selectItems value="#{preferencesController.localeItems}" />
				</e:selectOneMenu>
				<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" id="localeChangeButton" />
			</h:panelGroup>
		</e:panelGrid>
	</e:form>
	<script type="text/javascript">	
		hideButton("preferencesForm:localeChangeButton");		
	</script>
</e:page>
