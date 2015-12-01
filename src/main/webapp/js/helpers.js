(function () {
"use strict";

var app = angular.module('myApp');

app.service('h', function (basicHelpers, restWsHelpers) {

var h = this;

angular.extend(this, basicHelpers);

// rename restWsHelpers methods for compat
this.callRest = restWsHelpers.simple;
this.callRestModify = restWsHelpers.action;

this.getGroups = function () {
    return h.callRest('groups');
};

this.getAccounts = function () {
    return h.callRest('groups/accounts');
};

this.getRoles = function () {
    return h.callRest('roles');
};

this.getTemplates = function () {
    return h.callRest('templates');
};

this.getTemplates = function () {
    return h.callRest('templates');
};

this.getServices = function () {
    return h.callRest('services');
};

this.getServicesAdh = function () {
    return h.callRest('services/adhFctn');
};

this.mayGetMsgStatuses = function (msg) {
    if (msg.stateMessage === "SENT") {
	h.callRest('messages/' + msg.id + '/statuses').then(function (statuses) {
	    msg.statuses = statuses;
	});
    }
};

this.searchUser = function (token, extraParams) {
    function hash2array(h) {
	return $.map(h, function (name, id) { 
	    return { id: id, name: name };
	});
    }

    if (token.length < 4) {
	return [];
    }

    var params = angular.extend({ token: token }, extraParams);
    return h.callRest('users/search', params);
};

this.searchGroup = function (token) {
    if (token.length < 3) {
	return [];
    }

    return h.callRest('groups/search', { token: token });
};

this.get_noSMS = function (user, extraParams) {
    var params = angular.extend({ id: user.id }, extraParams);
    h.callRest('users/search', params)
	.then(function (users) {
	    var id2users = h.array2hash(users, 'id');
	    user.noSMS = id2users[user.id] ? id2users[user.id].noSMS : true;
	    if (!user.noSMS) console.log(user.id + ' has no SMS');
	});
};

});

})();
