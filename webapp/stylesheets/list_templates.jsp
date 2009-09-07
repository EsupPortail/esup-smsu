<%@include file="_include.jsp"%>

<e:page stringsVar="msgs" menuItem="templates"
	locale="#{sessionController.locale}"
	authorized="#{templateManagerController.pageAuthorized}">

	<%@include file="_navigation.jsp"%>
	<e:section value="#{msgs['TEMPLATE.LIST.TITLE']}" />

	<e:messages />

	<e:form id="listTemplatesForm">
		<e:dataTable
			rendered="#{not empty templateManagerController.paginator.visibleItems}"
			id="data" rowIndexVar="variable"
			value="#{templateManagerController.paginator.visibleItems}"
			var="uiTemplate" cellpadding="5" cellspacing="3" width="60%">

			<f:facet name="header">
				<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['TEMPLATE.TEXT.TITLE']}">
								<f:param
									value="#{templateManagerController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{templateManagerController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{templateManagerController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{templateManagerController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{templateManagerController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not templateManagerController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{templateManagerController.paginator.gotoFirstPage}" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{templateManagerController.paginator.gotoPreviousPage}" />
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList
								value="#{templateManagerController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == templateManagerController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != templateManagerController.paginator.currentPage}">
									<t:updateActionListener value="#{page}"
										property="#{templateManagerController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not templateManagerController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{templateManagerController.paginator.gotoNextPage}" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{templateManagerController.paginator.gotoLastPage}" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text value="#{msgs['TEMPLATE.TEXT.TEMPLATES_BY_PAGE']}" />
							<e:selectOneMenu
								onchange="javascript:{simulateLinkClick('listTemplatesForm:data:changeButton');}"
								value="#{templateManagerController.paginator.pageSize}">
								<f:selectItems
									value="#{templateManagerController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}"
								style="display:none;" id="changeButton"
								action="#{templateManagerController.paginator.forceReload}" />
						</h:panelGroup>
					</h:panelGrid>

				</h:panelGroup>
			</f:facet>

			<t:column sortable="true" defaultSorted="true">
				<f:facet name="header">
					<e:text value="#{msgs['TEMPLATE.LABEL']}" />
				</f:facet>
				<e:text value="#{uiTemplate.label}" />

			</t:column>

			<t:column width="150px;">
				<f:facet name="header">
					<e:text value="#{msgs['TEMPLATE.MODIFY']}" />
				</f:facet>
				<e:commandButton id="modifyTemplate"
					value="#{msgs['TEMPLATE.MODIFY']}"
					action="#{templateManagerController.modifyTemplateButton}"
					image="/media/icons/pencil.png">
					<t:updateActionListener value="#{uiTemplate}"
						property="#{templateManagerController.uiTemplate}" />
				</e:commandButton>
			</t:column>

			<t:column width="20px;">
				<f:facet name="header">
					<e:text value="" />
				</f:facet>
				<e:commandButton id="delete" value="#{msgs['TEMPLATE.DELETE']}"
					action="#{templateManagerController.delete}"
					image="/media/icons/minus-circle-frame.png"
					rendered="#{uiTemplate.isDeletable}">
					<t:updateActionListener value="#{uiTemplate}"
						property="#{templateManagerController.uiTemplate}" />
				</e:commandButton>
			</t:column>
		</e:dataTable>

		<f:verbatim>
			<br />
			<br />
		</f:verbatim>
		<e:paragraph value="#{msgs['TEMPLATE.ERROR']}"
			rendered="#{empty templateManagerController.paginator.visibleItems}"
			style="color:#f00;" />

	</e:form>

	<e:form id="createTemplateForm">
		<e:commandButton id="createTemplate"
			value="#{msgs['TEMPLATE.CREATE']}"
			action="#{templateManagerController.createTemplateButton}" />
	</e:form>

</e:page>