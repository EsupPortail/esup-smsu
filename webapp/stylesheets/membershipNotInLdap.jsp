<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="adhesion"
	locale="#{sessionController.locale}"
	authorized="#{membershipController.pageAuthorized}">
	<%@include file="_navigation.jsp"%>
	
	<e:form id="formMembershipNotInLdap">
			<e:paragraph style="color:red;" value="#{msgs['ADHESION.LOGIN.NOT.IN.LDAP']}" />
	</e:form>
</e:page>