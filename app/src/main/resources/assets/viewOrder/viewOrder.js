'use strict';

angular.module('myApp.viewOrder', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOrder/:id', {
        templateUrl: 'viewOrder/viewOrder.html',
        controller: 'ViewOrderCtrl'
    });
}])

.controller('ViewOrderCtrl', ['$scope', '$http', '$routeParams', '$location', function($scope, $http, $routeParams, $location) {
    $scope.order = [];
    var currentId = $routeParams.id;
    $scope.id = currentId;

    $scope.onlinePayment = function (id) {
        $location.path('/onlinePaymentForm/' + id);
    }

    $scope.offlinePayment = function (id) {
            $location.path('/offlinePaymentForm/' + id);
    }

    $scope.updatePart = function (id) {
            $location.path('/viewPart/:' + id);
    }

    $scope.acceptOrder = function (id) {
        $http
            .put('/api/orders/'+ id, {status: "InProgress"})
    }

    $scope.acceptOrder = function (id) {
            $http
                .delete('/api/orders/'+ id)
    }

    $http
        .get('/api/orders/'+ currentId)
        .then(function successCallback(response) {
            $scope.order = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);