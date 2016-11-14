'use strict';

angular.module('myApp.viewParts', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewParts', {
        templateUrl: 'viewParts/viewParts.html',
        controller: 'ViewPartsCtrl'
    });
}])

.controller('ViewPartsCtrl', ['$scope', '$http', '$location', function($scope, $http, $location) {
    $scope.parts = [];

    $scope.updatePart = function (id) {
            $location.path('/viewPart/:' + id);
    }

    $scope.deletePart = function (id) {
                $http
                    .delete('/api/parts/'+ id)
                    .then(function(){$location.path('/viewParts')}, function(){});
    }


    $http
        .get('/api/parts')
        .then(function successCallback(response) {
            $scope.parts = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);