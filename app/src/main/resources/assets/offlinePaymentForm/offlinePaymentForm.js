'use strict';

angular.module('myApp.offlinePaymentForm', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/offlinePaymentForm/:id', {
        templateUrl: 'offlinePaymentForm/offlinePaymentForm.html',
        controller: 'OfflinePaymentFormCtrl'
    });
}])

.controller('OfflinePaymentFormCtrl', ['$scope', '$http', '$location', '$routeParams', '$filter',function($scope, $http, $location, $routeParams, $filter) {
    var currentId = $filter('limitTo')($routeParams.id, 12, 1);
    $scope.requisites = {orderNumber: currentId};

    $scope.submitCQPayment = function(){
        $http
            .post('/api/cheque', $scope.requisites)
            .then(function(){$location.path('/view1')}, function(){});
    }

}]);