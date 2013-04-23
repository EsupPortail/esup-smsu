<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="envoiSMS"
	locale="#{sessionController.locale}"
	authorized="#{sendSMSController.pageAuthorized}" footer="">

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

	function calcCarat() {
		var body = document.getElementById("formGeneral:SMSbody").value;
		var prefix = document.getElementById("formGeneral:SMSPrefix").value;
		var signature = document.getElementById("formGeneral:SMSSignature").value;
		var msg = body + prefix + signature;
		msg = msg.replace(/[\[\]{}\\~^|\u20AC]/g, "xx"); // cf GSM's "Basic Character Set Extension". \u20AC is euro character
		var remainings = 160 - msg.length;
		document.getElementById("formGeneral:caract").value = remainings;
		if (remainings < 0) {
			document.getElementById("formGeneral:caract").style.color = "red";
		} else {
			document.getElementById("formGeneral:caract").style.color = "black";
		}
		return remainings >= 0;
	}
	
	function validateForm() {
	         var elt = document.getElementById("error-MESSAGETOOLONG");
		 if (!calcCarat()) {
			elt.style.display = "block";
			return false;
		 } else {
			elt.style.display = "none";
		 }
		 return true;
	}

</script>


	<e:section value="#{msgs['SENDSMS.TITLE']}" />

	<e:ul id='error-MESSAGETOOLONG' style="display:none" >
	   <e:li value="#{msgs['SENDSMS.MESSAGE.MESSAGETOOLONG']}" styleClass='portlet-msg-error' />
	</e:ul>

	<e:messages globalOnly="true"/>
<e:form id="formGeneral" onsubmit="return validateForm()" >
	

		<e:panelGrid border="0" columns="2" >
			<%--group selection --%>
			<%@include file="./sendSMS/_senderGroupSelection.jsp"%>

			<%--service selection--%>
			<%@include file="./sendSMS/_serviceSelection.jsp"%>

			<%--service selection --%>
			<%@include file="./sendSMS/_recipients.jsp"%>
			
			<%--SMS data--%>
			<%@include file="./sendSMS/_SMSData.jsp"%>
		<t:panelGroup colspan="2">			
			<e:outputLabel value="#{msgs['SENDMAIL.LABEL']}" rendered="#{sendSMSController.isCheckBoxSendMailShow}" for="checkbox1"/>
			<e:selectBooleanCheckbox id="checkbox1" binding="#{sendSMSController.checkbox}" 
			onchange="javascript:{simulateLinkClick('formGeneral:changeButton');}" rendered="#{sendSMSController.isCheckBoxSendMailShow}"/>
		</t:panelGroup>	 
			<e:commandButton value="#{msgs['_.BUTTON.CHANGE']}" style="display:none;"
								id="changeButton" action="#{sendSMSController.showMailPanel}"/>
		
		</e:panelGrid>
        <f:verbatim><br/></f:verbatim>
        
        <t:panelGroup colspan="2" id="mailPanel" binding="#{sendSMSController.mailPanelGrid}">
		<e:panelGrid border="0" columns="2" >
		<%-- 
	MAIL data
	 --%>
			<%@include file="./sendSMS/_MAILData.jsp"%>
		</e:panelGrid>
		
		</t:panelGroup>
		
		<f:verbatim><br/></f:verbatim>
		
		<e:commandButton value="#{msgs['SENDSMS.SENDBUTTON']}"
			id="sendSMSButton" action="#{performSendSmsController.sendSMSAction}" >
		</e:commandButton>
</e:form>
<script type="text/javascript">
highlightTableRows("formLdapSearchResult:recipientSearchList");
calcCarat();
</script>
</e:page>