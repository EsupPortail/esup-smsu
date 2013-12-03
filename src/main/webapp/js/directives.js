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

// for inputs with type 'file'
app.directive('base64content', function ($compile) {
  return {
    scope: { base64content: '=' },
    link: function (scope, element, attrs) {
	element.bind('change', function () {
	    // nb: we do not modify the content if the user did not choose a file
	    if (!this.files[0]) return;

	    var reader = new FileReader();
	    reader.onload = function (env) {
		scope.$apply(function () {
		    scope.base64content = env.target.result.replace(/.*?;base64,/, '');
		});
	    };
	    reader.readAsDataURL(this.files[0]);
	});
    }
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

})();