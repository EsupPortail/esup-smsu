<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="administrators"
	locale="#{sessionController.locale}"
	authorized="#{administratorsController.pageAuthorized}">
	<%@include file="_navigation.jsp"%>

	<e:form id="administratorsForm">
		<e:panelGrid columns="2" columnClasses="colLeft,colRight" width="100%"
			cellspacing="0" cellpadding="0">
			<e:section value="#{msgs['ADMINISTRATORS.TITLE']}" />
			<e:commandButton action="addAdmin"
				value="#{msgs['ADMINISTRATORS.BUTTON.ADD_ADMIN']}"
				rendered="#{administratorsController.currentUserCanAddAdmin}" />
		</e:panelGrid>

		<e:messages />

		<e:dataTable
			rendered="#{not empty administratorsController.paginator.visibleItems}"
			id="data" rowIndexVar="variable"
			value="#{administratorsController.paginator.visibleItems}"
			var="admin" border="0" style="width:100%" cellspacing="0"
			cellpadding="0">
			<f:facet name="header">
				<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['ADMINISTRATORS.TEXT.ADMINISTRATORS']}">
								<f:param
									value="#{administratorsController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{administratorsController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{administratorsController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{administratorsController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{administratorsController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not administratorsController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{administratorsController.paginator.gotoFirstPage}" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{administratorsController.paginator.gotoPreviousPage}" />
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList
								value="#{administratorsController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == administratorsController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != administratorsController.paginator.currentPage}" >
									<t:updateActionListener value="#{page}"
										property="#{administratorsController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not administratorsController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{administratorsController.paginator.gotoNextPage}" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{administratorsController.paginator.gotoLastPage}" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text
								value="#{msgs['ADMINISTRATORS.TEXT.ADMINISTRATORS_PER_PAGE']} " />
							<e:selectOneMenu onchange="javascript:{simulateLinkClick('administratorsForm:data:changeButton');}"
								value="#{administratorsController.paginator.pageSize}">
								<f:selectItems
									value="#{administratorsController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}"
								id="changeButton" action="#{administratorsController.paginator.forceReload"/>
						</h:panelGroup>
					</h:panelGrid>
					<t:htmlTag value="hr" />
				</h:panelGroup>
			</f:facet>
			<t:column>
				<e:bold value="#{admin.displayName} (#{admin.id})" />
			</t:column>
			<t:column style="text-align: right;">
				<e:commandButton action="deleteAdmin"
					rendered="#{sessionController.currentUser.id != admin.id}"
					value="#{msgs['ADMINISTRATORS.BUTTON.DELETE_ADMIN']}">
					<t:updateActionListener value="#{admin}"
						property="#{administratorsController.userToDelete}" />
				</e:commandButton>
			</t:column>
			<f:facet name="footer">
				<t:htmlTag value="hr" />
			</f:facet>
		</e:dataTable>

		<e:subSection value="#{msgs['ADMINISTRATORS.HEADER.LDAP_STATISTICS']}" />
		<h:panelGroup
			rendered="#{not empty administratorsController.ldapStatistics}">
			<e:dataTable value="#{administratorsController.ldapStatistics}"
				var="string" border="0" style="width: 100%" cellspacing="0"
				cellpadding="0">
				<f:facet name="header">
					<h:panelGroup>
						<t:htmlTag value="hr" />
					</h:panelGroup>
				</f:facet>
				<t:column>
					<e:bold value="#{string}" />
				</t:column>
				<f:facet name="footer">
					<t:htmlTag value="hr" />
				</f:facet>
			</e:dataTable>
		</h:panelGroup>
		<e:subSection
			value="#{msgs['ADMINISTRATORS.TEXT.LDAP_STATISTICS.NONE']}"
			rendered="#{empty administratorsController.ldapStatistics}" />
	</e:form>
	<script type="text/javascript">	
		hideButton("administratorsForm:data:changeButton");
	</script>
</e:page>
