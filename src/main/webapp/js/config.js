(function () {
"use strict";

var app = angular.module('myApp');

app.config(function($routeProvider, routesProvider, globalsProvider) {
    routesProvider.routes = routesProvider.computeRoutes(globalsProvider.baseURL);
    angular.forEach(routesProvider.routes, function (tab) {
	if (tab.controller) $routeProvider.when(tab.route, {templateUrl: tab.templateUrl, controller: tab.controller});
    });
    $routeProvider.otherwise({redirectTo: '/welcome'});
});

app.config(function($httpProvider) {
    $httpProvider.defaults.withCredentials = true;
});

})();
