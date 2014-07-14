(function () {
"use strict";

var app = angular.module('myApp', ['ngGrid', 'ngRoute',
				   'pascalprecht.translate', 'ui.bootstrap']);

app.provider('globals', function () {
    if (!document.esupSmsu) alert("missing configuration document.esupSmsu");
    angular.extend(this, document.esupSmsu);
    this.$get = function () { return this; };
});

})();
