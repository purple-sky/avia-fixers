'use strict';

angular.module('myApp.view2', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/view2', {
        templateUrl: 'view2/view2.html',
        controller: 'View2Ctrl'
    });
}])

.controller('View2Ctrl', ['$scope', '$http', '$location', function($scope, $http, $location) {
    $scope.parts = new Array(1);

    $scope.addPart = function() {
        $scope.parts.push({});
    };

    $scope.submitOrder = function(){
        $http
            .post('/api/orders', {parts: $scope.parts, customerId: 200048001})
            .then(function(){$location.path('/view1')}, function(){});
    }
}]);