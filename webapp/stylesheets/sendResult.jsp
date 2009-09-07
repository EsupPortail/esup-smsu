<%@include file="_include.jsp"%>

<e:page stringsVar="msgs"
	locale="#{sessionController.locale}"
	authorized="#{sendSMSController.pageAuthorized}">

	<%@include file="_navigation.jsp"%>
	<f:verbatim><br><br><br></f:verbatim>
	<e:outputLabel style="color:green;" value="#{msgs['MSG.SENDING.FOR.BACKOFFICE']}" rendered="#{sendSMSController.isShowMsgSending}"/>
	<f:verbatim><br></f:verbatim>
	<e:outputLabel style="color:green;" value="#{msgs['MSG.WAITTING.FOR.APPROVAL']}" rendered="#{sendSMSController.isShowMsgWainting}"/>
	<f:verbatim><br></f:verbatim>
	<e:outputLabel style="color:green;" value="#{msgs['MSG.WAITTING.FOR.NORECIPIENTFOUND']}" rendered="#{sendSMSController.isShowMsgNoRecipientFound}"/>
	<f:verbatim><br><br><br></f:verbatim>
</e:page>
	