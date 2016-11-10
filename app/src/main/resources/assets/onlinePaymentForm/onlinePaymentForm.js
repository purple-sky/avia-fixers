'use strict';

angular.module('myApp.onlinePaymentForm', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/onlinePaymentForm/:id', {
        templateUrl: 'onlinePaymentForm/onlinePaymentForm.html',
        controller: 'OnlinePaymentFormCtrl'
    });
}])

.controller('OnlinePaymentFormCtrl', ['$scope', '$http', '$location', '$routeParams', '$filter',function($scope, $http, $location, $routeParams, $filter) {
    var currentId = $filter('limitTo')($routeParams.id, 12, 1);
    $scope.requisites = {orderNumber: currentId};


    /*$scope.addPart = function() {
        $scope.parts.push({});
    };
    */

    $scope.submitEPayment = function(){
        $http
            .post('/api/epayments', $scope.requisites)
            .then(function(){$location.path('/view1')}, function(){});
    }

    // TODO : add checks for payments that customer exists, and we don't pay for same order twice
}]);