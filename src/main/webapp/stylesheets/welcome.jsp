<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="welcome" locale="#{sessionController.locale}" footer="">
	<%@include file="_navigation.jsp"%>
	<e:messages/>
	<e:section value="#{msgs['WELCOME.TITLE']}" />
	<e:paragraph  escape="false" value="#{msgs['WELCOME.TEXT.TOP']}" />
	
	<h:panelGroup rendered="#{sessionController.currentUser != null}">
		<e:paragraph value="#{msgs['WELCOME.TEXT.AUTHENTICATED']}" />
	</h:panelGroup>
	<e:form id="welcomeForm" rendered="#{sessionController.currentUser == null}">
		<e:paragraph value="#{msgs['WELCOME.TEXT.UNAUTHENTICATED']}" />
		<e:panelGrid columns="2" >
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
	<e:form id="preferencesForm1" rendered="#{sessionController.currentUser != null}">

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
	
	<e:paragraph value="#{welcomeController.testConnexion}" rendered="#{welcomeController.isConnexionTested}" />

<script type="text/javascript">	
	hideButton("welcomeForm:localeChangeButton");
	hideButton("preferencesForm1:localeChangeButton");		
</script>
<% /* @include file="_debug.jsp" */ %>
</e:page>
