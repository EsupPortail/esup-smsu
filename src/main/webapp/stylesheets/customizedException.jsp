<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="adhesion"
	locale="#{sessionController.locale}">
	<%@include file="_navigation.jsp"%>
	<e:paragraph value="#{msgs['CUSTOMIZED_EXCEPTION.TOP']}" />
	<e:outputLabel for="exceptionName"
		value="#{msgs['EXCEPTION.EXCEPTION.NAME']}" />
	<e:text id="exceptionName" value="#{exceptionController.exceptionName}" />
	<e:outputLabel for="exceptionMessage"
		value="#{msgs['EXCEPTION.EXCEPTION.MESSAGE']}" />
	<e:text id="exceptionMessage"
		value="#{exceptionController.exceptionMessage}" />
	<e:messages />
	<e:form id="exceptionForm" >
	<h:panelGroup>
		<h:panelGroup style="cursor: pointer" onclick="simulateLinkClick('exceptionForm:restartButton');" >
			<e:bold value="#{msgs['EXCEPTION.BUTTON.RESTART']} " />
			<t:graphicImage value="/media/images/restart.png"
				alt="#{msgs['EXCEPTION.BUTTON.RESTART']}" 
				title="#{msgs['EXCEPTION.BUTTON.RESTART']}" />
		</h:panelGroup>
		<e:commandButton style="display: none" id="restartButton" 
			action="#{exceptionController.restart}"
			value="#{msgs['EXCEPTION.BUTTON.RESTART']}" />
	</h:panelGroup>
</e:form>
</e:page>
