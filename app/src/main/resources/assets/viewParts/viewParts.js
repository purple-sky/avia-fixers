'use strict';

angular.module('myApp.viewParts', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewParts', {
        templateUrl: 'viewParts/viewParts.html',
        controller: 'ViewPartsCtrl'
    });
}])

.controller('ViewPartsCtrl', ['$scope', '$http',function($scope, $http) {
    $scope.parts = [];

    $http
        .get('/api/parts')
        .then(function successCallback(response) {
            $scope.parts = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);