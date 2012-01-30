<%@include file="../_include.jsp"%>

<e:page stringsVar="msgs" menuItem="gestionRoles" locale="#{sessionController.locale}" 
authorized="#{rolesController.pageAuthorized}" footer="">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['ROLE.UPDATE.TITLE']}" />

	<%--<e:messages showDetail="true" showSummary="true"/>--%>
	<e:form id="update">
	        
		<e:panelGrid columns="2" >

		<e:outputLabel for="Name"
				value="#{msgs['ROLE.LABEL.NAME']}" />
			<h:panelGroup>
				<e:inputText id="Name"
					value="#{rolesController.role.name}" maxlength="30"
					required="true" disabled="#{!rolesController.role.isUpdateable}">
				</e:inputText>
			
			</h:panelGroup>			

			<e:outputLabel for="validFonctions"
				value="#{msgs['ROLE.LIST.FONCTIONS']}" />
			<h:panelGroup>
				<e:selectManyCheckbox id="validFonctions"
			   		value="#{rolesController.selectedValues}"
			    	layout="pageDirection" disabled="#{!rolesController.role.isUpdateable}">
 					<t:selectItems value="#{rolesController.allBundleFonctions}" var="fct" itemValue="#{fct.id}" itemLabel="#{fct.name}" />
 				</e:selectManyCheckbox>
			</h:panelGroup>
			
		</e:panelGrid>
		<f:verbatim><br/><br/></f:verbatim>
		<h:panelGrid columns="2" >
			
			<e:commandButton value="Modifier" action="#{rolesController.update}" disabled="#{!rolesController.role.isUpdateable}"/>
		
			<e:commandButton value="#{msgs['ROLE.PAGE.RETOUR.LISTE']}"	action="navigationAdminRoles" />	
		</h:panelGrid>
	</e:form>
</e:page>