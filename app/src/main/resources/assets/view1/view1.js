'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/view1', {
        templateUrl: 'view1/view1.html',
        controller: 'View1Ctrl'
    });
}])

.controller('View1Ctrl', ['$scope', '$http', '$location',function($scope, $http, $location) {
    $scope.orders = [];
    $scope.where = {};

    $scope.viewOrder = function (id) {
        $location.path('/viewOrder/:' + id);
    }

    $scope.filter = function() {
        $http
            .get('/api/orders/filter', $scope.where)
            .then(function successCallback(response) {
                        $scope.orders = response.data;
                    }, function errorCallback(response) {}
                    );

    }

    $http
        .get('/api/orders')
        .then(function successCallback(response) {
            $scope.orders = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);