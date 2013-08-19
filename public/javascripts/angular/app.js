angular.module('jugjane', []).config(
	[ '$routeProvider', function($routeProvider) {
		$routeProvider
		.when('/', { templateUrl : 'assets/partials/index.html', controller : JugJaneCtrl })
		.when('/msg', { templateUrl : 'assets/partials/msg.html', controller : JugJaneCtrl })
		.when('/area/new', { templateUrl : 'assets/partials/area/new.html', controller : AreaCtrl })
		.otherwise({ redirectTo : '/' });
	} ]);