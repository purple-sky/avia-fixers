'use strict';

angular.module('myApp.viewPayments', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewPayments', {
        templateUrl: 'viewPayments/viewPayments.html',
        controller: 'ViewPaymentsCtrl'
    });
}])

.controller('ViewPaymentsCtrl', ['$scope', '$http', '$location',function($scope, $http, $location) {
    $scope.payments = [];

    $scope.viewPayment = function (id, type) {
        switch (type) {
            case "Online":
                $location.path('/viewOnlinePayment/:' + id);
                break;
            case "Offline":
                $location.path('/viewOfflinePayment/:' + id);
        }
    }

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