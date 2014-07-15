(function () {
"use strict";

var app = angular.module('myApp');

app.service('restWsHelpers', function ($http, $rootScope, globals, $q, $timeout, basicHelpers, login, loginSuccess, $location) {

// loginSuccess need restWsHelpers but it would create a circular deps, resolve it by hand:
loginSuccess.restWsHelpers = this;

getSessionIdOnStartup();

function getSessionIdOnStartup() {
    var sessionId = $location.search().sessionId;
    if (sessionId) {
	// save it then clean up URL
	$rootScope.sessionId = sessionId;
	$location.search('sessionId', null);
    }
}

function tryRelog() {

    function relogSuccess(loggedUser) {
	console.log('relog success');

	if ($rootScope.impersonatedUser) {
	    // relog does not use XHR, so X-Impersonate-User was not passed
	    // update loggedUser by using XHR
	    simple('login').then(loginSuccess.set);
	} else {
	    loginSuccess.set(loggedUser);
	}
	return null;
    }
    function queueXhrRequest() {
	console.log('queuing request');
	var deferred = $q.defer();
	login.windowOpenState.deferredQueue.push(deferred);
	return deferred.promise;
    }

    if (login.windowOpenState.deferredQueue) {
	return queueXhrRequest();
    }
    return login.jsonp().then(relogSuccess, function () {
	if (login.windowOpenState.deferredQueue) {
	    return queueXhrRequest();
	}
	console.log("jsonpLogin failed, going to windowOpenLogin");

	$rootScope.loggedUser = undefined; // hide app
	return login.windowOpen().then(relogSuccess, function (resp) {
	    console.log('relog failed');
	    console.log(resp);
	    alert("relog failed");
	    return $q.reject("needIframe");
	});
    });
}

function setHttpHeader(methods, name, val) {
    var headers = $http.defaults.headers;
    angular.forEach(methods, function (method) {
	if (!headers[method]) headers[method] = {};
	headers[method][name] = val;
    });
}

var alerted = {};
function alertOnce(msg, timeout) {
    if (alerted[msg]) return;
    alert(msg);
    alerted[msg] = 1;
    if (timeout > 0) {
       $timeout(function () { delete alerted[msg]; }, timeout);
    }
}

function xhrRequest(args, flags) {
    var onError401 = function (resp) {
	if (flags.justSuccessfullyLogged) {
	    if (!flags.cookiesRejected && $rootScope.sessionId) {
		console.log("Race? Our request was done without flag cookiesRejected. Retrying with jsessionid in request");
		return xhrRequest(args, flags);
	    } else {	
		alert("FATAL : both cookies and URL parameter jsessionid are rejected");
		return $q.reject(resp);
	    }
	}
	return tryRelog().then(function () { 
	    return xhrRequest(args, { justSuccessfullyLogged: true });
	});
    };
    var onErrorCsrf = function (resp, err) {
	if (flags.xhrRequestInvalidCsrfState) {
	    alert("Invalid CRSF prevention token failed twice");
	    return $q.reject(resp);
	}
	setHttpHeader(['post','put','delete'], "X-CSRF-TOKEN", err.token);
	console.log("retrying with new CSRF token");
	return xhrRequest(args, { xhrRequestInvalidCsrfState: true });
    };
    var onErrorFromJson = function(resp, err) {
	if (err.error === "Invalid CRSF prevention token")
	    return onErrorCsrf(resp, err);
	else {
	    alert(err.error);
	    return $q.reject(err);
	}
    };
    var onError = function(resp) {
	var status = resp.status;
	if (status === 0) {
	    alert("unknown failure (server seems to be down)");
	    return $q.reject(resp);
	} else if (status === 503) {
            alertOnce("Le serveur est en maintenance, veuillez ré-essayer ultérieurement", 2000);
            return $q.reject(resp);         
	} else if (status === 401) {
	    return onError401(resp);
	} else if (resp.data) {
	    var err = basicHelpers.fromJsonOrNull(resp.data);
	    if (err && err.error)
		return onErrorFromJson(resp, err);

	}
	alert("unknown error " + status);
	return $q.reject(resp);
    };
    if ($rootScope.sessionId && !flags.cookiesRejected) {
	flags.cookiesRejected = true;
	args = angular.copy(args);
	args.url = args.url + ";jsessionid=" + $rootScope.sessionId;
    }
    return $http(args).then(function (resp) {
	return resp;
    }, onError);
}

function headers() {
    var r = {};
    if ($rootScope.impersonatedUser) {
	r["X-Impersonate-User"] = $rootScope.impersonatedUser;
    }
    return r;
}

function simple($function, params) {
    var url = globals.baseURL + '/rest/' + $function;
    params = angular.extend({}, params);
    params.cacheSlayer = new Date().getTime(); // for our beloved IE which caches every AJAX... ( http://stackoverflow.com/questions/16098430/angular-ie-caching-issue-for-http )
    var args = { method: 'get', url: url, params: params, headers: headers() };
    return xhrRequest(args, {}).then(function(resp) {
	return resp.data;
    });
};

function action(method, restPath, o) {
    var args = { method: method, url: globals.baseURL + '/rest/' + restPath, headers: headers() };
    if (method !== 'post') {
	var id = o.id;
	if (typeof id === "undefined") return alert("internal error: missing id");
	delete o.id;
	args.url += '/'+id;
    }
    if (method !== 'delete') {
	args.data = o;
    }
    return xhrRequest(args, {});
};

this.simple = simple;
this.action = action;

});

})();