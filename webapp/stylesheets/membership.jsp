<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="adhesion"
	locale="#{sessionController.locale}"
	authorized="#{membershipController.pageAuthorized}">
	<%@include file="_navigation.jsp"%>
	
	<e:section value="" />
	
	<e:messages globalOnly="true" />
	
	<e:form id="formMembership">
			<e:panelGrid columns="2" rendered="#{membershipController.member.flagPending}"  style="border-style:double">
			<t:panelGroup colspan="2">
				<e:text id="validtext" value="#{msgs['ADHESION.TEXT.MEMBERVALIDATION']}" />
			</t:panelGroup>
			<e:outputLabel for="phoneNumberValidationCode"
				value="#{msgs['ADHESION.LABEL.PHONENUMBERVALIDATIONCODE']}"/>
			<h:panelGroup >
			<e:inputText id="phoneNumberValidationCode"
				value="#{membershipController.member.phoneNumberValidationCode}"
				maxlength="255" />
			<e:message for="phoneNumberValidationCode" />
			</h:panelGroup >
			<e:commandButton value="#{msgs['ADHESION.BOUTON.VALIDATIONCODE']}" action="#{membershipController.validCode}" />
		</e:panelGrid>
	
		<e:panelGrid columns="2"  style="border-style:double" rendered="#{not membershipController.member.flagPending}">
			<e:outputLabel for="firstName"
				value="#{msgs['ADHESION.LABEL.FIRSTNAME']}" />
			<e:text id="firstName"
				value="#{membershipController.member.firstName}" />

			<e:outputLabel for="lastName"
				value="#{msgs['ADHESION.LABEL.LASTNAME']}" />
			<e:text id="lastName" value="#{membershipController.member.lastName}" />
	
			<e:outputLabel for="phoneNumber"
				value="#{msgs['ADHESION.LABEL.PHONENUMBER']}" />
			<h:panelGroup >
				<e:inputText id="phoneNumber"
					value="#{membershipController.member.phoneNumber}" maxlength="10"
					required="true"
					validator="#{membershipController.validatePhoneNumber}"/>
				<e:message for="phoneNumber" />
			</h:panelGroup>

			<e:outputLabel for="conditionsGenerales"
					value="#{msgs['ADHESION.LABEL.CONDITIONSGENERALES']}" />
			<h:panelGroup >
				<e:paragraph id="conditionsGenerales"
					value="#{msgs['ADHESION.TEXT.CONDITIONSGENERALES']}" />
				<e:selectBooleanCheckbox id="validGeneralConditions"
					value="#{membershipController.member.validCG}"/>
				<e:text value="#{msgs['ADHESION.LABEL.IACCEPT']}" />
			</h:panelGroup>
			
			<e:outputLabel for="conditionsParticulieres"
				value="#{msgs['ADHESION.LABEL.CONDITIONSPARTICULIERES']}" />
			<h:panelGroup >
				<e:text id="conditionsParticulieres"
					value="#{msgs['ADHESION.TEXT.CONDITIONSPARTICULIERES']}" />
				<e:selectManyCheckbox id="validParticularConditions"
		   			value="#{membershipController.member.validCP}"
		    		layout="pageDirection">
					<t:selectItems value="#{membershipController.allServices}" var="svc" itemValue="#{svc.key}" itemLabel="#{svc.name}"/>
				</e:selectManyCheckbox>
			</h:panelGroup>			
	
			<h:panelGroup style="text-align:center">
				<e:commandButton value="#{msgs['ADHESION.BOUTON.VALIDATIONINFO']}" action="#{membershipController.save}" />
			</h:panelGroup>
	
		</e:panelGrid>
			
	</e:form>
</e:page>