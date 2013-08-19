function JugJaneCtrl($scope, $routeParams) {
	$scope.title = $routeParams['title']
	$scope.text = $routeParams['text']
	$scope.url = $routeParams['url']
}
 
function AreaCtrl($scope, $http, $location, $routeParams) {
	$scope.areatypes = [ { id: '0', text: 'Indoor gym' }, { id: '1', text: 'Outdoor' } ];
    $scope.createArea = function() {
        $http({
            method : 'POST',
            url : '/area/new',
            data : $scope.area
        }).success(function() { 
            $location.path('/msg?title=Ok'); });
    }
    
	$scope.area = {};
	$scope.area.areatype = $scope.areatypes[0].id;
}

//JugJaneCtrl.$inject = ['$scope', '$routeParams'];
//AreaCtrl.$inject = ['$scope', '$http', '$routeParams'];