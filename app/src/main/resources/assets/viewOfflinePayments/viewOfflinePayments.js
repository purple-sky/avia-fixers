'use strict';

angular.module('myApp.viewOfflinePayments', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOfflinePayments', {
        templateUrl: 'viewOfflinePayments/viewOfflinePayments.html',
        controller: 'ViewOfflinePaymentsCtrl'
    });
}])

.controller('ViewOfflinePaymentsCtrl', ['$scope', '$http', '$location',function($scope, $http, $location) {
    $scope.payments = [];

    $scope.viewPayment = function (id) {
        $location.path('/viewOfflinePayment/:' + id);
    }

    $http
        .get('/api/cheque')
        .then(function successCallback(response) {
            $scope.payments = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);