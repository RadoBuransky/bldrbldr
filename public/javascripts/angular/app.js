var jugJaneApp = angular.module('jugjane', [])

jugJaneApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		templateUrl : 'assets/partials/index.html',
		controller : JugJaneCtrl
	}).when('/gym/new', {
		templateUrl : 'assets/partials/gym/new.html',
		controller : GymCtrl
	}).when('/gym/approve/:secret', {
		templateUrl : 'assets/partials/msg.html',
		controller : GymApproveCtrl
	}).when('/gym/validate/:secret', {
		templateUrl : 'assets/partials/msg.html',
		controller : GymValidateCtrl
	}).otherwise({
		redirectTo : '/'
	});
} ]);

jugJaneApp.config([ '$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push('commonHttpInterceptor');
} ]);

jugJaneApp.factory('commonHttpInterceptor', function($q, $rootScope) {
	return {
		'responseError' : function(rejection) {
			if (rejection.status == 500) {
				$('body').html(rejection.data);
				return;
			}
			else if (rejection.status / 100 == 4) {
				$rootScope.msg = {};
				$rootScope.msg.title = "Error!";
				$rootScope.msg.text = rejection.data;
				$rootScope.msg.url = '#/';
				return $q.reject(rejection);
			}
			return $q.reject(rejection);
		}
	}
});

jugJaneApp.run( function($rootScope, $location) {
	// register listener to watch route changes
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
    	$rootScope.msg = null;
    });
});