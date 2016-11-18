'use strict';

angular.module('myApp.offlinePaymentForm', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/offlinePaymentForm/:id', {
        templateUrl: 'offlinePaymentForm/offlinePaymentForm.html',
        controller: 'OfflinePaymentFormCtrl'
    });
}])

.controller('OfflinePaymentFormCtrl', ['$scope', '$http', '$location', '$routeParams', '$filter', function($scope, $http, $location, $routeParams, $filter) {
    var currentId = $filter('limitTo')($routeParams.id, 12, 1);
    $scope.requisites = {orderNumber: currentId};
    $scope.order = [];
    $scope.where = {order: currentId};

    $scope.submitCQPayment = function(){
        $http
            .post('/api/cheque', $scope.requisites)
            .then(function(){$location.path('/viewOfflinePayments')}, function(){});
    }
// TODO: add functionality to fill price to pay automatically

    $http
            .get('/api/orders', {params: $scope.where})
            .then(function successCallback(response) {
                $scope.order = response.data;
            }, function errorCallback(response) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            });

}]);