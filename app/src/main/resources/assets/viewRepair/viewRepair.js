'use strict';

angular.module('myApp.viewRepair', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/repair/:id', {
        templateUrl: 'viewRepair/viewRepair.html',
        controller: 'ViewRepairCtrl'
    });
}])

.controller('ViewRepairCtrl', ['$scope', '$http', '$location', '$routeParams', '$filter',
                                function($scope, $http, $location, $routeParams, $filter) {
    var currId = $filter('limitTo')($routeParams.id, 20, 1);
    $scope.id = currId;
    $scope.parts = [];
    $scope.repair = {};

    $scope.updateRepair = function() {
            $http
                .post('/api/schedule/' + currId, $scope.repair)
                .then(function(){$location.path('/repairs')}, function(){});
        }

    $http
        .get('/api/schedule/'+ currId)
        .then(function successCallback(response) {
            $scope.parts = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);