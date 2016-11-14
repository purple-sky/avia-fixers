'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/view1', {
        templateUrl: 'view1/view1.html',
        controller: 'View1Ctrl'
    });
}])

.controller('View1Ctrl', ['$scope', '$http', '$location', '$rootScope',function($scope, $http, $location, $rootScope) {
    $scope.orders = [];
    $scope.where = {};
    // add when want to filter stuff by user attributes
    $scope.fixerUser = $rootScope.fixerUser;

    $scope.viewOrder = function (id) {
        $location.path('/viewOrder/:' + id);
    }

    $scope.filter = function() {
        $http
            .get('/api/orders', {params: $scope.where})
            .then(function successCallback(response) {
                        $scope.orders = response.data;
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
        .get('/api/orders')
        .then(function successCallback(response) {
            $scope.orders = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);