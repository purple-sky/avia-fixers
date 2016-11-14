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

    $http
        .get('/api/report')
        .then(function successCallback(response) {
            $scope.reports = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        });
}]);