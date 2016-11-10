'use strict';

angular.module('myApp.viewPart', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewPart/:id', {
        templateUrl: 'viewPart/viewPart.html',
        controller: 'ViewPartCtrl'
    });
}])

.controller('ViewPartCtrl', ['$scope', '$http', '$routeParams', '$location', '$filter', function($scope, $http, $routeParams, $location, $filter) {
    var currId = $filter('limitTo')($routeParams.id, 12, 1);
    $scope.id = currId;
    //$scope.part = {partNum: currId};

    $scope.postUpdate = function () {
        $http
                    .put('/api/parts', $scope.part)
                    .then(function(){$location.path('/viewPart/:'+currId)}, function(){});
    }


    $http
        .get('/api/parts/:'+ currId)
        .then(function successCallback(response) {
            $scope.part = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);