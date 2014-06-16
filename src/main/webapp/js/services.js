(function () {
"use strict";

function computeRoutes(baseURL) {
  var templatesBaseURL = baseURL + "/partials";
  var l =
    [{ route: '/welcome', mainText: "Accueil", controller: 'WelcomeCtrl' }, 
     { route: '/membership', mainText: "Adhésion", controller: 'MembershipCtrl'},
     { route: '/send', mainText: "Envoi SMS", show: 'loggedUser.can.FCTN_SMS_ENVOI_ADH || loggedUser.can.FCTN_SMS_ENVOI_GROUPES || loggedUser.can.FCTN_SMS_ENVOI_NUM_TEL || loggedUser.can.FCTN_SMS_ENVOI_LISTE_NUM_TEL || loggedUser.can.FCTN_SMS_REQ_LDAP_ADH', controller: 'SendCtrl'},
     { route: '/messages', mainText: "Suivi des envois", show: 'loggedUser.can.FCTN_SUIVI_ENVOIS_UTIL || loggedUser.can.FCTN_SUIVI_ENVOIS_ETABL', controller: 'MessagesCtrl'},
     { route: '/approvals', mainText: "Approbation des envois", show: 'loggedUser.can.APPROBATION_ENVOI', controller: 'ApprovalsCtrl'},
     { route: '/templates', mainText: "Modèles", show: 'loggedUser.can.FCTN_GESTION_MODELES', controller: 'TemplatesCtrl'},
     { route: '/roles', mainText: "Rôles", show: 'loggedUser.can.FCTN_GESTION_ROLES_CRUD', controller: 'RolesCtrl'},
     { route: '/groups', mainText: "Groupes", show: 'loggedUser.can.FCTN_GESTION_GROUPE', controller: 'GroupsCtrl'},
     { route: '/services', mainText: "Thèmes/partenaires", show: 'loggedUser.can.FCTN_GESTION_SERVICES_CP', controller: 'ServicesCtrl'},
     { route: '/logout', mainText: "Déconnexion", show: 'allowLogout', controller: 'EmptyCtrl' },
     { route: '/about', mainText: "A propos de", title: "A propos de SMSU-U", controller: 'EmptyCtrl'},
     { route: '/messages/:id', text: "Détail", parent: '/messages', controller: 'MessagesDetailCtrl', templateUrl: templatesBaseURL + '/messages-detail.html'},
     { route: '/templates/:id', parent: '/templates', controller: 'TemplatesDetailCtrl', templateUrl: templatesBaseURL + '/templates-detail.html'},
     { route: '/roles/:id', parent: '/roles', controller: 'RolesDetailCtrl', templateUrl: templatesBaseURL + '/roles-detail.html'},
     { route: '/groups/:id', parent: '/groups', controller: 'GroupsDetailCtrl', templateUrl: templatesBaseURL + '/groups-detail.html'},
     { route: '/services/:id', parent: '/services', controller: 'ServicesDetailCtrl', templateUrl: templatesBaseURL + '/services-detail.html'}];
  angular.forEach(l, function (tab) {
    if (!tab.templateUrl) tab.templateUrl = templatesBaseURL + tab.route + '.html';
  });
  return l;
}

var app = angular.module('myApp');

app.provider('routes', function () {
    this.routes = [];
    this.computeRoutes = computeRoutes;
    this.$get = function () {
	return { routes: this.routes };
    };
});

app.provider('globals', function () {
    if (!document.esupSmsu) alert("missing configuration document.esupSmsu");
    var globals = document.esupSmsu;
    this.baseURL = globals.baseURL;
    this.isWebWidget = globals.isWebWidget;
    this.wsgroupsURL = globals.wsgroupsURL;

    this.$get = function () { 
	return this;
    };
});

}());