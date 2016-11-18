'use strict';

angular.module('myApp.viewReport', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/reportView', {
        templateUrl: 'viewReport/viewReport.html',
        controller: 'ViewReportCtrl'
    });
}])

.controller('ViewReportCtrl', ['$scope', '$http', '$location', '$routeParams',
                                function($scope, $http, $location, $routeParams) {
    $scope.reports = [];
    $scope.totals = {};

    $scope.getTotals = function () {
    $http
            .get('/api/report/totals')
            .then(function successCallback(response) {
                $scope.totals = response.data;
            }, function errorCallback(response) {
    });

    }

    $http
        .get('/api/report')
        .then($scope.getTotals())
        .then(function successCallback(response) {
            $scope.reports = response.data;
        }, function errorCallback(response) {
        });
}]);