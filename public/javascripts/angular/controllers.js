function IndexCtrl($scope, $http, formDataObject) {
}

function BoulderCreateCtrl($scope, $http) {
	$scope.grades = [];
	$scope.boulder = {};
	$scope.boulder.gradeid = null;

	$http({
		method : 'GET',
		url : '/gym/'  + $routeParams.secret + '/grades'
	}).success(function(result) {
		$scope.grades = result;
		$scope.boulder.gradeid = $scope.grades[0].id;
	});

	$scope.uploadPhoto = function($files) {
		$http.uploadFile({
			url : '/boulder/new',
			data : $scope.boulder,
			file : $scope.photo
		}).success(function() {
			showMsg($scope, 'Thank you!', 'Go on. Give us another one.', '#/');
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