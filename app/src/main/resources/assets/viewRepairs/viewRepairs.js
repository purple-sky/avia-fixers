'use strict';

angular.module('myApp.viewRepairs', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/repairs', {
        templateUrl: 'viewRepairs/viewRepairs.html',
        controller: 'ViewRepairsCtrl'
    });
}])

.controller('ViewRepairsCtrl', ['$scope', '$http', '$location', '$rootScope', function($scope, $http, $location, $rootScope) {
    $scope.parts = [];
    $scope.where = {};
    $scope.fixerUser = $rootScope.fixerUser;

    $scope.updatePart = function (id) {
            $location.path('/repair/:' + id);
    }

    $scope.filter = function() {
            $http
                .get('/api/schedule', {params: $scope.where})
                .then(function successCallback(response) {
                            $scope.parts = response.data;
                        }, function errorCallback(response) {}
                        );

    }

    $scope.$watch('where.status', function () {
        if($scope.where.status == 'all'){
            delete $scope.where.status;
        }
    });

    $scope.setOrderBy = function (field) {
        $scope.where.orderBy = field;
        $scope.filter();
    };
    $scope.resetOrderBy = function () {
        delete $scope.where.orderBy;
        $scope.filter();
    };

    $http
        .get('/api/schedule')
        .then(function successCallback(response) {
            $scope.parts = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);