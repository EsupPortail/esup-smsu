<%@include file="_include.jsp"%>

<e:page stringsVar="msgs" menuItem="gestionServicesCP"
	locale="#{sessionController.locale}"
	authorized="#{servicesSmsuController.pageAuthorized}" footer="">

	<%@include file="_navigation.jsp"%>
	<e:section value="#{msgs['SERVICE.LIST.TITLE']}" />

	<e:messages />

	<e:form id="listServicesForm">
		<e:dataTable
			rendered="#{not empty servicesSmsuController.paginator.visibleItems}"
			id="data" rowIndexVar="variable"
			value="#{servicesSmsuController.paginator.visibleItems}"
			var="uiService" cellpadding="5" cellspacing="3" width="60%">

			<f:facet name="header">
				<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['SERVICE.TEXT.TITLE']}">
								<f:param
									value="#{servicesSmsuController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{servicesSmsuController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{servicesSmsuController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{servicesSmsuController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{servicesSmsuController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not servicesSmsuController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{servicesSmsuController.paginator.gotoFirstPage}" 
									image="/media/icons/control-stop-180.png"/>
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{servicesSmsuController.paginator.gotoPreviousPage}" 
									image="/media/icons/control-180.png"/>
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList value="#{servicesSmsuController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == servicesSmsuController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != servicesSmsuController.paginator.currentPage}">
									<t:updateActionListener value="#{page}"
										property="#{servicesSmsuController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not servicesSmsuController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{servicesSmsuController.paginator.gotoNextPage}" 
									image="/media/icons/control.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{servicesSmsuController.paginator.gotoLastPage}"
									image="/media/icons/control-stop.png" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text value="#{msgs['SERVICE.TEXT.SERVICES_BY_PAGE']}" />
							<e:selectOneMenu
								onchange="javascript:{simulateLinkClick('listServicesForm:data:changeButton');}"
								value="#{servicesSmsuController.paginator.pageSize}">
								<f:selectItems
									value="#{servicesSmsuController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}"
								style="display:none;" id="changeButton"
								action="#{servicesSmsuController.paginator.forceReload}" />
						</h:panelGroup>
					</h:panelGrid>

				</h:panelGroup>
			</f:facet>

			<t:column sortable="true" defaultSorted="true">
				<f:facet name="header">
					<e:text value="#{msgs['SERVICE.NAME']}" />
				</f:facet>
				<e:text value="#{uiService.name}" />

			</t:column>

			<t:column>
				<f:facet name="header">
					<e:text value="#{msgs['SERVICE.KEY']}" />
				</f:facet>
				<e:text value="#{uiService.key}" />

			</t:column>

			<t:column width="20px;">
				<f:facet name="header">
					<e:text value="" />
				</f:facet>
				<e:commandButton id="detailPage" value="#{msgs['SERVICE.MODIFY']}"
					action="navigationModifyService"
					image="/media/icons/pencil.png"
					title="#{msgs['SERVICE.MODIFY']}" >
					<t:updateActionListener value="#{uiService}"
						property="#{servicesSmsuController.uiService}" />
				</e:commandButton>
			</t:column>

			<t:column width="20px;">
				<f:facet name="header">
					<e:text value="" />
				</f:facet>
				<e:commandButton id="delete" value="#{msgs['SERVICE.DELETE']}"
					action="#{servicesSmsuController.delete}"
					rendered="#{uiService.isDeletable}"
					image="/media/icons/minus-circle-frame.png" title="#{msgs['SERVICE.DELETE']}">
					<t:updateActionListener value="#{uiService}"
						property="#{servicesSmsuController.uiService}" />
				</e:commandButton>
			</t:column>
		</e:dataTable>

		<f:verbatim>
			<br />
			<br />
		</f:verbatim>
		<e:paragraph value="#{msgs['SERVICE.ERROR']}"
			rendered="#{empty servicesSmsuController.paginator.visibleItems}"
			style="color:#f00;" />

	</e:form>

	<e:form id="createServiceForm">
		<e:commandButton id="createPage" value="#{msgs['SERVICE.CREATE']}"
			action="#{servicesSmsuController.createServiceButton}" />
	</e:form>

</e:page>