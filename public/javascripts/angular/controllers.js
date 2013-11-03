function IndexCtrl($scope, $http, formDataObject) {
}

function RouteCtrl($scope, $http, $routeParams) {
	$scope.route = {};
	
	var init = function() {
		$http({
			method : 'GET',
			url : '/'  + $routeParams.gymname + '/' + $routeParams.routeId
		}).success(function(result) {
			$scope.route = result.route;
		});
	}
	
	// Initialize controller
	init();
}

function GymCtrl($scope, $http, $routeParams, $cookies) {
	$scope.gym = {};
	$scope.isAdmin = false;
	
	var init = function() {
		$http({
			method : 'GET',
			url : '/'  + $routeParams.gymname + ($routeParams.s == null ? '' : ('?s=' + $routeParams.s))
		}).success(function(result) {
			$scope.gym = result;
			$scope.isAdmin = isAdmin($cookies, $scope.gym.handle);
		});
	}
	
	// Initialize controller
	init();
}

function BoulderNewCtrl($scope, $http, $routeParams) {
	$scope.boulder = {};
	$scope.boulder.gradeId = null;
	$scope.boulder.holdsColor = null;
	$scope.boulder.gymName = $routeParams.gymname;
	$scope.boulder.gymSecret = $routeParams.secret;

	$http({
		method : 'GET',
		url : '/'  + $routeParams.gymname + '/new'
	}).success(function(result) {
		$scope.grades = result.grades;
		$scope.holds = result.holds;
		if ($scope.grades != null && $scope.grades.length > 0)
			$scope.boulder.grade = $scope.grades[0];
		else
			$scope.boulder.grade = null;
	});

	$scope.uploadPhoto = function($files) {
		$scope.boulder.gradeId = $scope.boulder.grade.id
		$scope.boulder.holdsColor = $scope.boulder.holdColors.name
		$http.uploadFile({
			url : '/boulder/new',
			data : $scope.boulder,
			file : $scope.photo
		}).success(function() {
			showMsg($scope, 'Thank you!', 'Go on. Give us another one.', '#/hive/new/666');
		})
	}

	$scope.onFileSelect = function($files) {
		$scope.photo = $files[0];
	}
}

function GymNewCtrl($scope, $http) {
	$scope.createGym = function() {
		$http({
			method : 'POST',
			url : '/gym/new',
			data : $scope.gym
		})
		.success(
				function() {
					showMsg(
							$scope,
							'Thank you!',
							'Please check your mailbox to validate the email address.',
							'#/');
				})
	}

	$scope.gym = {};
}

function GymApproveCtrl($scope, $http, $routeParams) {
	console.log($routeParams.secret)
	$http({
		method : 'GET',
		url : '/gym/approve/' + $routeParams.secret,
		data : $scope.gym
	}).success(function() {
		showMsg($scope, 'Yes!', 'The gym has been approved.', '#/');
	})
}

function GymValidateCtrl($scope, $http, $routeParams) {
	console.log($routeParams.secret)
	$http({
		method : 'GET',
		url : '/gym/validate/' + $routeParams.secret,
		data : $scope.gym
	})
			.success(
					function() {
						showMsg(
								$scope,
								'Yes!',
								'The gym has been validated. You may now start uploading boulders.',
								'#/');
					})
}

function isAdmin($cookies, gymHandle) {
	return $cookies[gymHandle] != null;	
}

function showMsg($scope, title, text, url) {
	$scope.msg = {};
	$scope.msg.title = title;
	$scope.msg.text = text;
	$scope.msg.url = url;
}