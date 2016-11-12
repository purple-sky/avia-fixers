'use strict';

angular.module('myApp.viewRepairs', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/repairs', {
        templateUrl: 'viewRepairs/viewRepairs.html',
        controller: 'ViewRepairsCtrl'
    });
}])

.controller('ViewRepairsCtrl', ['$scope', '$http', '$location', function($scope, $http, $location) {
    $scope.parts = [];

    $scope.updatePart = function (id) {
            $location.path('/repair/:' + id);
        }

    $http
        .get('/api/schedule')
        .then(function successCallback(response) {
            $scope.parts = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);