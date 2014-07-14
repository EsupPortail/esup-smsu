(function () {
"use strict";

var app = angular.module('myApp');

app.service('loginSuccess', function ($rootScope) {

this.set = function (loggedUser) {
    console.log('user logged in: ' + loggedUser.id);

    $rootScope.sessionId = loggedUser.sessionId;
    delete loggedUser.sessionId;
    $rootScope.loggedUser = userWithCapabilities(loggedUser);
};

function userWithCapabilities(user) {
    user.can = {};
    angular.forEach(user.rights || [], function (c) {
	user.can[c] = true;
    });
    return user;
};

});

})();