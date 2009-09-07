<%@include file="../_include.jsp"%>
<e:page stringsVar="msgs" menuItem="gestionRoles" locale="#{sessionController.locale}" 
authorized="#{rolesController.pageAuthorized}">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['ROLE.LIST.TITLE']}" />
	
	<e:form id="rolesForm">
				<e:dataTable rendered="#{not empty rolesController.paginator.visibleItems}" 
				    id="data" rowIndexVar="variable"
					value="#{rolesController.paginator.visibleItems}" var="role"
					cellpadding="5" cellspacing="3" width="60%"  >
					
					<f:facet name="header">
					<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['ROLE.TEXT.TITLE']}">
								<f:param
									value="#{rolesController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{rolesController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{rolesController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{rolesController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{rolesController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not rolesController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{rolesController.paginator.gotoFirstPage}" 
									image="/media/icons/control-stop-180.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{rolesController.paginator.gotoPreviousPage}" 
									image="/media/icons/control-180.png" />
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList
								value="#{rolesController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == rolesController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != rolesController.paginator.currentPage}" >
									<t:updateActionListener value="#{page}"
										property="#{rolesController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not rolesController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{rolesController.paginator.gotoNextPage}" 
									image="/media/icons/control.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{rolesController.paginator.gotoLastPage}" 
									image="/media/icons/control-stop.png" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text
								value="#{msgs['ROLE.TEXT.ROLE_BY_PAGE']}" />
							<e:selectOneMenu onchange="javascript:{simulateLinkClick('rolesForm:data:changeButton');}"
								value="#{rolesController.paginator.pageSize}">
								<f:selectItems
									value="#{rolesController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" style="display:none;"
								id="changeButton" action="#{rolesController.paginator.forceReload}"/>
						</h:panelGroup>
					</h:panelGrid>
									
				</h:panelGroup>
				</f:facet>
					
					<t:column sortable="true" defaultSorted="true" >
						<f:facet name="header" >
							<e:text
								value="#{msgs['ROLE.NAME']}" />
						</f:facet>
						<e:text value="#{role.name}" />
						
					</t:column>
					
					<t:column width="150px;">
						<f:facet name="header">
							<e:text
								value="#{msgs['ROLE.DETAIL']}" />
						</f:facet>
						<e:commandButton id="detailPage" value="#{msgs['ROLE.DISPLAY']}"	action="#{rolesController.display}"
						image="/media/icons/pencil.png" title="#{msgs['ROLE.DISPLAY']}" >
							<t:updateActionListener value="#{role}"
							property="#{rolesController.role}" />
						</e:commandButton>
					</t:column>
						
					<t:column width="20px;">
					<f:facet name="header">
						<e:text
							value="" />
					</f:facet>
					<e:commandButton id="delete" value="#{msgs['ROLE.DELETE']}"	action="#{rolesController.delete}" rendered="#{role.isDeletable}"
					image="/media/icons/minus-circle-frame.png" title="#{msgs['ROLE.DELETE']}" >
						<t:updateActionListener value="#{role}"
						property="#{rolesController.role}" />
					</e:commandButton>
					</t:column>
				
				</e:dataTable>
			
			<f:verbatim><br/><br/></f:verbatim>
			<e:paragraph
				value="#{msgs['ROLE.ERROR']}"
				rendered="#{empty rolesController.paginator.visibleItems}" style="color:#f00;"/>
		</e:form>

		<e:form >	
			<e:commandButton id="createPage" value="#{msgs['ROLE.CREATE']}"	action="#{rolesController.create}"/>
		</e:form>
		
</e:page>
