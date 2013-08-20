function JugJaneCtrl($scope, $routeParams) {
}
 
function AreaCtrl($scope, $http, $location, $routeParams) {
	$scope.areatypes = [ { id: '0', text: 'Indoor gym' }, { id: '1', text: 'Outdoor' } ];
    $scope.createArea = function() {
        $http({
            method : 'POST',
            url : '/area/new',
            data : $scope.area
        }).success(function() { 
            $scope.msg = { title: 'Thank you!', text: 'Ano', url: '/' };
        }).error(function() { 
            $scope.msg = { title: 'Error!', text: 'S', url: '/' };
        })
    }
    
	$scope.area = {};
	$scope.area.areatype = $scope.areatypes[0].id;
}

//JugJaneCtrl.$inject = ['$scope', '$routeParams'];
//AreaCtrl.$inject = ['$scope', '$http', '$routeParams'];