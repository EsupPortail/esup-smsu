(function () {
"use strict";

function computeRoutes(baseURL) {
  var templatesBaseURL = baseURL + "/partials";
  var l =
    [{ route: '/welcome', mainText: "Accueil", controller: 'EmptyCtrl' }, 
     { route: '/applications', mainText: "Applications clientes", show: 'loggedUser.can.FCTN_API_CONFIG_APPLIS', controller: 'ApplicationsCtrl'},
     { route: '/accounts', mainText: "Comptes d'imputation", show: 'loggedUser.can.FCTN_GESTION_CPT_IMPUT', controller: 'AccountsCtrl'},
     { route: '/consolidatedSummary', mainText: "Relevé consolidé", show: 'loggedUser.can.FCTN_API_EDITION_RAPPORT', controller: 'ConsolidatedSummaryCtrl' },
     { route: '/detailedSummary', mainText: "Relevé détaillé", show: 'loggedUser.can.FCTN_API_EDITION_RAPPORT', controller: 'DetailedSummaryCtrl' },
     { route: '/users', mainText: "Gestion des utilisateurs", show: 'loggedUser.can.FCTN_MANAGE_USERS', controller: 'UsersCtrl' },
     { route: '/logout', mainText: "Déconnexion", show: '!isPortlet', controller: 'EmptyCtrl' },
     { route: '/about', mainText: "A propos de", title: "A propos de SMSU-U", controller: 'EmptyCtrl'},
     { route: '/users/:id', parent: '/users', controller: 'UsersDetailCtrl', templateUrl: templatesBaseURL + '/users-detail.html'},
     { route: '/applications/:id', parent: '/applications', controller: 'ApplicationsDetailCtrl', templateUrl: templatesBaseURL + '/applications-detail.html'},
     { route: '/accounts/:id', parent: '/accounts', controller: 'AccountsDetailCtrl', templateUrl: templatesBaseURL + '/accounts-detail.html'}];
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
    if (!document.esupSmsuApiAdmin) alert("missing configuration document.esupSmsuApiAdmin");
    var globals = document.esupSmsuApiAdmin;
    this.baseURL = globals.baseURL;

    this.$get = function () { 
	return this;
    };
});

}());