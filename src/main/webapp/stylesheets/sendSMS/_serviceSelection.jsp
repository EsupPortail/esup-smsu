<%@include file="../_include.jsp"%>

<e:outputLabel for="selectService"
	value="#{msgs['SENDSMS.LABEL.SERVICE']}" />

	<e:selectOneMenu id="selectService"
		value="#{sendSMSController.selectedService}"
		onchange="submit();"
		valueChangeListener="#{smsRecipientController.clearRecipients}"
		validator="#{performSendSmsController.validateService}">
		<f:selectItems value="#{sendSMSController.serviceOptions}" />
	</e:selectOneMenu>
