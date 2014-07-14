(function () {
"use strict";

var app = angular.module('myApp');

app.directive('whenScrolled', function() {
    return function(scope, elem, attr) {
        $(window).bind('scroll', function() {
	    var docViewBottom = $(window).scrollTop() + $(window).height();
	    var elemBottom = $(elem).offset().top + $(elem).height();
	    if (elemBottom === 0) return; // why? in some cases, the event is triggered and the height/position is unknown (on chromium at least)
	    //var t = elemBottom + " <= " + docViewBottom
	    //$("#debug").text(t);
	    if ((elemBottom <= docViewBottom)) {
		//$("#debug").text(t + " scrolled into view");
                scope.$apply(attr.whenScrolled);
            }
        });
    };
});

app.directive('displayRequiredIfNeeded', function ($parse) {
    return { restrict: 'A', 
	     template: '<span ng-show="$parent.submitted && inputName.$error.required" class="help-block">Required</span>',
	     scope: { inputName: '=' }
	   }; 
});

app.directive('myValidator', function () {
  return {
    restrict: 'A',
    require: 'ngModel', // controller to be passed into directive linking function
    link: function (scope, elem, attr, ctrl) {
	var checkers = scope.$eval(attr.myValidator);
	ctrl.$parsers.push(function (viewValue) {
	    angular.forEach(checkers, function (checker, checkName) {
		ctrl.$setValidity(checkName, checker(viewValue));
	    });
	    return viewValue;
        });
    }
   };
});

app.directive('autocompleteUserOrGroup', function (globals) {
  var searchUserURL = globals.wsgroupsURL + '/searchUserCAS';
  var searchGroupURL = globals.wsgroupsURL + '/searchGroup';
  return {
    restrict: 'A',
    require: 'ngModel',
    link: function (scope, el, attr, ngModel) {
	var select = function (event, ui) {
	    // NB: this event is called before the selected value is set in the "input"

	    ui.item.id = ui.item.value;
	    ui.item.name = ui.item.label;

	    $(el).val(ui.item.label);

            scope.$apply(function () {
                ngModel.$setViewValue(ui.item);
            });
	    scope.$apply(attr.onSelect);
	    return false;
        };
	var params = { select: select };
	if (attr.autocompleteUserOrGroup === 'user') {
            $(el).autocompleteUser(searchUserURL, params);
	} else {
            $(el).autocompleteGroup(searchGroupURL, params);
	}
    }
  };
});

app.directive('myAutocomplete', function (globals) {
  return {
    restrict: 'A',
    replace: true,
    // the scope could be the following (can't work for ng-model, so simple dynamic templating is used)
    //scope: { onSelect: '&', doSearch: '&', ng-model: '=' },
    template: function(element, attrs) {
	if (globals.wsgroupsURL) {
	    return '<input type="text" autocomplete-user-or-group="' + attrs.myAutocomplete + '">';
	} else {
	    var typeahead = 'e as e.name for e in ' + attrs.doSearch;
            return '<input type="text" typeahead="' + typeahead + '" typeahead-on-select="' + attrs.onSelect + '" >';
	}
    }
  };
});

app.directive('myInclude', function (globals) {
  return {
    restrict: 'A',
    replace: true,
    template: function(element, attrs) {
	var absoluteURL = globals.baseURL + '/partials/' + attrs.myInclude;
	return '<div ng-include="\'' + absoluteURL + '\'"></div>';
    }
  };
});

})();