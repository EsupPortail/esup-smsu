function debug_msg(msg) {
    $("#debug").html($("#debug").html() + "<br>" + msg);
};

(function () {
"use strict";

var app = angular.module('myApp');

app.service('h', function ($http, $rootScope, routes, globals, $q, $timeout, $window) {

var h = this;

this.objectKeys = function (o) {
    return $.map(o, function (l, k) { return k; });
};
this.objectValues = function (o) {
    return $.map(o, function (l, k) { return l; });
};
this.objectSlice = function (o, fields) {
    var r = {};
    angular.forEach(fields, function (field) {
	if (field in o)
	    r[field] = o[field];
    });
    return r;
};
this.array2hash = function (array, field) {
    var h = {};
    angular.forEach(array, function (e) {
	h[e[field]] = e;
    });
    return h;
};
this.array2hashMulti = function (array, field) {
    var h = {};
    angular.forEach(array, function (e) {
	var k = e[field];
	(h[k] = h[k] || []).push(e);
    });
    return h;
};
this.array_map = function (array, f) {
    var r = [];
    angular.forEach(array, function (e) {
	r.push(f(e)); 
    });
    return r;
};
this.simpleFilter = function (array, f) {
    var r = [];
    angular.forEach(array, function (e) {
	if (f(e)) r.push(e);
    });
    return r;
};
this.simpleFind = function (array, f) {
    var r;
    angular.forEach(array, function (e) {
	if (f(e)) r = e; 
    });
    return r;
};
this.uniqWith = function (array, f) {
    var o = {};
    angular.forEach(array, function (e) {
	var k = f(e);
	if (!(k in o)) o[k] = e;
    });
    return h.objectValues(o);
};
this.array_remove_elt = function (array, searchElement) {
    var i, length = array.length;
    for (i = 0; i < length; i++) {
      if (array[i] === searchElement) {
	  array.splice(i, 1);
          return;
      }
    }
};

this.array2set = function (array) {
    var set = {}
    angular.forEach(array, function (e) { set[e] = true; });
    return set;
};
this.set2array = function (set) {
    var array = [];
    angular.forEach(set, function (bool, e) {
	if (bool) array.push(e);
    });
    return array;
};
this.array_difference = function (array1, array2) {
    var set2 = this.array2set(array2);
    return this.simpleFilter(array1, function (e) { return !(e in set2); });
};
this.array_intersection = function (array1, array2) {
    var set2 = this.array2set(array2);
    return this.simpleFilter(array1, function (e) { return (e in set2); });
};

this.jsonpLogin = function () {
    console.log("jsonpLogin start");
    return $http.jsonp(globals.baseURL + '/rest/login?callback=JSON_CALLBACK').then(function (resp) {
	console.log("jsonpLogin ok");
	return resp.data;
    });
};

function windowOpenLoginDivCreate() {
    var elt = angular.element('<div>', {'class': 'windowOpenLoginDiv alert alert-warning'});
    elt.html('Votre session a expiré. Veuillez vous identifier à nouveau. <span class="glyphicon glyphicon-log-in"></span>');
    angular.element('.myAppDiv').prepend(elt);
    return elt;
}
function windowOpenLoginOnMessage(state) {
    var onmessage = function(e) {
	if (typeof e.data !== "string") return;
	var m = e.data.match(/^loggedUser=(.*)$/);
	if (!m) return;

	windowOpenLoginCleanup(state);
	$rootScope.$apply(function () { 
	    state.deferred.resolve(angular.fromJson(m[1]));
	});
    };
    $window.addEventListener("message", onmessage);  
    return onmessage;
}
var windowOpenLoginState = {}
function windowOpenLoginCleanup(state) {
    try {
	if (state.div) state.div.remove();
	if (state.listener) $window.removeEventListener("message", state.listener);  
	if (state.window) state.window.close(); 
    } catch (e) {}
    windowOpenLoginState = {};
};
this.windowOpenLogin = function () {
    $rootScope.loggedUser = undefined; // hide app

    windowOpenLoginCleanup(windowOpenLoginState);
    var state = windowOpenLoginState = {};

    state.deferred = $q.defer();
    state.div = windowOpenLoginDivCreate();
    state.div.bind("click", function () {
	state.listener = windowOpenLoginOnMessage(state); 
	state.window = $window.open(globals.baseURL + '/rest/login');
    });
    return state.deferred.promise;
};

this.jsonpFallbackWindowOpenLogin = function () {
    return h.jsonpLogin().then(null, function () {
	console.log("jsonpLogin failed, trying windowOpenLogin");
	return h.windowOpenLogin();
    });
};

this.setLoggedUser = function (loggedUser) {
    console.log('user logged in: ' + loggedUser.id);

    $rootScope.loggedUser = h.userWithCapabilities(loggedUser);
};

function fromJsonOrNull(json) {
    try {
	return angular.fromJson(json);
    } catch (e) {
	return null;
    }
}

function setHttpHeader(methods, name, val) {
    var headers = $http.defaults.headers;
    angular.forEach(methods, function (method) {
	if (!headers[method]) headers[method] = {};
	headers[method][name] = val;
    });
}

var xhrRequest401State = false;
var xhrRequestInvalidCsrfState = false;
function xhrRequest(args) {
    var onError401 = function (resp) {
	if (xhrRequest401State) {
	    alert("fatal, relog failed");
	    return $q.reject(resp);
	}
	xhrRequest401State = true;
	return h.jsonpFallbackWindowOpenLogin().then(function (loggedUser) {
	    console.log('relog success');
	    h.setLoggedUser(loggedUser);
	    return xhrRequest(args);
	}, function (resp) {
	    console.log('relog failed');
	    console.log(resp);
	    alert("relog failed");
	    return $q.reject("needIframe");
	});
    };
    var onErrorCsrf = function (resp, err) {
	if (xhrRequestInvalidCsrfState) {
	    alert("Invalid CRSF prevention token failed twice");
	    return $q.reject(resp);
	}
	xhrRequestInvalidCsrfState = true;
	setHttpHeader(['post','put','delete'], "X-CSRF-TOKEN", err.token);
	console.log("retrying with new CSRF token");
	return xhrRequest(args);
    };
    var onErrorFromJson = function(resp, err) {
	if (err.error === "Invalid CRSF prevention token")
	    return onErrorCsrf(resp, err);
	else {
	    alert(err.error);
	    return $q.reject(resp);
	}
    };
    var onError = function(resp) {
	var status = resp.status;
	if (status == 0) {
	    alert("unknown failure (server seems to be down)");
	    return $q.reject(resp);
	} else if (status == 401) {
	    return onError401(resp);
	} else if (resp.data) {
	    var err = fromJsonOrNull(resp.data);
	    if (err && err.error)
		return onErrorFromJson(resp, err);

	}
	alert("unknown error " + status);
	return $q.reject(resp);
    };
    return $http(args).then(function (resp) {
	if (xhrRequest401State) { console.log('rest after relog success'); console.log(resp); }
	xhrRequest401State = false;
	xhrRequestInvalidCsrfState = false;
	return resp;
    }, onError);
}

this.callRest = function ($function, params) {
    var url = globals.baseURL + '/rest/' + $function;
    params = angular.extend({}, params);
    params.cacheSlayer = new Date().getTime(); // for our beloved IE which caches every AJAX... ( http://stackoverflow.com/questions/16098430/angular-ie-caching-issue-for-http )
    var args = { method: 'get', url: url, params: params };
    return xhrRequest(args).then(function(resp) {
	return resp.data;
    });
};

this.callRestModify = function (method, restPath, o) {
    var args = { method: method, url: globals.baseURL + '/rest/' + restPath };
    if (method !== 'post') {
	var id = o.id;
	if (typeof id === "undefined") return alert("internal error: missing id");
	delete o.id;
	args.url += '/'+id;
    }
    if (method !== 'delete') {
	args.data = o;
    }
    return xhrRequest(args);
};

this.userWithCapabilities = function (user) {
    user.can = {};
    angular.forEach(user.rights || [], function (c) {
	user.can[c] = true;
    });
    return user;
};

this.findCurrentTab = function ($scope, templateUrl) {
    var tab = h.simpleFind(routes.routes, function (tab) { return tab.templateUrl == templateUrl; });
    if (!tab) return;
    var mainTab;
    if (tab.parent) {
	mainTab = h.simpleFind(routes.routes, function (mainTab) { return mainTab.route == tab.parent; });
    } else {
	mainTab = tab;
    }
    $scope.currentMainTab = mainTab;
    $scope.currentTab = tab;
};

this.getTemplateUrl = function (basename) {
    return globals.baseURL + '/partials/' + basename;
};

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

this.mayGetMsgStatuses = function (msg) {
    if (msg.stateMessage === "SENT") {
	h.callRest('messages/' + msg.id + '/statuses').then(function (statuses) {
	    msg.statuses = statuses;
	});
    }
};

});

})();
