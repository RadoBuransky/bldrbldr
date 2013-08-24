function JugJaneCtrl($scope, $routeParams) {
}
 
function GymCtrl($scope, $http, $location, $routeParams) {
    $scope.createGym = function() {
        $http({
            method : 'POST',
            url : '/gym/new',
            data : $scope.gym
        }).success(function() {
        	$scope.msg = {};
        	$scope.msg.title = "Thank you!";
        	$scope.msg.text = "Please check your mailbox to validate the email address.";
        	$scope.msg.url = '#/';
        })
    }
    
	$scope.gym = {};
}