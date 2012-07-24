<%@include file="../_include.jsp"%>
<e:page stringsVar="msgs" menuItem="suiviEnvois" locale="#{sessionController.locale}" footer="">
	<%@include file="../_navigation.jsp"%>
	<e:section value="#{msgs['SEND.DETAIL.TITLE']}" />

	<e:panelGrid columns="2" cellpadding="5" cellspacing="3">


	<e:bold value="#{msgs['SMS.CONTENT']}"></e:bold>
	<e:text value="#{messagesController.message.content}" />
		
	<e:bold value="#{msgs['SMS.CENTER.VALUE']}"></e:bold>
	<e:text value="#{messagesController.message.account.label}" />
		
	<e:bold value="#{msgs['SMS.DATE']}"></e:bold>
	<e:text value="#{messagesController.message.date}" >
	<f:convertDateTime locale="#{preferencesController.locale}" pattern="dd/MM/yyyy kk:mm" timeZone="Europe/Paris"/>
	</e:text>

	<e:bold value="#{msgs['SMS.STATE']}"></e:bold>
	<e:text value="#{messagesController.message.stateMessage}" />
	
	<e:bold value="#{msgs['SMS.NBR.RECEVERS']}"></e:bold>
	<e:text id="TT" value="#{messagesController.destCount}" />
			
	<e:bold rendered="#{messagesController.backListDestCount != null}" value="#{msgs['SMS.NBR.RECEVERS.BACKLIST']}"></e:bold>
	<e:text rendered="#{messagesController.backListDestCount != null}" value="#{messagesController.backListDestCount}" />
		
	<e:bold rendered="#{messagesController.sentSMSCount != null}" value="#{msgs['SMS.NBR.SENT']}"></e:bold>
	<e:text rendered="#{messagesController.sentSMSCount != null}" value="#{messagesController.sentSMSCount}" />
		
	<e:bold rendered="#{messagesController.message.stateMail != ''}" value="#{msgs['SMS.MAIL.SEND']}"></e:bold>
	<e:text rendered="#{messagesController.message.stateMail != ''}" value="#{messagesController.message.stateMail}" />
	
	
	</e:panelGrid>
	
	<f:verbatim><br/><br/></f:verbatim>
<h:panelGrid columns="1" >
<e:form >	
	<e:commandButton value="#{msgs['SMS.PAGE.RETOUR.RECHERCHE']}" action="TrackingSend"/>
</e:form>
</h:panelGrid>
	
<e:message for="TT" style="color:#f00;"/>
	<f:verbatim><br/><br/></f:verbatim>
</e:page>

