<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="envoiSMS"
	locale="#{sessionController.locale}"
	authorized="#{sendSMSController.pageAuthorized}">

	<%@include file="_navigation.jsp"%>

<script type="text/javascript">
	var recipientSelected = false;
	function selectRecipient(linkId, phone) {
		if(phone != '') {
			if (!recipientSelected) {
				recipientSelected = true;
				simulateLinkClick(linkId);
	  		}
		}
	}
	function mailPanel(linkId) {
			simulateLinkClick(linkId);
	  	
	}
</script>


	<e:section value="#{msgs['SENDSMS.TITLE']}" />
	<e:messages globalOnly="true"/>
<e:form id="formGeneral" >
	

		<e:panelGrid border="0" columns="2" width="550px">
		
		<%--
	group selection
	 --%>
			<%@include file="./sendSMS/_senderGroupSelection.jsp"%>

			<%--
	service selection
	 --%>

			<%@include file="./sendSMS/_serviceSelection.jsp"%>

			<%--
	service selection
	 --%>

			<%@include file="./sendSMS/_recipients.jsp"%>
			
			<%-- 
	SMS data
	 --%>

			<%@include file="./sendSMS/_SMSData.jsp"%>
			
			<e:outputLabel value="#{msgs['SENDMAIL.LABEL']}" rendered="#{sendSMSController.isCheckBoxSendMailShow}"/>
			<e:selectBooleanCheckbox id="checkbox1" binding="#{sendSMSController.checkbox}" 
			onchange="javascript:{simulateLinkClick('formGeneral:changeButton');}" rendered="#{sendSMSController.isCheckBoxSendMailShow}"/>
			 
			<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" style="display:none;"
								id="changeButton" action="#{sendSMSController.showMailPanel}"/>
		
		</e:panelGrid>
        <f:verbatim><br/><br/></f:verbatim>
        
        <t:panelGroup id="mailPanel" binding="#{sendSMSController.mailPanelGrid}">
		<e:panelGrid border="0" columns="2" width="550px" >
		<%-- 
	MAIL data
	 --%>
			<%@include file="./sendSMS/_MAILData.jsp"%>
		</e:panelGrid>
		
		</t:panelGroup>
		<f:verbatim><br/><br/></f:verbatim>
		
		<e:commandButton value="#{msgs['SENDSMS.SENDBUTTON']}"
			id="sendSMSButton" action="#{performSendSmsController.sendSMSAction}" >
		</e:commandButton>
</e:form>
<script type="text/javascript">
highlightTableRows("formLdapSearchResult:recipientSearchList");
</script>
</e:page>