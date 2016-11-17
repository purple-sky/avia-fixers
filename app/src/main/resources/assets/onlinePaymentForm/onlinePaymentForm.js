'use strict';

angular.module('myApp.onlinePaymentForm', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/onlinePaymentForm/:id', {
        templateUrl: 'onlinePaymentForm/onlinePaymentForm.html',
        controller: 'OnlinePaymentFormCtrl'
    });
}])

.controller('OnlinePaymentFormCtrl', ['$scope', '$http', '$location', '$routeParams', '$filter', function($scope, $http, $location, $routeParams, $filter) {
    var currentId = $filter('limitTo')($routeParams.id, 15, 1);
    $scope.requisites = {orderNumber: currentId};

    // TODO: add functionality to fill price to pay automatically

    /*$scope.order = {};

    $scope.getOrder = function () {
        $path
        .get('/api/orders/'+ currentId)
        .then(function successCallback(response) {
            $scope.order = response.data;
            }, function errorCallback(response) {});
    }*/


    $scope.submitEPayment = function(){
        $http
            .post('/api/epayments', $scope.requisites)
            .then(function(){$location.path('/viewOnlinePayments')}, function(){});
    }


}]);