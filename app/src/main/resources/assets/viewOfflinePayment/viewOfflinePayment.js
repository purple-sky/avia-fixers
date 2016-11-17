'use strict';

angular.module('myApp.viewOfflinePayment', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOfflinePayment/:id', {
        templateUrl: 'viewOfflinePayment/viewOfflinePayment.html',
        controller: 'ViewOfflinePaymentCtrl'
    });
}])

.controller('ViewOfflinePaymentCtrl', ['$scope', '$http', '$routeParams', '$location', '$filter', '$rootScope', function($scope, $http, $routeParams, $location, $filter, $rootScope) {
    var currentId = $filter('limitTo')($routeParams.id, 20, 1);
    $scope.id = currentId;
    $scope.fixerUser = $rootScope.fixerUser;


    $http
        .get('/api/cheque/'+ currentId)
        .then(function successCallback(response) {
            $scope.cqpayment = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);