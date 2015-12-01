(function () {
"use strict";

var app = angular.module('myApp');

var phoneNumberPatternOne = /^0[67]\d{8}$/;
var phoneNumberPatternAll = /\b0[67]\d{8}\b/g;

app.filter('array_difference', function (h) {
    return function (arr1, arr2) { 
	return h.array_difference(arr1, arr2);
    };
});

app.controller('MainCtrl', function($scope, h, $route, $parse, routes, globals, restWsHelpers) {

    $scope.allowLogout = false;//!globals.isWebWidget;
    $scope.wsgroupsURL = globals.wsgroupsURL;

    $scope.$watch('loggedUser', function () {
	$scope.mainVisibleTabs = $.grep(routes.routes, function(e) { 
	    return e.mainText && (!e.show || $parse(e.show)($scope));
	});
    });

    restWsHelpers.initialLogin();

    $scope.$on('$locationChangeSuccess', function(){
	routes.findCurrentTab($scope, $route.current.templateUrl);
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

app.controller('WelcomeCtrl', function($scope, $rootScope, h) {
    $scope.searchUser = h.searchUser;
    $scope.wip = {};
    $scope.impersonate = function (user) {
	$rootScope.impersonatedUser = user;
	restWsHelpers.simpleLogin();
    };
});

app.controller('GroupsDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;

    var allowSendFonctions = [
	'FCTN_SMS_ENVOI_ADH',
	'FCTN_SMS_ENVOI_GROUPES',
	'FCTN_SMS_ENVOI_NUM_TEL',
	'FCTN_SMS_ENVOI_LISTE_NUM_TEL',
	'FCTN_SMS_REQ_LDAP_ADH',
	'FCTN_SMS_ENVOI_SERVICE_CP' ];

    $scope.wip = {};
    $scope.groupOrUserChoices = [{ key:"group", label:"Groupe" },
				 { key:"user", label: "Utilisateur" }];

    h.getAccounts().then(function (list) {
	$scope.accounts = list;
    });

    h.getRoles().then(function (roles) {
	angular.forEach(roles, function (role) {
	    role.allowSend = h.array_intersection(allowSendFonctions, role.fonctions).length;
	});
	$scope.roles = h.array2hash(roles, 'name');
    });

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.group && $scope.group.label || (id === 'new' ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('group.label', updateCurrentTabTitle);

    $scope.searchUser = h.searchUser;
    $scope.searchGroup = h.searchGroup;

    $scope.setLabel = function () {
	var e = $scope.wip.label;
	$scope.group.label = e.value || e.id;
	$scope.group.displayName = e.label || e.name;
	$scope.myForm.label.$setValidity('unique', $scope.checkUniqueLabel(e.id));
    };

    $scope.checkUniqueLabel = function (label) {
	var group = $scope.label2group[label];
	return !group || group === $scope.group;
    };
    $scope.addSupervisor = function () {
	var e = $scope.wip.supervisor;
	$scope.wip.supervisor = null;
	$scope.group.supervisors[e.value || e.id] = e.label || e.name;
    };
    $scope.removeSupervisor = function (id) {
	delete $scope.group.supervisors[id];
    };

    var modify = function (method, then) {
	var group = angular.copy($scope.group);

	if (group.isNew && !group.account) {
	    // this must be a "Destination Group"
	    // a valid account is needed, choose one:
	    group.account = $scope.accounts[0];
	}
	delete group.isNew;
	h.callRestModify(method, 'groups', group).then(function () {
	    $location.url(then || '/groups');
	});
    };    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	var method = $scope.group.isNew ? 'post' : 'put';
	modify(method);
    };
    $scope["delete"] = function () {
	modify('delete');
    };

    h.getGroups().then(function (groups) {
	$scope.label2group = h.array2hash(groups, 'label');

	if (id === "new") {
	    $scope.group = { isNew: true, quotaSms: 0, supervisors: {} };
	    $scope.groupOrUser = 'group';
	} else {
	    var id2group = h.array2hash(groups, 'id');

	    if (id in id2group) {
		$scope.group = id2group[id];
		$scope.groupOrUser = $scope.group.labelIsUserId ? 'user' : 'group';
		$scope.wip.label = $scope.group.displayName;
	    } else {
		alert("invalid group " + id);
	    }
	}
    });	
});

app.controller('GroupsCtrl', function($scope, h) {
    function setGroups(groups, name2role) {
	$scope.groupsDestinations = [];
	$scope.groups = h.simpleFilter(groups, function (group) {
	    if (group.quotaSms > 0 ||
		name2role[group.role].fonctions.length > 0) {
		return true;
	    } else {
		$scope.groupsDestinations.push(group);
		return false;
	    }
	});
    }

    h.getRoles().then(function (roles) {
	var name2role = h.array2hash(roles, 'name');
	h.getGroups().then(function(groups) {
	    setGroups(groups, name2role);
	    // tell ng-grid:
            if (!$scope.$$phase) $scope.$apply();
	});
    });
    $scope.warnConsumedRatio = 0.9;
    $scope.consumedRatio = function (e) {
	return e.consumedSms / e.quota;
    };
    $scope.gridOptions = { data: 'groups',
			   sortInfo: {fields: ['label'], directions: ['asc', 'desc']},
			   headerRowHeight: '50',
			   multiSelect: false,
			   columnDefs: [{field: 'displayName', displayName:"Group", width: '***', 
					   cellTemplate: '<div class="ngCellText"><a href="#/groups/{{row.entity.id}}">{{row.getProperty(col.field)}}</a></div>'},
					{field: 'role', displayName: 'Rôle', width: '*'},
					{field: 'quotaSms', displayName: 'Quota', width: '*'},
					{field: 'consumedSms', displayName: 'Nombre de \nSMS consommés', width: '*',
					 cellTemplate: '<div ng-class="{highConsumedRatio: consumedRatio(row.entity) > warnConsumedRatio}"><div class="ngCellText">{{row.entity.consumedSms}}</div></div>'
					} ]
			 };

});

app.controller('TemplatesCtrl', function($scope, h) {
    h.getTemplates().then(function (templates) {
	$scope.templates = templates;
    });
});

app.controller('TemplatesDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.template && $scope.template.label || (id === 'new' ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('template.label', updateCurrentTabTitle);

    $scope.checkUniqueName = function (name) {
	var template = $scope.label2template[name];
	return !template || template === $scope.template;
    };

    var modify = function (method) {
	var template = angular.copy($scope.template);
	delete template.isNew;
	h.callRestModify(method, 'templates', template).then(function () {
	    $location.path('/templates');
	});
    };
    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	modify($scope.template.isNew ? 'post' : 'put');
    };
    $scope["delete"] = function () {
	modify('delete');
    };

    h.getTemplates().then(function (templates) {
	$scope.label2template = h.array2hash(h.objectValues(templates), 'label');
	if (id === "new") {
	    $scope.template = { isNew: true, body: "", heading: "", signature: "" };
	} else {
	    var id2template = h.array2hash(templates, 'id');
	    if (id in id2template) {
		$scope.template = id2template[id];
	    } else {
		alert("invalid template " + id);
	    }
	}
    });	
});

app.controller('RolesCtrl', function($scope, h) {
    h.getRoles().then(function (roles) {
	$scope.roles = roles;
    });
});

app.controller('RolesDetailCtrl', function($scope, h, $routeParams, $location, $translate, $filter) {
    var id = $routeParams.id;
    var fonction2text;

    $scope.forOrdering = function (fonction) { return fonction2text && fonction2text[fonction]; };
    $scope.fonctionText = function (fonction) {
	return fonction2text[fonction]
		.replace(/^\d+ /, '')
		.replace('FCTN_SMS_ENVOI_SERVICE_CG', 'Envoi SMS en utilisant \'Aucun\' service')
		.replace('FCTN_SMS_ENVOI_SERVICE_', 'Envoi SMS au service : ')
		.replace('FCTN_SMS_ADHESION_SERVICE_', 'Adhésion au service : ');
    };

    h.callRest('roles/fonctions').then(function (list) {
	$scope.allFonctions = list;
	$translate(list).then(function (h) { fonction2text = h; });
    });

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.role && $scope.role.name || (id === 'new' ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('role.name', updateCurrentTabTitle);

    $scope.checkUniqueName = function (name) {
	var role = $scope.name2role[name];
	return !role || role === $scope.role;
    };

    function updateAvailableFonctions() {
	$scope.availableFonctions = h.array_difference($scope.allFonctions, $scope.role.fonctions);
    }
    $scope.removeFonction = function (fonction) {
	h.array_remove_elt($scope.role.fonctions, fonction);
	updateAvailableFonctions();
    };
    $scope.addFonction = function (fonction) {
	$scope.role.fonctions.push(fonction);
	updateAvailableFonctions();
    };

    var modify = function (method) {
	var role = angular.copy($scope.role);
	delete role.isNew;
	h.callRestModify(method, 'roles', role).then(function () {
	    $location.path('/roles');
	});
    };
    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	modify($scope.role.isNew ? 'post' : 'put');
    };
    $scope["delete"] = function () {
	modify('delete');
    };

    h.getRoles().then(function (roles) {
	$scope.name2role = h.array2hash(roles, 'name');
	if (id === "new") {
	    $scope.role = { isNew: true, fonctions: [] };
	    updateAvailableFonctions();
	} else {
	    var id2role = h.array2hash(roles, 'id');
	    if (id in id2role) {
		$scope.role = id2role[id];
		$scope.role.fonctions.sort();
		updateAvailableFonctions();
	    } else {
		alert("invalid role " + id);
	    }
	}
    });	
});

app.controller('ServicesCtrl', function($scope, h) {
    h.getServices().then(function (services) {
	$scope.services = services;
    });
});

app.controller('ServicesDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;

    var updateCurrentTabTitle = function () {
	$scope.currentTab.text = $scope.service && $scope.service.key || (id === 'new' ? 'Création' : 'Modification');
    };
    updateCurrentTabTitle();
    $scope.$watch('service.key', updateCurrentTabTitle);

    $scope.checkUniqueName = function (name) {
	var service = $scope.key2service[name];
	return !service || service === $scope.service;
    };

    var modify = function (method) {
	var service = angular.copy($scope.service);
	delete service.isNew;
	h.callRestModify(method, 'services', service).then(function () {
	    $location.path('/services');
	});
    };
    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	modify($scope.service.isNew ? 'post' : 'put');
    };
    $scope["delete"] = function () {
	modify('delete');
    };

    h.getServices().then(function (services) {
	$scope.key2service = h.array2hash(h.objectValues(services), 'key');
	if (id === "new") {
	    $scope.service = { isNew: true };
	} else {
	    var id2service = h.array2hash(services, 'id');
	    if (id in id2service) {
		$scope.service = id2service[id];
	    } else {
		alert("invalid service " + id);
	    }
	}
    });	
});

app.controller('MembershipCtrl', function($scope, h) {
    h.getServicesAdh().then(function (services) {
	$scope.services = services;
    });

    var isPhoneNumberInBlackList = function () {
	$scope.isPhoneNumberInBlackList = false;
	if (!$scope.membership.phoneNumber) return;

	h.callRest('membership/isPhoneNumberInBlackList').then(function (is) {
	    $scope.phoneNumberInBlackList_msg = "Votre numéro de téléphone est dans la liste noire";
	    $scope.isPhoneNumberInBlackList = angular.fromJson(is);
	}, function (err) {
	    $scope.isPhoneNumberInBlackList = true;	    
	    $scope.phoneNumberInBlackList_msg = err ? err.error : "error";
	});
    };

    var set_prev_membership = function () {
	$scope.membership.prev_validCG = $scope.membership.validCG;
    };

    var modify = function (method) {
	var membershipRaw = h.objectSlice($scope.membership, ['login','phoneNumber', 'validCG']); // keep only modifiable fields
	membershipRaw.validCP = h.set2array($scope.membership.validCP);
	h.callRestModify(method, 'membership', membershipRaw).then(function () {
	    var prev = $scope.membership.prev_validCG;
	    $scope.submitted_msg = 
		!prev === !$scope.membership.validCG ? "Modifications enregistrées" :
		$scope.membership.validCG ? "Adhésion enregistrée" : "Résiliation effectuée";
	    set_prev_membership();
	    $scope.myForm.$setPristine();
	    isPhoneNumberInBlackList();
	});
    };
    
    $scope.submit = function () {
	if (!$scope.myForm.$valid) return;
	modify('post');
    };

    h.callRest('membership').then(function (membership) {
	membership.validCP = h.array2set(membership.validCP);
	$scope.membership = membership;
	set_prev_membership();
	isPhoneNumberInBlackList();
    });
});

app.controller('ApprovalsCtrl', function($scope, h) {

    function get_approvals() {
	h.callRest('approvals').then(function (approvals) {
	    angular.forEach(approvals, function(msg) {
		msg.date = new Date(msg.date);
	    });
	    $scope.approvals = approvals;
	});
    }

    function modify(msg, status) {
	msg = { id: msg.id, stateMessage: status };
	h.callRestModify('put', 'approvals', msg).then(function () {
	    get_approvals();
	});
    }

    $scope.approve = function (msg) { modify(msg, "IN_PROGRESS"); };
    $scope.cancel = function (msg) { modify(msg, "CANCEL"); };

    get_approvals();
});

app.controller('SendCtrl', function($scope, h, $location) {
    $scope.wip = { phoneNumber: null, login: null }; // temp
    $scope.msg = {};
    var allRecipientTypes = ['SMS_ENVOI_ADH', 'SMS_ENVOI_GROUPES', 'SMS_ENVOI_NUM_TEL', 'SMS_ENVOI_LISTE_NUM_TEL'];
    $scope.$watch('loggedUser', function () {
	$scope.recipientTypes = $.grep(allRecipientTypes, function (e) { 
	    return $scope.loggedUser && $scope.loggedUser.can["FCTN_" + e];
	});
	$scope.recipientType = $scope.recipientTypes[0];
	//$scope.recipientType = 'SMS_ENVOI_LISTE_NUM_TEL'; // TEST
    });

    $scope.msg.mailOption = '';
    $scope.mailOptions = [
	{key: '', label: 'aucun'},
	{key: 'DUPLICATE', label: "accompagner le SMS d'un courriel"},
	{key: 'OTHER', label: "autres destinataires"},
    ];

    h.callRest('messages/groupLeaves').then(function (groupLeaves) {
	$scope.groupLeaves = groupLeaves;
	$scope.msg.senderGroup = groupLeaves[0].id;
    });
    h.callRest('services/sendFctn').then(function (services) {
	$scope.services = services;
    });
    h.callRest('templates').then(function (templates) {
	$scope.templates = templates;
    });

    function computeContent(body, template) {
	body = body || '';
	if (template) {
	    return template.heading + body + template.signature;
	} else {
	    return body;
	}
    }

    $scope.$watch('msg.template', function () {
	if ($scope.msg.template)
	    $scope.msg.body = $scope.msg.template.body;
    });

    $scope.nbMoreCharsAllowed = function(body) {
	var content = computeContent(body, $scope.msg.template);
	content = content.replace(/[\[\]{}\\~^|\u20AC]/g, "xx"); // cf GSM's "Basic Character Set Extension". \u20AC is euro character
	return 160 - content.length;
    };
    $scope.checkMaxSmsLength = function (body) {
	return $scope.nbMoreCharsAllowed(body) >= 0;
    };

    $scope.checkPhoneNumber = function (nb) {
	return !nb || nb.match(phoneNumberPatternOne);
    };

    $scope.msg.destLogins = [];
    $scope.msg.destPhoneNumbers = [];

    // TEST
    //$scope.msg.destLogins.push({id:"aanli", name: "Aymar Anli"});
    //$scope.msg.destGroup = {id:"foo", name: "Groupe Machin"};


    $scope.searchUser = function (token) {
	if (token.length < 4) {
	    $scope.wip.logins = null;
	    return [];
	}
	return h.searchUser(token, { service: $scope.msg.serviceKey })
	    .then(function (logins) {
		$scope.wip.logins = logins;
		return logins;
	    });
    };

    $scope.searchGroup = function (token) {
	if (token.length < 3) {
	    $scope.wip.groups = null;
	    return [];
	}
	return h.searchGroup(token)
	    .then(function (groups) {
		$scope.wip.groups = groups;
		return $scope.wip.groups;
	    });
    };

    $scope.addDestLogin = function () {
	if ($scope.wsgroupsURL) {
	    h.get_noSMS($scope.wip.login, { service: $scope.msg.serviceKey });
	}
	$scope.msg.destLogins.push($scope.wip.login);
	$scope.wip.login = null;
    };
    $scope.addDestPhoneNumber = function () {
	$scope.msg.destPhoneNumbers.push($scope.wip.phoneNumber);
	$scope.wip.phoneNumber = null;
    };
    $scope.addDestGroup = function () {
	$scope.msg.destGroup = $scope.wip.group; 
	$scope.wip.group = null;
    };
    $scope.addListDestPhoneNumber = function () {
	var s = $scope.wip.listPhoneNumbers;
	var numbers = s.match(phoneNumberPatternAll);
	if (numbers) {
	    $scope.msg.destPhoneNumbers = 
		$scope.msg.destPhoneNumbers.concat(numbers);
	    $scope.wip.listPhoneNumbers =
		s.replace(phoneNumberPatternAll, '');	    
	}
    };

    //$scope.wip.login = { id: 'prigaux', name: 'P Rig' };
    //$scope.addDestLogin();
    

    $scope.removeRecipient = function (e) {
	var msg = $scope.msg;
	if (e === msg.destGroup) {
	    msg.destGroup = null;
	} else {
	    h.array_remove_elt(msg.destLogins, e);
	    h.array_remove_elt(msg.destPhoneNumbers, e);
	}
    };

    $scope.submit = function () {
	if ($scope.wip.phoneNumber &&
	    $scope.myForm.destPhoneNumber &&
	    !$scope.myForm.destPhoneNumber.$invalid) {
	    return $scope.addDestPhoneNumber();
	}
	if ($scope.wip.listPhoneNumbers &&
	    $scope.msg.destPhoneNumbers.length === 0) {
	    return $scope.addListDestPhoneNumber();
	}

	$scope.submitted = 1; 
	if (!$scope.myForm.$valid) return;

	function destIds(l) {
	    var ids = h.array_map(l, function (e) { return e.id; });
	    return ids.length ? ids : null;
	}
	var msg = $scope.msg;
	var msgToSend = h.objectSlice($scope.msg, ['senderGroup','serviceKey']);
	msgToSend.content = computeContent(msg.body, msg.template);
	msgToSend.smsTemplate = msg.template && msg.template.label; // for statistics on templates usage
	msgToSend.recipientLogins = destIds(msg.destLogins);
	msgToSend.recipientPhoneNumbers = msg.destPhoneNumbers.length ? msg.destPhoneNumbers : null;
	msgToSend.recipientGroup = msg.destGroup && msg.destGroup.id;
	if (msg.mailOption) {
	    var otherRecipients = msg.mailToSend.mailOtherRecipients;
	    msgToSend.mailToSend =
		{ isMailToRecipients: msg.mailOption === 'DUPLICATE',
		  mailContent: msgToSend.content, 
		  mailTemplate : msgToSend.smsTemplate,
		  mailSubject: msg.mailToSend.mailSubject,
		  mailOtherRecipients : otherRecipients ? otherRecipients.split("\n") : [] };
	}

	h.mutexAction($scope, 'sending', function () {
	    console.log("sending...");
	    console.log(msgToSend);
	    return h.callRestModify('post', 'messages', msgToSend).then(function (resp) {
		var msg = resp.data;
		$location.path('messages/' + msg.id);
	    });
	});

    };

});

app.controller('MessagesCtrl', function($scope, h, $location, $route) {
    $scope.initialNbResults = 50;
    $scope.nbResults = $scope.initialNbResults;
    $scope.filter = $location.search();
    $scope.inProgress = false;

    h.callRest('messages/senders').then(function (senders) {
	$scope.senders = senders;
    });

    $scope.setFilter = function (e) {
	e = h.objectSlice(e, ['sender']); // all but hashKey
	$location.search(e);
	$route.reload();
    };

    var sender_name = function (login) {
	return $scope.senders && $scope.senders[login] || login;
    };

    $scope.formattedFilter = function () {
	var f = $scope.filter;
	var l = [];
	if (f.sender) l.push("Expéditeur = " + sender_name(f.sender));
	return l.length ? l.join(", ") : "aucun";
    };

    var msgWithDetails;
    $scope.toggleMsgDetails = function (msg) {
	if (msgWithDetails) msgWithDetails.showDetails = false;
	if (msgWithDetails && msg === msgWithDetails) {
	    msgWithDetails = null;
	} else {
	    msgWithDetails = msg;
	    msg.showDetails = true;
	    h.mayGetMsgStatuses(msg);
	}
    };

    var getResults = function() {
	if ($scope.inProgress) return;
	$scope.inProgress = true;
	var fullFilter = angular.extend({ maxResults: $scope.nbResults }, $scope.filter);
	h.callRest('messages', fullFilter)
	    .then(function (messages) {
		$scope.noMoreResults = messages.length < fullFilter.maxResults;
		$scope.messages = messages;
		$scope.showSender = $scope.loggedUser.can.FCTN_SUIVI_ENVOIS_ETABL && !$scope.filter.sender;
		$scope.inProgress = false;
	    });
    };

    $scope.showMoreResults = function () {
	if ($scope.noMoreResults) return;
	$scope.nbResults = $scope.nbResults + $scope.initialNbResults;
	getResults();
    };

    getResults();
});

app.controller('MessagesDetailCtrl', function($scope, h, $routeParams, $location) {
    var id = $routeParams.id;

    $scope.getMsg = function () {
	h.callRest('messages/' + id).then(function (msg) {
	    $scope.msg = msg;
	    h.mayGetMsgStatuses(msg);
	});
    };

    $scope.getMsg();
});

})();
