(function () {
"use strict";

var app = angular.module('myApp');

app.service('basicHelpers', function () {

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
this.array_concat_map = function (array, f) {
    var r = [];
    angular.forEach(array, function (e) {
	r = r.concat(f(e)); 
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
this.uniq = function (array) {
    return h.set2array(h.array2set(array));
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
    var set = {};
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

});

})();