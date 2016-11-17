'use strict';

angular.module('myApp.viewPayments', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewPayments', {
        templateUrl: 'viewPayments/viewPayments.html',
        controller: 'ViewPaymentsCtrl'
    });
}])

.controller('ViewPaymentsCtrl', ['$scope', '$http', '$location', '$rootScope', function($scope, $http, $location, $rootScope) {
    $scope.payments = [];
    $scope.where = {};
    $scope.fixerUser = $rootScope.fixerUser;

    $scope.viewPayment = function (id, type) {
        switch (type) {
            case "Online":
                $location.path('/viewOnlinePayment/:' + id);
                break;
            case "Offline":
                $location.path('/viewOfflinePayment/:' + id);
        }
    }

    $scope.filter = function() {
        $http
            .get('/api/payments', {params: $scope.where})
            .then(function successCallback(response) {
                $scope.payments = response.data;
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

    $scope.toOnline = function () {
        $location.path('/viewOnlinePayments');
    }

    $scope.toOffline = function () {
            $location.path('/viewOfflinePayments');
        }

    $http
        .get('/api/payments')
        .then(function successCallback(response) {
            $scope.payments = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);