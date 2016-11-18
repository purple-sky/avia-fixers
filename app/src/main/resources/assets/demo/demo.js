'use strict';

angular.module('myApp.demo', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/demo', {
        templateUrl: 'demo/demo.html',
        controller: 'DemoCtrl'
    });
}])

.controller('DemoCtrl', ['$scope', '$http', '$location', '$routeParams',
                                function($scope, $http, $location, $routeParams) {
    $scope.demo2 = [];
    $scope.demo4 = [];

    $scope.getDemo4 = function() {
        $http
            .get('/api/demos/demo4')
            .then(function successCallback(response) {
                $scope.demo4 = response.data;
            }, function errorCallback(response) {
        });
    }

    $http
        .get('/api/demos/demo2')
        .then($scope.getDemo4())
        .then(function successCallback(response) {
            $scope.demo2 = response.data;
        }, function errorCallback(response) {
        });
}]);