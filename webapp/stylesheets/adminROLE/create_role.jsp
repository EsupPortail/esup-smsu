<%@include file="../_include.jsp"%>
<e:page stringsVar="msgs" menuItem="gestionRoles" locale="#{sessionController.locale}" 
authorized="#{rolesController.pageAuthorized}">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['ROLE.NEW.TITLE']}" />

	<e:form>
		<e:panelGrid columns="2" >

			<e:outputLabel 
				value="#{msgs['ROLE.LABEL.NAME']}" for="Name" />
			<h:panelGroup>
				<e:inputText id="Name"
					value="#{rolesController.role.name}" maxlength="30"
					required="true">
				</e:inputText>
				<e:message for="Name" />
			</h:panelGroup>

			<e:outputLabel for="validFonctions"
				value="#{msgs['ROLE.LIST.FONCTIONS']}" />
			<h:panelGroup>
				<e:selectManyCheckbox id="validFonctions"
			   		value="#{rolesController.selectedValues}"
			    	layout="pageDirection">
 					<t:selectItems value="#{rolesController.allBundleFonctions}" var="fct" itemValue="#{fct.id}" itemLabel="#{fct.name}"/>
				</e:selectManyCheckbox>
			</h:panelGroup>
			
		</e:panelGrid>
	
		<f:verbatim><br/><br/></f:verbatim>
		<h:panelGrid columns="2" >
		<e:commandButton value="#{msgs['ROLE.SAVE']}" action="#{rolesController.save}" />
		<e:commandButton value="#{msgs['ROLE.PAGE.RETOUR.LISTE']}"	action="navigationAdminRoles" immediate="true"/>			
		</h:panelGrid>
		
	</e:form>
	
</e:page>