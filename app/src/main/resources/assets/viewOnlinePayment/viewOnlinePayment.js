'use strict';

angular.module('myApp.viewOnlinePayment', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOnlinePayment/:id', {
        templateUrl: 'viewOnlinePayment/viewOnlinePayment.html',
        controller: 'ViewOnlinePaymentCtrl'
    });
}])

.controller('ViewOnlinePaymentCtrl', ['$scope', '$http', '$routeParams', '$location', '$filter', '$rootScope', function($scope, $http, $routeParams, $location, $filter, $rootScope) {
    var currentId = $filter('limitTo')($routeParams.id, 20, 1);
    $scope.id = currentId;
    $scope.fixerUser = $rootScope.fixerUser;

    $http
        .get('/api/epayments/'+ currentId)
        .then(function successCallback(response) {
            $scope.epayment = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);