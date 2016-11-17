'use strict';

angular.module('myApp.orderForm', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/orderForm', {
        templateUrl: 'orderForm/orderForm.html',
        controller: 'OrderFormCtrl'
    });
}])

.controller('OrderFormCtrl', ['$scope', '$http', '$location', '$rootScope', function($scope, $http, $location, $rootScope) {
    $scope.parts = new Array(1);

    $scope.addPart = function() {
        $scope.parts.push({});
    };

    $scope.submitOrder = function(){
        $http
            // customerID below is a plug-in, real ID is retrieved on server side
            .post('/api/orders', {parts: $scope.parts, customerId: 200048001})
            .then(function(){$location.path('/view1')}, function(){});
    }
}]);