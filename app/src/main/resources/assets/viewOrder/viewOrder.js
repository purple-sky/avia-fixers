'use strict';

angular.module('myApp.viewOrder', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewOrder/:id', {
        templateUrl: 'viewOrder/viewOrder.html',
        controller: 'ViewOrderCtrl'
    });
}])

.controller('ViewOrderCtrl', ['$scope', '$http', '$routeParams', '$location', '$rootScope', function($scope, $http, $routeParams, $location, $rootScope) {
    $scope.order = [];
    var currentId = $routeParams.id;
    $scope.id = currentId;
    $scope.fixerUser = $rootScope.fixerUser;

    $scope.onlinePayment = function (id) {
        $location.path('/onlinePaymentForm/' + id);
    }

    $scope.offlinePayment = function (id) {
            $location.path('/offlinePaymentForm/' + id);
    }

    $scope.updatePart = function (id) {
            $location.path('/viewPart/:' + id);
    }

    $scope.deletePart = function (id) {
                $http
                     .delete('/api/parts/'+ id)
                     .then(function(){$location.path('/view1')}, function(){});
    }


    $scope.acceptOrder = function (id) {
        $http
            .put('/api/orders/'+ id, {status: "InProgress"})
            .then(function(){$location.path('/viewOrder/:'+currId)}, function(){});
    }

    $scope.deleteOrder = function (id) {
            $http
                .delete('/api/orders/'+ id)
                .then(function(){$location.path('/view1')}, function(){});
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