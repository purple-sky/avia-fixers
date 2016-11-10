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
  'myApp.version'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  $locationProvider.hashPrefix('!');

// TODO: change route to loginForm
  $routeProvider.otherwise({redirectTo: '/view1'});
}]);
