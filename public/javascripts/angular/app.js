var jugJaneApp = angular.module('jugjane', [])

jugJaneApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		templateUrl : 'assets/partials/index.html',
		controller : JugJaneCtrl
	}).when('/gym/new', {
		templateUrl : 'assets/partials/gym/new.html',
		controller : GymCtrl
	}).otherwise({
		redirectTo : '/'
	});
} ]);
jugJaneApp.config([ '$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push('commonHttpInterceptor');
} ]);

jugJaneApp.factory('commonHttpInterceptor', function($q) {
	return {
		// optional method
		'responseError' : function(rejection) {
			if (rejection.status == 500) {
				$('body').html(rejection.data);
				return;
			}
			return $q.reject(rejection);
		}
	}
});