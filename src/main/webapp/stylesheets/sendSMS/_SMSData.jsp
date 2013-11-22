<%@include file="../_include.jsp"%>


<e:outputLabel for="selectModelSMS"
	value="#{msgs['SENDSMS.LABEL.MODEL']}" />
<%--<e:form id="DataForm">--%>
<e:selectOneMenu id="selectModelSMS" onchange="submit();"
	value="#{sendSMSController.selectedSmsModel}"
	valueChangeListener="#{sendSMSController.modifSmsModel}">
	<f:selectItems value="#{sendSMSController.smsTemplateOptions}" />
</e:selectOneMenu>
<%--</e:form>--%>
<e:outputLabel for="SMSPrefix" value="#{msgs['SENDSMS.LABEL.PREFIX']}" />
<e:inputText id="SMSPrefix" value="#{sendSMSController.smsPrefix}"
	disabled="true" size="50" style="background-color:#cecece;"/>

<e:outputLabel for="SMSbody" value="#{msgs['SENDSMS.LABEL.BODY']}" />
<e:inputTextarea id="SMSbody" binding="#{sendSMSController.smsBody}"
	style="width:319px;" onkeyup="calcCarat();"/>

<e:outputLabel for="SMSSignature" value="#{msgs['SENDSMS.LABEL.SIGNATURE']}" />
<e:inputText id="SMSSignature" value="#{sendSMSController.smsSuffix}"
	readonly="true" disabled="true" size="50"  style="background-color:#cecece;"/>

<e:outputLabel for="caract" value="#{msgs['SENDSMS.LABEL.CARACT']}" />
<e:outputLabel id="caract"/>
