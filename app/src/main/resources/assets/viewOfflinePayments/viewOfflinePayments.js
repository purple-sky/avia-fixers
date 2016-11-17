'use strict';

angular.module('myApp.viewOfflinePayments', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOfflinePayments', {
        templateUrl: 'viewOfflinePayments/viewOfflinePayments.html',
        controller: 'ViewOfflinePaymentsCtrl'
    });
}])

.controller('ViewOfflinePaymentsCtrl', ['$scope', '$http', '$location', '$rootScope', function($scope, $http, $location, $rootScope) {
    $scope.payments = [];
    $scope.where = {};
    $scope.fixerUser = $rootScope.fixerUser;

    $scope.viewPayment = function (id) {
        $location.path('/viewOfflinePayment/:' + id);
    }

    $scope.filter = function() {
            $http
                .get('/api/cheque', {params: $scope.where})
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

    $http
        .get('/api/cheque')
        .then(function successCallback(response) {
            $scope.payments = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);