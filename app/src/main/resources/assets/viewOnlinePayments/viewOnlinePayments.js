'use strict';

angular.module('myApp.viewOnlinePayments', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOnlinePayments', {
        templateUrl: 'viewOnlinePayments/viewOnlinePayments.html',
        controller: 'ViewOnlinePaymentsCtrl'
    });
}])

.controller('ViewOnlinePaymentsCtrl', ['$scope', '$http', '$location',function($scope, $http, $location) {
    $scope.payments = [];

    $scope.viewPayment = function (id) {
        $location.path('/viewOnlinePayment/:' + id);
    }

    $http
        .get('/api/epayments')
        .then(function successCallback(response) {
            $scope.payments = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);