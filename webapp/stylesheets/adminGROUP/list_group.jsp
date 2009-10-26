<%@include file="../_include.jsp"%>
<e:page stringsVar="msgs" menuItem="gestionGroupes" locale="#{sessionController.locale}" 
authorized="#{groupsManagerController.pageAuthorized}">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['GROUPE.LIST.TITLE']}" />
	
	<e:form id="groupsForm">
				<e:dataTable rendered="#{not empty groupsManagerController.paginator.visibleItems}" 
				    id="data" rowIndexVar="variable"
					value="#{groupsManagerController.paginator.visibleItems}" var="group"
					cellpadding="5" cellspacing="3" width="60%"  >
					
					<f:facet name="header">
					<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['GROUPE.TEXT.TITLE']}">
								<f:param
									value="#{groupsManagerController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{groupsManagerController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{groupsManagerController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{groupsManagerController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{groupsManagerController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not groupsManagerController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{groupsManagerController.paginator.gotoFirstPage}" 
									image="/media/icons/control-stop-180.png"/>
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{groupsManagerController.paginator.gotoPreviousPage}"
									image="/media/icons/control-180.png"/>
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList
								value="#{groupsManagerController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == groupsManagerController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != groupsManagerController.paginator.currentPage}" >
									<t:updateActionListener value="#{page}"
										property="#{groupsManagerController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not groupsManagerController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{groupsManagerController.paginator.gotoNextPage}" 
									image="/media/icons/control.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{groupsManagerController.paginator.gotoLastPage}" 
									image="/media/icons/control-stop.png" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text
								value="#{msgs['GROUPE.TEXT.GROUPE_BY_PAGE']}" />
							<e:selectOneMenu onchange="javascript:{simulateLinkClick('groupsForm:data:changeButton');}"
								value="#{groupsManagerController.paginator.pageSize}">
								<f:selectItems
									value="#{groupsManagerController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" style="display:none;"
								id="changeButton" action="#{groupsManagerController.paginator.forceReload}"/>
						</h:panelGroup>
					</h:panelGrid>
									
				</h:panelGroup>
				</f:facet>
					
					<t:column sortable="true" defaultSorted="true" >
						<f:facet name="header" >
							<e:text
								value="#{msgs['GROUPE.NAME']}" />
						</f:facet>
						<e:text value="#{group.displayName}" />
						
					</t:column>
					
					<t:column width="150px;">
						<f:facet name="header">
							<e:text
								value="#{msgs['GROUPE.DETAIL']}" />
						</f:facet>
						<e:commandButton id="detailPage" value="#{msgs['GROUPE.DISPLAY']}"	action="#{groupsManagerController.display}"
						image="/media/icons/pencil.png" title="#{msgs['GROUPE.DISPLAY']}" >
							<t:updateActionListener value="#{group.customizedGroup}"
							property="#{groupsManagerController.group}" />
						</e:commandButton>
					</t:column>
						
					<t:column width="20px;">
					<f:facet name="header">
						<e:text
							value="" />
					</f:facet>
					<e:commandButton id="delete" value="#{msgs['GROUPE.DELETE']}"	action="#{groupsManagerController.delete}"
					image="/media/icons/minus-circle-frame.png" title="#{msgs['GROUPE.DELETE']}" >
						<t:updateActionListener value="#{group}"
						property="#{groupsManagerController.group}" />
					</e:commandButton>
					</t:column>
				
				</e:dataTable>
			
			<f:verbatim><br/><br/></f:verbatim>
			<e:paragraph
				value="#{msgs['GROUPE.ERROR']}"
				rendered="#{empty groupsManagerController.paginator.visibleItems}" style="color:#f00;"/>
		</e:form>

		<e:form >	
			<e:commandButton id="createPage" value="#{msgs['GROUPE.CREATE']}"	action="#{groupsManagerController.create}"
			rendered="#{groupsManagerController.isShowCreateButton}"/>
		</e:form>
		
		
</e:page>
