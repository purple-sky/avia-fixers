'use strict';

angular.module('myApp.viewOnlinePayment', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOnlinePayment/:id', {
        templateUrl: 'viewOnlinePayment/viewOnlinePayment.html',
        controller: 'ViewOnlinePaymentCtrl'
    });
}])

.controller('ViewOnlinePaymentCtrl', ['$scope', '$http', '$routeParams', '$location', '$filter', function($scope, $http, $routeParams, $location, $filter) {
    $scope.epayments = [];
    var currId = $filter('limitTo')($routeParams.id, 15, 1);
    $scope.id = currentId;


    $http
        .get('/api/epayments/'+ currentId)
        .then(function successCallback(response) {
            $scope.epayments = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);