'use strict';

angular.module('myApp.viewOfflinePayment', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOfflinePayment/:id', {
        templateUrl: 'viewOfflinePayment/viewOfflinePayment.html',
        controller: 'ViewOfflinePaymentCtrl'
    });
}])

.controller('ViewOfflinePaymentCtrl', ['$scope', '$http', '$routeParams', '$location', '$filter', function($scope, $http, $routeParams, $location, $filter) {
    $scope.cqpayments = [];
    var currId = $filter('limitTo')($routeParams.id, 15, 1);
    $scope.id = currentId;


    $http
        .get('/api/cheque/'+ currentId)
        .then(function successCallback(response) {
            $scope.cqpayments = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);