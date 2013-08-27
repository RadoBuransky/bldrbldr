function JugJaneCtrl($scope, $routeParams) {
}
 
function GymCtrl($scope, $http) {
    $scope.createGym = function() {
        $http({
            method : 'POST',
            url : '/gym/new',
            data : $scope.gym
        }).success(function() {
        	showMsg($scope, 'Thank you!', 'Please check your mailbox to validate the email address.', '#/');
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
    }).success(function() {
    	showMsg($scope, 'Yes!', 'The gym has been validated. You may now start uploading boulders.', '#/');
    })
}

function showMsg($scope, title, text, url) {
	$scope.msg = {};
	$scope.msg.title = title;
	$scope.msg.text = text;
	$scope.msg.url = url;	
}