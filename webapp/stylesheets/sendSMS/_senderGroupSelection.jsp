<%@include file="../_include.jsp"%>

<e:outputLabel for="selectGroup" value="#{msgs['SENDSMS.LABEL.GROUP']}" />
<e:selectOneMenu id="selectGroup"
	value="#{sendSMSController.selectedUserGroup}">
	<f:selectItems value="#{sendSMSController.userGroupsOptions}" />
</e:selectOneMenu>
<t:panelGroup colspan="2">
</t:panelGroup>