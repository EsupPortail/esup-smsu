<%@include file="../_include.jsp"%>




<e:outputLabel value="#{msgs['SENDMAIL.LABEL.RECIPIENTS']}" rendered="#{sendSMSController.isShow}"/>
<e:selectBooleanCheckbox id="checkbox2" binding="#{sendSMSController.checkboxRecipients}" 
rendered="#{sendSMSController.isShow}"/>


<e:outputLabel for="Others" value="#{msgs['SENDMAIL.LABEL.OTHERS']}" />
<e:inputText id="Others" value="#{sendSMSController.mailOtherRecipients}"  />

<e:outputLabel for="selectModelMAIL"
	value="#{msgs['SENDMAIL.LABEL.MODEL']}" />
<%--<e:form id="DataForm">--%>
<e:selectOneMenu id="selectModelMAIL" onchange="submit();"
	value="#{sendSMSController.selectedMailModel}"
	valueChangeListener="#{sendSMSController.modifMailModel}">
	<f:selectItems value="#{sendSMSController.smsTemplateOptions}" />
</e:selectOneMenu>

<e:outputLabel for="Subject" value="#{msgs['SENDMAIL.LABEL.SUBJECT']}" />
<e:inputText id="Subject" value="#{sendSMSController.mailSubject}" size="50" />

<%--</e:form>--%>
<e:outputLabel for="MAILPrefix" value="#{msgs['SENDSMS.LABEL.PREFIX']}" />
<e:inputText id="MAILPrefix" value="#{sendSMSController.mailPrefix}"
	disabled="true" size="50" />

<e:outputLabel for="MAILbody" value="#{msgs['SENDSMS.LABEL.BODY']}" />
<e:inputTextarea id="MAILbody" binding="#{sendSMSController.mailBody}"
	style="width:319px;"  />

<e:outputLabel for="MAILSignature" value="#{msgs['SENDSMS.LABEL.SIGNATURE']}" />
<e:inputText id="MAILSignature" value="#{sendSMSController.mailSuffix}"
	readonly="true" disabled="true" size="50" />

