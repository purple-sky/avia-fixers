'use strict';

angular.module('myApp.loginForm', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/loginForm', {
        templateUrl: 'loginForm/loginForm.html',
        controller: 'LoginFormCtrl'
    });
}])

.controller('LoginFormCtrl', ['$scope', '$http', '$location', function($scope, $http, $location) {
    $scope.login = {};

    $scope.logIn = function(){
        $http
            .get('/api/login', $scope.login)
            .then(function(){$location.path('/view1')}, function(){});
    }
}]);