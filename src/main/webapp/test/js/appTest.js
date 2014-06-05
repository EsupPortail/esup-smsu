// taken verbatim from angular.js (except for forEach -> angular.forEach)
function isDefined(value){return typeof value != 'undefined';}
function parseKeyValue(/**string*/keyValue) {
  var obj = {}, key_value, key;
  angular.forEach((keyValue || "").split('&'), function(keyValue){
    if (keyValue) {
      key_value = keyValue.split('=');
      key = decodeURIComponent(key_value[0]);
      obj[key] = isDefined(key_value[1]) ? decodeURIComponent(key_value[1]) : true;
    }
  });
  return obj;
}

var myAppTest = angular.module('myAppTest', ['myApp', 'ngMockE2E']);

myAppTest.run(function($http, $httpBackend, h, $rootScope) {

    function flatten(l) {
	return h.array_concat_map(l, function (l) { return l || []; });	
    }
    function reverse_hashMulti(h_in) {
	var h = {};
	angular.forEach(h_in, function (bs, a) {
	    angular.forEach(bs, function (b) {
	    	(h[b] = h[b] || []).push(a);
	    });
	});
	return h;
    }
    var defaultLoggedUser = 'admin';

    var consts = {
	users: {"admin":"The Boss","sender1":"Sender #1","sender2":"Sender #2","user1": "User #1"},
	allFonctions: ["FCTN_SMS_ENVOI_ADH","FCTN_SMS_ENVOI_GROUPES","FCTN_SMS_ENVOI_NUM_TEL","FCTN_SMS_REQ_LDAP_ADH","FCTN_SMS_AJOUT_MAIL","FCTN_GESTIONS_RESPONSABLES","FCTN_GESTION_ROLES_CRUD","FCTN_GESTION_ROLES_AFFECT","FCTN_GESTION_MODELES","FCTN_GESTION_SERVICES_CP","FCTN_GESTION_QUOTAS","FCTN_SUIVI_ENVOIS_UTIL","FCTN_SUIVI_ENVOIS_ETABL","FCTN_GESTION_GROUPE","FCTN_SMS_ENVOI_LISTE_NUM_TEL"],
	accounts: ["test-univ.fr"],
	basicGroups: [{"id": "gfoo", "name": "GroupFoo"}, 
		      {"id": "senders", "name": "All Senders"}],
	groupMembers: {"gfoo": ["user1"], "senders": ["sender1", "sender2"]},
    };
    consts.user2groupIds = reverse_hashMulti(consts.groupMembers);

    var db = {
	groups: [{"id":11,"label":"admin","displayName": consts.users["admin"], "labelIsUserId":true,
		  "role":"SUPER_ADMIN","account":"test-univ.fr", "supervisors":{},
		  "quotaSms":0,"maxPerSms":1,"consumedSms":0 },
		 {"id":12,"label":"senders","displayName": id2groupName("senders"), "labelIsUserId":false,
		  "role":"sender","account":"test-univ.fr","supervisors":h.objectSlice(consts.users, ["sender1"]),
		  "quotaSms":0,"maxPerSms":1,"consumedSms":0 }],

	roles: [{"id":1,"name":"SUPER_ADMIN","fonctions":angular.copy(consts.allFonctions)},
		{"id":2,"name":"Default","fonctions":[]},
		{"id":3,"name":"sender","fonctions":["FCTN_SMS_ENVOI_GROUPES","FCTN_SUIVI_ENVOIS_UTIL","FCTN_SMS_ENVOI_ADH","FCTN_SMS_AJOUT_MAIL"]}],
	services: [],
 	templates: [{"id":1,"label":"U","heading":"Foo","body":"","signature":""}],
	membership: [{"firstName":"The","lastName":"Boss","phoneNumber":"0601010101","availablePhoneNumbers":[],"phoneNumberValidationCode":null,"validCG":true,"validCP":[],"login":"admin","flagPending":false}],
    };

    var msg34 = {"id":34,"date":1396984471000,"content":"test BL esup-smu-new",
		 "recipients":["0652767674"], "groupRecipientName":null,
		 "senderLogin":'sender1', "groupSenderName":consts.basicGroups[0].name,
		 "stateMail": "ERROR",
		 "stateMessage":"CANCEL","supervisors":["supervisor1"]};
    var msg33 = {"id":33,"date":1366107339000,"content":"été\nhiver\nété",
		 "recipients":["sender1", "sender2"], "groupRecipientName":consts.basicGroups[0].name,
		 "senderLogin":"sender2", "groupSenderName":consts.basicGroups[0].name,
		 "stateMessage":"WAITING_FOR_APPROVAL","supervisors":["sender1"]};
    db.msgs = [completeMsg(msg34),completeMsg(msg33)];

    function id2groupName(id) {
	var g = h.simpleFind(consts.basicGroups, function (g) {
	    return g.id === id;
	});
	return g && g.name;
    }

    function roleNames2rights(roleNames) {
	var roles = h.array_map(roleNames, function (name) {
	    return h.simpleFind(db.roles, function (e) { return e.name === name; });
	});
	return h.uniq(h.array_concat_map(roles, function (e) { return e.fonctions; }));
    }

    function userGroups(id) {
	var userGroup = h.simpleFilter(db.groups, function (g) { 
	    return g.label === id && g.labelIsUserId;
	});
	var nonUserGroups = h.array_concat_map(consts.user2groupIds[id], function (gId) {
	    return h.simpleFilter(db.groups, function (g) {
		return g.label === gId && !g.labelIsUserId;
	    });
	});
	return userGroup.concat(nonUserGroups);
    }

    function getLoggedUser(id) {
	var groups = userGroups(id);
	var rights = roleNames2rights(h.array_map(groups, function (e) { return e.role; }));
	return {"id":id, "displayName": consts.users[id], "rights": rights};
    }

    function userGroupLeaves() {
	var r = h.array_map(userGroups($rootScope.loggedUser.id), function (g) {
	    var name = g.labelIsUserId ? consts.users[g.label] : id2groupName(g.label);
	    return { id: g.label, name: name };
	});
	return r;
    }

    function senders() {
	var h = {};
	angular.forEach(db.msgs, function (msg) {
	    h[msg.senderLogin] = msg.senderName;
	});
	return h;
    }

    function newId(list) {
	var ids = h.array_map(list, function(o) { return o.id; });
	var max = ids.length ? Math.max.apply(null, ids) : 0;
	return max + 1;
    }

    function completeMsg(msg) {
	var msg_ = { 
	    senderName : consts.users[msg.senderLogin],
	    nbRecipients : msg.recipients.length,
	    accountLabel : consts.accounts[0],
	    date : new Date(),
	    id : newId(db.msgs),
	    serviceName : null, stateMail : null
	};
	return angular.extend(msg_, msg);
    }

    function createMsg(inMsg) {
	var recipients = flatten([inMsg.recipientPhoneNumbers, inMsg.recipientLogins, 
				 consts.groupMembers[inMsg.recipientGroup]]);
	var msg = {
	    "content": inMsg.content,
	    "senderLogin": $rootScope.loggedUser.id,
	    "groupSenderName": inMsg.senderGroup,
	    "groupRecipientName": inMsg.recipientGroup,
	    "stateMessage":"WAITING_FOR_APPROVAL",
	    "supervisors":["sender1"],
	    "recipients": recipients };
	if (inMsg.mailToSend) msg.stateMail = "WAIT";
	return completeMsg(msg);
    }

    function canApprove(msg) {
	return msg.stateMessage === 'WAITING_FOR_APPROVAL' &&
	    ($rootScope.loggedUser.can.FCTN_GESTIONS_RESPONSABLES ||
	     h.array2set(msg.supervisors)[$rootScope.loggedUser.id]);
    }

    function loggedUserMembership() {
	var o = h.simpleFind(db.membership, function (e) { 
	    return e.login === $rootScope.loggedUser.id;
	});
	if (!o) {
	    o = { login: $rootScope.loggedUser.id };
	    db.membership.push(o);
	}
	return o;
    }

    $httpBackend.whenGET(/rest.groups.accounts/).respond(consts.accounts);
    $httpBackend.whenGET(/rest.roles.fonctions/).respond(consts.allFonctions);
    $httpBackend.whenGET(/rest.users.search/).respond(consts.users);
    $httpBackend.whenGET(/rest.groups.search/).respond(consts.basicGroups);

    function whenGET_rest_login(method, url, data, headers) {
	var loggedUser = getLoggedUser(headers["X-Impersonate-User"] || defaultLoggedUser);
	return [200, loggedUser];
    }
    $httpBackend.whenGET(/rest.login/).respond(whenGET_rest_login);
    $httpBackend.whenJSONP(/rest.login/).respond(whenGET_rest_login);

    $httpBackend.whenGET(/rest.messages.groupLeaves/).respond(function () { return [200, userGroupLeaves()]; });
    $httpBackend.whenGET(/rest.messages.senders/).respond(function () { return [200, senders()]; });
    $httpBackend.whenGET(/rest.messages/).respond(get_list(db.msgs));
    $httpBackend.whenPOST(/rest.messages/).respond(function (method, url, data) {
	var msg = createMsg(angular.fromJson(data));
	db.msgs.unshift(msg);
	return [200, msg];
    });
    $httpBackend.whenGET(/rest.approvals/).respond(function () { return [200, h.simpleFilter(db.msgs, canApprove)]; });
    $httpBackend.whenPUT(/rest.approvals/).respond(modify_list(db.msgs));

    $httpBackend.whenGET(/rest.membership/).respond(function () { return [200, loggedUserMembership() ]; });
    $httpBackend.whenPOST(/rest.membership/).respond(modify_object(loggedUserMembership));

    function modify_object(object_getter) {
	return function(method, url, data) {
	    angular.extend(object_getter(), angular.fromJson(data));
	    return [200];
	};
    }

    function create_list(list) {
	return function(method, url, data) {
	    var o = angular.fromJson(data);
	    o.id = newId(list);
	    list.push(o);
	    return [200];
	};
    }
    function get_list(list) {
	return function(method, url, data) {
	    var m = url.match(/\/(\d+)/);
	    if (m) {
		var id = m[1];
		var o = h.simpleFind(list, function (o) { return o.id == id; });
		return [200, o];
	    } else {
		return [200, list];
	    }
	};
    }
    function modify_list(list) {
	return function(method, url, data) {
	    var id = url.match(/(\d+)$/)[0];
	    var o = h.simpleFind(list, function (o) { return o.id == id; });
	    angular.extend(o, angular.fromJson(data));
	    return [200];
	};
    }
    function delete_list(list) {
	return function(method, url, data) {
	    var id = url.match(/(\d+)$/)[0];
	    var list_ = h.simpleFilter(list, function (o) { return o.id != id; });
	    angular.copy(list_, list);
	    return [200];
	};
    }

    angular.forEach([ 'roles', 'services', 'templates', 'groups' ], function (kind) {
	var list = db[kind];
	var regexp = new RegExp("/rest/" + kind);
	$httpBackend.whenGET(regexp).respond(list);
	$httpBackend.whenPUT(regexp).respond(modify_list(list));
	$httpBackend.whenPOST(regexp).respond(create_list(list));
	$httpBackend.whenDELETE(regexp).respond(delete_list(list));
    });

    $httpBackend.whenGET(/.*/).passThrough();
});


