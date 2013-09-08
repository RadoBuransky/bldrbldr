function IndexCtrl($scope, $http, formDataObject) {
}

function BoulderNewCtrl($scope, $http, $routeParams) {
	$scope.boulder = {};
	$scope.boulder.grade = null;
	$scope.boulder.holdColors = null;

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

function GymCtrl($scope, $http) {
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

function showMsg($scope, title, text, url) {
	$scope.msg = {};
	$scope.msg.title = title;
	$scope.msg.text = text;
	$scope.msg.url = url;
}