function IndexCtrl($scope, $http, formDataObject) {
	$scope.gyms = [];
	$scope.boulder = {};
	$scope.boulder.gymid = null;
	
    $http({
        method : 'GET',
        url : '/gym/list'
    }).success(function(result) {
    	$scope.gyms = result;
    	$scope.boulder.gymid = $scope.gyms[0].id;
    });
    
    $scope.uploadPhoto = function() {
        $http({
            method : 'POST',
            url : '/boulder/new',
            headers: { 'Content-Type': false },
            data: { photo: $scope.photo },
			transformRequest : function(data) {
				var formData = new FormData();
				// need to convert our json object to a string version of json
				// otherwise
				// the browser will do a 'toString()' on the object which will
				// result
				// in the value '[Object object]' on the server.
				//formData.append("model", angular.toJson(data.model));
				
				// add each file to the form data and iteratively name them
				formData.append("file0", data.photo);
				
				return formData;
            }
        }).success(function() {
        	showMsg($scope, 'Thank you!', 'Go on. Give us another one.', '#/');
        })
    }
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