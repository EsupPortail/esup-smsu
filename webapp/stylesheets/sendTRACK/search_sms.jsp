<%@include file="../_include.jsp"%>

<e:page stringsVar="msgs" menuItem="suiviEnvois" locale="#{sessionController.locale}" footer="">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['SEND.SEARCH.TITLE']}" />
	
	
	<!-- Begin search SMS -->
	
<h:form id="searchSMS">
  
  <e:panelGrid columns="2">
        <e:outputLabel for="beginDate" value="#{msgs['SEND.DATE.BEGIN']}"/>
  		<h:panelGroup>  
    	<e:inputText id="beginDate" value="#{messagesController.beginDate}" maxlength="10">
			<f:convertDateTime locale="#{preferencesController.locale}" pattern="dd/MM/yyyy" timeZone="Europe/Paris"/>
		</e:inputText>	
		<e:message for="beginDate" style="color:#f00;"/>
		</h:panelGroup>
		
		<e:outputLabel for="endDate" value="#{msgs['SEND.DATE.END']}"/>
  		<h:panelGroup>  
    	<e:inputText id="endDate" value="#{messagesController.endDate}" maxlength="10">
			<f:convertDateTime locale="#{preferencesController.locale}" pattern="dd/MM/yyyy" timeZone="Europe/Paris"/>
		</e:inputText>
    	<e:message for="endDate" style="color:#f00;"/>
		</h:panelGroup>	
		
        <e:outputLabel  for="users"  value="#{msgs['SEND.SEARCH.USERS']}" />
    	<e:selectOneMenu id="users" value="#{usersController.userUserId}">
		   	<f:selectItems  value="#{usersController.userUserItems}" />
  		</e:selectOneMenu>
		
  	    <e:outputLabel  for="templates"   value="#{msgs['SEND.SEARCH.MODELS']}" />
    	<e:selectOneMenu id="templates"  value="#{templatesController.userTemplateId}" >
			<f:selectItems value="#{templatesController.userTemplateItems}" />
  		</e:selectOneMenu>
	
		<e:outputLabel  for="groups"  value="#{msgs['SEND.SEARCH.GROUPS']}" />
    	<e:selectOneMenu id="groups" value="#{groupsController.userGroupId}" >
		   	<f:selectItems value="#{groupsController.userGroupItems}" />
  		</e:selectOneMenu>
  	
  		<e:outputLabel  for="accounts"   value="#{msgs['SEND.SEARCH.CENTERS']}" />
   		<e:selectOneMenu id="accounts" value="#{accountsController.userAccountId}" >
			<f:selectItems  value="#{accountsController.userAccountItems}" />
  		</e:selectOneMenu>
  		
  		<e:outputLabel  for="services"   value="#{msgs['SEND.SEARCH.SERVICES']}" />
    	<e:selectOneMenu id="services"  value="#{servicesController.userServiceId}" >
			<f:selectItems value="#{servicesController.userServiceItems}" />
  		</e:selectOneMenu>
  	</e:panelGrid>
	
	<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;<br/><br/></f:verbatim>
    <f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
	
  
  <e:inputText id="dates" style="display:none;"></e:inputText>
  <e:message for="dates" style="color:#f00;"/>
    
  <f:verbatim><br/><br/></f:verbatim>
                
  <e:commandButton id="send" value="#{msgs['SEND.BUTTON.SEARCH']}"  action="#{messagesController.listSend}" >

  <e:message for="send" style="color:#f00;"/>
  
  <t:updateActionListener value="#{groupsController.userGroupId}"
	property="#{messagesController.userGroupId}" />

  <t:updateActionListener value="#{usersController.userUserId}"
	property="#{messagesController.userUserId}" />

  <t:updateActionListener value="#{accountsController.userAccountId}"
	property="#{messagesController.userAccountId}" />

 <t:updateActionListener value="#{servicesController.userServiceId}"
	property="#{messagesController.userServiceId}" />

  <t:updateActionListener value="#{templatesController.userTemplateId}"
	property="#{messagesController.userTemplateId}" />

  </e:commandButton>
  <e:commandButton   value="#{msgs['_.BUTTON.CANCEL']}"  action="cancelSend"   immediate="true" />
	    
  </h:form>
  
  <f:verbatim><br/><br/><br/></f:verbatim>

  <!-- End search SMS -->
  <!-- Begin list SMS -->
  <t:panelGroup binding="#{messagesController.listSearchPanelGrid}">
	<e:form id="messagesForm">
				<e:dataTable rendered="#{not empty messagesController.paginator.visibleItems}" 
				    id="data" rowIndexVar="variable"
					value="#{messagesController.paginator.visibleItems}" var="message"
					cellpadding="5" cellspacing="3" width="100%"  >
					
					<f:facet name="header">
					<h:panelGroup>
					<h:panelGrid columns="3" columnClasses="colLeft,,colRight"
						width="100%">
						<h:panelGroup>
							<e:text value="#{msgs['SMS.TEXT.TITLE']}">
								<f:param
									value="#{messagesController.paginator.firstVisibleNumber + 1}" />
								<f:param
									value="#{messagesController.paginator.lastVisibleNumber + 1}" />
								<f:param
									value="#{messagesController.paginator.totalItemsCount}" />
							</e:text>
						</h:panelGroup>
						<h:panelGroup
							rendered="#{messagesController.paginator.lastPageNumber == 0}" />
						<h:panelGroup
							rendered="#{messagesController.paginator.lastPageNumber != 0}">
							<h:panelGroup
								rendered="#{not messagesController.paginator.firstPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.FIRST']}"
									action="#{messagesController.paginator.gotoFirstPage}" 
									image="/media/icons/control-stop-180.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.PREVIOUS']}"
									action="#{messagesController.paginator.gotoPreviousPage}"
									image="/media/icons/control-180.png" />
							</h:panelGroup>
							<e:text value=" #{msgs['PAGINATION.TEXT.PAGES']} " />
							<t:dataList
								value="#{messagesController.paginator.nearPages}"
								var="page">
								<e:text value=" " />
								<e:italic value="#{page + 1}"
									rendered="#{page == messagesController.paginator.currentPage}" />
								<h:commandLink value="#{page + 1}"
									rendered="#{page != messagesController.paginator.currentPage}" >
									<t:updateActionListener value="#{page}"
										property="#{messagesController.paginator.currentPage}" />
								</h:commandLink>
								<e:text value=" " />
							</t:dataList>
							<h:panelGroup
								rendered="#{not messagesController.paginator.lastPage}">
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.NEXT']}"
									action="#{messagesController.paginator.gotoNextPage}" 
									image="/media/icons/control.png" />
								<e:text value=" " />
								<e:commandButton value="#{msgs['PAGINATION.BUTTON.LAST']}"
									action="#{messagesController.paginator.gotoLastPage}" 
									image="/media/icons/control-stop.png" />
							</h:panelGroup>
						</h:panelGroup>
						<h:panelGroup>
							<e:text
								value="#{msgs['SMS.TEXT.SMS_PER_PAGE']}" />
							<e:selectOneMenu onchange="javascript:{simulateLinkClick('messagesForm:data:changeButton');}"
								value="#{messagesController.paginator.pageSize}">
								<f:selectItems
									value="#{messagesController.paginator.pageSizeItems}" />
							</e:selectOneMenu>
							<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" style="display:none;"
								id="changeButton" action="#{messagesController.paginator.forceReload}"/>
						</h:panelGroup>
					</h:panelGrid>
									
				</h:panelGroup>
				</f:facet>
					
					<t:column sortable="true" defaultSorted="true" >
						<f:facet name="header" >
							<e:text
								value="#{msgs['SMS.DATE']}" />
						</f:facet>
						<e:text value="#{message.date}" >
						<f:convertDateTime locale="#{preferencesController.locale}" pattern="dd/MM/yyyy" timeZone="Europe/Paris"/>
						</e:text>
					</t:column>
					
					<t:column>
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.STATE']}" />
						</f:facet>
						<e:text value="#{message.stateMessage}" />
					</t:column>
					
					<t:column >
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.USER.VALUE']}" />
						</f:facet>
						<e:text value="#{message.senderName}" />
					</t:column>
			
					<t:column>
						<f:facet name="header">
							<e:text
								value="#{msgs['SMS.GROUP.SENDER']}" />
						</f:facet>
						<e:text value="#{message.groupSenderName}" />
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
								value="#{msgs['SMS.DETAIL']}" />
						</f:facet>
						<e:commandButton id="detailPage" value="#{msgs['SMS.DISPLAY']}"	action="#{messagesController.displayDetails}" 
						image="/media/icons/pencil.png" title="#{msgs['SMS.DISPLAY']}" >
							<t:updateActionListener value="#{message}"
							property="#{messagesController.message}" />
						</e:commandButton>
					</t:column>
						
				</e:dataTable>
			
			<f:verbatim><br/><br/></f:verbatim>
			<e:paragraph
				value="#{msgs['SEND.SEARCH.MESSAGES.CRITERIA.ERROR']}"
				rendered="#{empty messagesController.paginator.visibleItems}" style="color:#f00;"/>
		</e:form>

	</t:panelGroup>	
	<!-- End list SMS -->	


</e:page>
