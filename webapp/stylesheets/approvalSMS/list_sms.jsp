<%@include file="../_include.jsp"%>
<e:page stringsVar="msgs" menuItem="approbationEnvoi" locale="#{sessionController.locale}" 
authorized="#{approvalController.pageAuthorized}" footer="">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['APPROVAL.LIST.TITLE']}" />
	
	<!-- Begin list SMS -->
  <e:form id="approvalForm">
				<e:dataTable rendered="#{not empty approvalController.paginator.visibleItems}" 
				    id="data" rowIndexVar="variable"
					value="#{approvalController.paginator.visibleItems}" var="message"
					cellpadding="5" cellspacing="3" width="100%"  >
					
					<f:facet name="header">
					<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['SMS.TEXT.TITLE']}">
								<f:param
									value="#{approvalController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{approvalController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{approvalController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{approvalController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{approvalController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not approvalController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{approvalController.paginator.gotoFirstPage}" 
									image="/media/icons/control-stop-180.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{approvalController.paginator.gotoPreviousPage}"
									image="/media/icons/control-180.png" />
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList
								value="#{approvalController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == approvalController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != approvalController.paginator.currentPage}" >
									<t:updateActionListener value="#{page}"
										property="#{approvalController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not approvalController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{approvalController.paginator.gotoNextPage}" 
									image="/media/icons/control.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{approvalController.paginator.gotoLastPage}" 
									image="/media/icons/control-stop.png" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text
								value="#{msgs['SMS.TEXT.SMS_PER_PAGE']}" />
							<e:selectOneMenu onchange="javascript:{simulateLinkClick('approvalForm:data:changeButton');}"
								value="#{approvalController.paginator.pageSize}">
								<f:selectItems
									value="#{approvalController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" style="display:none;"
								id="changeButton" action="#{approvalController.paginator.forceReload}"/>
						</h:panelGroup>
					</h:panelGrid>
									
				</h:panelGroup>
				</f:facet>
					
					<t:column sortable="true" defaultSorted="true">
						<f:facet name="header" >
							<e:text
								value="#{msgs['SMS.USER.VALUE']}" />
						</f:facet>
						<e:text value="#{message.displayName}" />
					</t:column>
			
					<t:column>
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.GROUP.VALUE']}" />
						</f:facet>
						<e:text value="#{message.groupRecipientName}" />
					</t:column>
			
					<t:column>
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.CENTER.VALUE']}" />
						</f:facet>
						<e:text value="#{message.account.label}" />
					</t:column>
					
					<t:column>
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.SERVICE.VALUE']}" />
						</f:facet>
						<e:text value="#{message.service.name}" rendered ="#{not empty message.service}" />
						<e:text value="#{msgs['SENDSMS.LABEL.NONE']}" rendered ="#{empty message.service}" />
					</t:column>
					
					<t:column>
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.CONTENT']}" />
						</f:facet>
						<e:text value="#{message.content}" />
					</t:column>
					
					<t:column>
						<f:facet name="header">
							<e:text
								value="" />
						</f:facet>
						<e:commandButton id="validate" value="#{msgs['APPROVAL.VALIDATE']}"	action="#{approvalController.validate}" 
						 title="#{msgs['APPROVAL.VALIDATE']}" >
							<t:updateActionListener value="#{message}"
							property="#{approvalController.message}" />
						</e:commandButton>
					</t:column>
					
					<t:column>
						<f:facet name="header">
							<e:text
								value="" />
						</f:facet>
						<e:commandButton id="cancel" value="#{msgs['APPROVAL.CANCEL']}"	action="#{approvalController.cancel}" 
						 title="#{msgs['APPROVAL.CANCEL']}" >
							<t:updateActionListener value="#{message}"
							property="#{approvalController.message}" />
						</e:commandButton>
					</t:column>
				</e:dataTable>
			
			<f:verbatim><br/><br/></f:verbatim>
			<e:paragraph
				value="#{msgs['APPROVAL.MESSAGES.EMPTY']}"
				rendered="#{empty approvalController.paginator.visibleItems}" style="color:#f00;"/>
		</e:form>
	<!-- End list SMS -->	

</e:page>
