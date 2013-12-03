(function () {
"use strict";

var app = angular.module('myApp');

app.controller('MainCtrl', function($scope, h, $route, $parse, $location, routes) {

    $scope.isPortlet = true;
    //if ($scope.isPortlet) h.cleanupUportalCss();

    //if (!$route.current) $location.path('/welcome');

    $scope.$watch('loggedUser', function () {
	$scope.mainVisibleTabs = $.grep(routes.routes, function(e) { 
	    return e.mainText && (!e.show || $parse(e.show)($scope));
	});
    });

    h.jsonpFallbackWindowOpenLogin().then(h.setLoggedUser);

    $scope.$on('$locationChangeSuccess', function(){
	h.findCurrentTab($scope, $route.current.templateUrl);
	$scope.forceSidenav = $scope.currentTab && $scope.currentTab.route === "/welcome";
    });

    $scope.$on('$routeChangeStart', function () {
	$scope.loadInProgress = true;
    });   
    $scope.$on('$routeChangeError', function () {
	$scope.loadInProgress = false;
    });
    $scope.$on('$routeChangeSuccess', function(){
	$scope.loadInProgress = false;
    });
});


app.controller('EmptyCtrl', function($scope) {});

app.controller('UsersCtrl', function($scope, h) {
    h.getUsers($scope.roles).then(function (users) {
	$scope.users = users;
    });
});

app.controller('UsersDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.user && $scope.user.login || (id === 'new' ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('user.login', updateCurrentTabTitle);

    $scope.checkUniqueName = function (name) {
	var user = $scope.login2user[name];
	return !user || user === $scope.user;
    };

    var modify = function (method) {
	var user = $scope.user;
	user = { id: user.id, login: user.login, role: user.role }; // keep only modifiable fields
	h.callRestModify(method, 'users', user).then(function () {
	    $location.path('/users');
	});
    };
    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	modify($scope.user.isNew ? 'post' : 'put');
    };
    $scope["delete"] = function () {
	modify('delete');
    };

    h.getUsers($scope.roles).then(function (users) {
	$scope.login2user = h.array2hash(h.objectValues(users), 'login');
	if (id === "new") {
	    $scope.user = { isNew: true };
	} else if (id in users) {
	    $scope.user = users[id];
	} else {
	    alert("invalid application " + id);
	}
    });	
});

app.controller('ApplicationsDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;

    h.getAccounts().then(function (list) {
	$scope.accounts = list;
    });	

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.app && $scope.app.name || (id === 'new' ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('app.name', updateCurrentTabTitle);

    $scope.checkUniqueName = function (name) {
	var app = $scope.name2app[name];
	return !app || app === $scope.app;
    };

    var modify = function (method, then) {
	var app = angular.copy($scope.app);
	delete app.isNew;
	h.callRestModify(method, 'applications', app).then(function () {
	    $location.url(then || '/applications');
	});
    };    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	var method = $scope.app.isNew ? 'post' : 'put';
	if ($scope.app.accountName == null) {
	    // must first create the account
	    var accountNameSuggestion = $scope.app.name;
	    h.createEmptyAccount(accountNameSuggestion, $scope.accounts).then(function (account) {
		$scope.app.accountName = account.name;
  		modify(method, '/accounts/' + account.id + '?isNew');
	    });
	} else {
	    modify(method);
	}
    };
    $scope["delete"] = function () {
	modify('delete');
    };

    h.getApplications().then(function (applications) {
	$scope.name2app = h.array2hash(applications, 'name');

	if (id === "new") {
	    $scope.app = { isNew: true, accountName: null, quota: 0 };
	} else {
	    var id2app = h.array2hash(applications, 'id');

	    if (id in id2app) {
		$scope.app = id2app[id];
	    } else {
		alert("invalid application " + id);
	    }
	}
    });	
});

app.controller('AccountsDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;
    $scope.isNew = $routeParams.isNew;

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.account && $scope.account.name || ($scope.isNew ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('account.name', updateCurrentTabTitle);

    $scope.checkUniqueName = function (name) {
	var account = $scope.name2account[name];
	return !account || account === $scope.account;
    };

    var modify = function (method) {
	var account = angular.copy($scope.account);
	h.callRestModify(method, 'accounts', account).then(function () {
	    $location.path('/accounts');
	});
    };    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	modify('put');
    };
    $scope.disable = function () {
	$scope.account.quota = 0;
	modify('put');	
    };

    h.getAccounts().then(function (accounts) {
	$scope.name2account = h.array2hash(accounts, 'name');
	var id2account = h.array2hash(accounts, 'id');

	if (id in id2account) {
	    $scope.accounts = accounts;
	    $scope.account = id2account[id];
	    $scope.appDisabled = $scope.account.quota == 0;
	} else {
	    alert("invalid account " + id);
	}
    });	
});

app.controller('AccountsCtrl', function($scope, h) {
    h.getAccounts().then(function(list) {
	$scope.accounts = list;
	// tell ng-grid:
        if (!$scope.$$phase) $scope.$apply();
    });	
    $scope.warnConsumedRatio = 0.9;
    $scope.consumedRatio = function (account) {
	return account.consumedSms / account.quota;
    };
    $scope.gridOptions = { data: 'accounts',
			   sortInfo: {fields: ['name'], directions: ['asc', 'desc']},
			   headerRowHeight: '50',
			   multiSelect: false,
			   columnDefs: [{field: 'name', displayName:"Compte d'imputation", width: '****', 
					   cellTemplate: '<div class="ngCellText"><a href="#/accounts/{{row.entity.id}}">{{row.getProperty(col.field)}}</a></div>'},
					{field: 'quota', displayName: 'Quota', width: '**'},
					{field: 'consumedSms', displayName: 'Nombre de SMS consommés', width: '**',
					 cellTemplate: '<div ng-class="{highConsumedRatio: consumedRatio(row.entity) > warnConsumedRatio}"><div class="ngCellText">{{row.entity.consumedSms}}</div></div>'
					}]
			 };

});

app.controller('ApplicationsCtrl', function($scope, h) {
    h.getApplications().then(function(list) {
	$scope.applications = list;
	// tell ng-grid:
        if (!$scope.$$phase) $scope.$apply();
    });	
    $scope.warnConsumedRatio = 0.9;
    $scope.consumedRatio = function (account) {
	return account.consumedSms / account.quota;
    };
    $scope.gridOptions = { data: 'applications',
			   sortInfo: {fields: ['name'], directions: ['asc', 'desc']},
			   headerRowHeight: '50',
			   multiSelect: false,
			   columnDefs: [{field: 'name', displayName:"Application", width: '***', 
					   cellTemplate: '<div class="ngCellText"><a href="#/applications/{{row.entity.id}}">{{row.getProperty(col.field)}}</a></div>'},
					{field: 'quota', displayName: 'Quota', width: '*'},
					{field: 'consumedSms', displayName: 'Nombre de \nSMS consommés', width: '*',
					 cellTemplate: '<div ng-class="{highConsumedRatio: consumedRatio(row.entity) > warnConsumedRatio}"><div class="ngCellText">{{row.entity.consumedSms}}</div></div>'
					} ]
			 };

});

app.controller('ConsolidatedSummaryCtrl', function($scope, h) {
    var computeTree = function (flatList) {
	var tree = {};
	angular.forEach(flatList, function (e) {
	    var key1 = e.institution;
	    var key2 = e.app + "+" + e.account;
	    if (!tree[key1]) 
		tree[key1] = {};
	    if (!tree[key1][key2]) 
		tree[key1][key2] = $.extend({ data: [] }, h.objectSlice(e, ['institution', 'app', 'account']));
	    tree[key1][key2].data.unshift(e);
	});
	angular.forEach(tree, function (subtree, key1) {
	    tree[key1] = $.map(h.objectKeys(subtree).sort(), function (k) { return subtree[k]; });
	});
	return tree;
    };

    h.callRest('summary/consolidated').then(function(flatList) {
	angular.forEach(flatList, function (e) {
	    $.extend(e, h.getInstAppAccount(e));
	    e.nbReceived = e.nbSendedSMS - e.nbSMSInError;
	    e.failureRate = Math.round(e.nbSMSInError / e.nbSendedSMS * 100) + "%";
	});
	$scope.appAccountsTree = computeTree(flatList);
    });

    $scope.setAppAccount = function(e) {
	$scope.appAccount = e;
    };
});

app.controller('DetailedSummaryCtrl', function($scope, h, $location, $route) {
    $scope.initialNbResults = 50;
    $scope.nbResults = $scope.initialNbResults;
    $scope.accountFilter = $location.search();

    $scope.setAppAccount = function (e) {
	e = h.objectSlice(e, ['institution', 'app', 'account']); // all but hashKey
	$location.search(e);
	$route.reload();
    };

    h.callRest('summary/detailed/criteria').then(function(flatList) {
	flatList = h.array_map(flatList, function (e) {
	    return h.getInstAppAccount(e);
	});
	$scope.appAccountsTree = h.array2hashMulti(flatList, 'institution');
    });

    $scope.inProgress = false;

    var nbSmsAndDetails = function (e) {
	var details = [];
	if (e.nbInProgress > 0) details.push(e.nbInProgress + " en cours");
	if (e.errors > 0) details.push(e.errors + " " + (e.errors > 1 ? "erreurs" : "erreur"));
	return e.nbSms + (details.length ? " dont " + details.join(" et ") : "");
    };

    var computeGroupedByRaw = function(flatList) {
	var groupedBy = [];
	var currentKey = null;
	var current;
	for (var i = 0; i < $scope.nbResults; i++) {
	    var e = flatList[i];
	    if (!e) break;
	    var key = e.appName + "+" + e.accountName;
	    if (currentKey !== key) {
		currentKey = key;
		current = $.extend({ list: [] }, h.getInstAppAccount(e));
		groupedBy.push(current);
	    }
	    current.list.push({ date: new Date(e.date), nbSmsAndDetails: nbSmsAndDetails(e) });
	}
	return groupedBy;
    };

    var computeGroupedBy = function() {
	if ($scope.inProgress) return;
	$scope.inProgress = true;
	var fullFilter = angular.extend({ maxResults: $scope.nbResults }, $scope.accountFilter);
	h.callRest('summary/detailed', fullFilter)
	    .then(function (flatList) {
		$scope.noMoreResults = flatList.length < fullFilter.maxResults;
		$scope.groupedBy = computeGroupedByRaw(flatList);
		$scope.inProgress = false;
	    }, function (resp) {
		// an error occured.
		if (resp && resp.status == 404 && $scope.accountFilter.account) {
		    // try removing accountFilter
		    $scope.setAppAccount({});
		}
	    });
    };

    $scope.showMoreResults = function () {
	if ($scope.noMoreResults) return;
	$scope.nbResults = $scope.nbResults + $scope.initialNbResults;
	computeGroupedBy();
    };

    computeGroupedBy();
});

})();
