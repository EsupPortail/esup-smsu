<%@include file="_include.jsp"%>
<e:page stringsVar="msgs" menuItem="adhesion"
	locale="#{sessionController.locale}"
	authorized="#{membershipController.pageAuthorized}" footer="">
	<%@include file="_navigation.jsp"%>
	<script type="text/javascript">
	function selectPhoneNumberFromAvailable(value) {
		document.getElementById("formMembership:phoneNumber").value = value;
	}
</script>

	<e:section value="#{msgs['MEMBERSHIP.TITLE']}" />

	<e:messages globalOnly="true" />

	<e:panelGrid rendered="#{membershipController.isValidationOK}" id="pan">
		<e:text id="MemberCodeOk"
			value="#{msgs['ADHESION.MESSAGE.MEMBERCODEOK']}" />
	</e:panelGrid>

	<e:form id="formMembership">
		<e:panelGrid columns="2"
			rendered="#{membershipController.member.flagPending}" id="palll">
			<t:panelGroup colspan="2">
				<e:text id="validtext"
					value="#{msgs['ADHESION.TEXT.MEMBERVALIDATION']}" />
			</t:panelGroup>
			<e:outputLabel for="phoneNumberValidationCode"
				value="#{msgs['ADHESION.LABEL.PHONENUMBERVALIDATIONCODE']}" />
			<h:panelGroup>
				<e:inputText id="phoneNumberValidationCode"
					value="#{membershipController.member.phoneNumberValidationCode}"
					maxlength="255" />
				<e:message for="phoneNumberValidationCode" />
			</h:panelGroup>
			<t:panelGroup colspan="2">
				<e:commandButton value="#{msgs['ADHESION.BOUTON.VALIDATIONCODE']}"
					action="#{membershipController.validCode}" />
			</t:panelGroup>
		</e:panelGrid>

		<e:panelGrid columns="2"
			rendered="#{not membershipController.member.flagPending}" id="panel">
			<e:outputLabel for="firstName"
				value="#{msgs['ADHESION.LABEL.FIRSTNAME']}" />
			<e:text id="firstName"
				value="#{membershipController.member.firstName}" />

			<e:outputLabel for="lastName"
				value="#{msgs['ADHESION.LABEL.LASTNAME']}" />
			<e:text id="lastName" value="#{membershipController.member.lastName}" />



			<e:outputLabel for="phoneNumber"
				value="#{msgs['ADHESION.LABEL.PHONENUMBER']}" />
			<h:panelGroup>
				<e:panelGrid columns="2" id="b">
					<h:panelGroup>
						<e:inputText id="phoneNumber"
							value="#{membershipController.member.phoneNumber}" maxlength="10"
							validator="#{membershipController.validatePhoneNumber}" />
						<e:message for="phoneNumber" />
					</h:panelGroup>
					<h:panelGroup
						rendered="#{not empty membershipController.availablePhoneNumbers}">
						<e:panelGrid columns="2" id="panelautre">
							<e:outputLabel for="availablePhoneNumbers"
								value="#{msgs['ADHESION.LABEL.AVAILABLEPHONENUMBERS']}" />
							<h:selectOneListbox id="availablePhoneNumbers"
								onclick="selectPhoneNumberFromAvailable(this.options[this.selectedIndex].value)">
								<f:selectItems
									value="#{membershipController.availablePhoneNumbers}" />
							</h:selectOneListbox>
						</e:panelGrid>
					</h:panelGroup>
					<h:panelGroup
						rendered="#{empty membershipController.availablePhoneNumbers}">
						<f:verbatim> </f:verbatim>
					</h:panelGroup>
				</e:panelGrid>
			</h:panelGroup>

			<e:outputLabel for="conditionsGenerales"
				value="#{msgs['ADHESION.LABEL.CONDITIONSGENERALES']}" />
			<h:panelGroup>
				<e:paragraph id="conditionsGenerales"
					value="#{msgs['ADHESION.TEXT.CONDITIONSGENERALES']}" />
				<e:selectBooleanCheckbox id="validGeneralConditions"
					value="#{membershipController.member.validCG}" />
				<e:text value="#{msgs['ADHESION.LABEL.IACCEPT']}" />
			</h:panelGroup>

			<e:outputLabel for="conditionsParticulieres"
				value="#{msgs['ADHESION.LABEL.CONDITIONSPARTICULIERES']}"
				rendered="#{not empty membershipController.allServices}" />
			<h:panelGroup
				rendered="#{not empty membershipController.allServices}">
				<e:text id="conditionsParticulieres"
					value="#{msgs['ADHESION.TEXT.CONDITIONSPARTICULIERES']}" />
				<e:selectManyCheckbox id="validParticularConditions"
					value="#{membershipController.member.validCP}"
					layout="pageDirection">
					<t:selectItems value="#{membershipController.allServices}"
						var="svc" itemValue="#{svc.key}" itemLabel="#{svc.name}" />
				</e:selectManyCheckbox>
			</h:panelGroup>

			<t:panelGroup colspan="2">
				<e:text id="infoEnvoiSmsValidation"
					value="#{msgs['ADHESION.TEXT.INFOENVOISMSVALIDATION']}"
					rendered="#{membershipController.activateValidation}" />
			</t:panelGroup>

			<t:panelGroup style="text-align:center" colspan="2">
				<e:commandButton value="#{msgs['ADHESION.BOUTON.VALIDATIONINFO']}"
					action="#{membershipController.save}" />
			</t:panelGroup>

		</e:panelGrid>

	</e:form>
</e:page>