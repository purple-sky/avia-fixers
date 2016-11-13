'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'myApp.loginForm',
    'myApp.view1',
    'myApp.view2',
    'myApp.viewParts',
    'myApp.viewOrder',
    'myApp.onlinePaymentForm',
    'myApp.offlinePaymentForm',
    'myApp.viewPart',
    'myApp.viewPayments',
    'myApp.viewOnlinePayments',
    'myApp.viewOfflinePayments',
    'myApp.viewOnlinePayment',
    'myApp.viewOfflinePayment',
    'myApp.viewRepair',
    'myApp.viewRepairs',
    'myApp.version'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!');
    // TODO: change route to loginForm
    $routeProvider.otherwise({redirectTo: '/view1'});
}]).
run(['$rootScope', '$location', function($rootScope, $location) {
    $rootScope.$on('$routeChangeStart', function (event, next) {
        if ($rootScope.fixerUser == undefined) {
            $location.path('/login');
        }
    });
}]).
controller('MainCtrl', ['$scope', '$rootScope', '$location', function($scope, $rootScope, $location) {
    $rootScope.$watch('fixerUser', function(){
        $scope.fixerUser = $rootScope.fixerUser;
    });

    $scope.logout = function () {
        delete $rootScope.fixerUser;
        $location.path('/login');
    }
}]);
