<%@include file="_include.jsp"%>
       <e:menuItem id="welcome" value="#{msgs['NAVIGATION.TEXT.WELCOME']}"
            action="#{welcomeController.enter}"
            accesskey="#{msgs['NAVIGATION.ACCESSKEY.WELCOME']}" />
        <e:menuItem id="login" action="casLogin"
            value="#{msgs['NAVIGATION.TEXT.LOGIN']}"
            accesskey="#{msgs['NAVIGATION.ACCESSKEY.LOGIN']}"
            rendered="#{sessionController.printLogin}" />
        <e:menuItem id="logout" action="#{sessionController.logout}"
            value="#{msgs['NAVIGATION.TEXT.LOGOUT']}"
            accesskey="#{msgs['NAVIGATION.ACCESSKEY.LOGOUT']}"
            rendered="#{sessionController.printLogout}" />
        <e:menuItem id="adhesion" 
            value="#{msgs['NAVIGATION.TEXT.ADHESION']}"
          action="#{membershipController.enter}" 
			rendered="#{membershipController.pageAuthorized}" />
        <e:menuItem id="envoiSMS" 
            value="#{msgs['NAVIGATION.TEXT.ENVOISMS']}" 
            action="#{sendSMSController.enter}" 
            rendered="#{sendSMSController.pageAuthorized}" />
        <e:menuItem id="suiviEnvois" 
            value="#{msgs['NAVIGATION.TEXT.SUIVIENVOIS']}"
            action="#{messagesController.enter}"
            rendered="#{messagesController.pageAuthorized}" />
        <e:menuItem id="approbationEnvoi" 
            value="#{msgs['NAVIGATION.TEXT.APPROBATIONENVOI']}"
            action="#{approvalController.enter}"
            rendered="#{approvalController.pageAuthorized}" />
        <e:menuItem id="templates" 
            value="#{msgs['NAVIGATION.TEXT.GESTIONMODELES']}"
            action="#{templateManagerController.enter}"
            rendered="#{templateManagerController.pageAuthorized}" />
        <e:menuItem id="gestionRoles" 
            value="#{msgs['NAVIGATION.TEXT.GESTIONROLES']}"
            action="#{rolesController.enter}"
            rendered="#{rolesController.pageAuthorized}" />
        <e:menuItem id="gestionGroupes"
            value="#{msgs['NAVIGATION.TEXT.GESTIONGROUPES']}"
             action="#{groupsManagerController.enter}"
            rendered="#{groupsManagerController.pageAuthorized}" />
        <e:menuItem id="gestionServicesCP"
            value="#{msgs['NAVIGATION.TEXT.GESTIONSERVICESCP']}"
            action="#{servicesSmsuController.enter}"
            rendered="#{servicesSmsuController.pageAuthorized}" />
        <e:menuItem id="gestionComptesImputation"
            value="#{msgs['NAVIGATION.TEXT.GESTIONCOMPTESIMPUTATION']}"
            rendered="false" />
        
        <e:menuItem id="about" value="#{msgs['NAVIGATION.TEXT.ABOUT']}"
            accesskey="#{msgs['NAVIGATION.ACCESSKEY.ABOUT']}"
            action="#{aboutController.enter}" />
