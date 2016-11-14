'use strict';

angular.module('myApp.viewParts', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/viewParts', {
        templateUrl: 'viewParts/viewParts.html',
        controller: 'ViewPartsCtrl'
    });
}])

.controller('ViewPartsCtrl', ['$scope', '$http', '$location', '$rootScope', function($scope, $http, $location, $rootScope) {
    $scope.parts = [];
    $scope.where = {};
    $scope.fixerUser = $rootScope.fixerUser;

    $scope.updatePart = function (id) {
            $location.path('/viewPart/:' + id);
    }

    $scope.deletePart = function (id) {
                $http
                    .delete('/api/parts/'+ id)
                    .then(function(){$location.path('/viewParts')}, function(){});
    }

    $scope.filter = function() {
        $http
            .get('/api/parts', {params: $scope.where})
            .then(function successCallback(response) {
                        $scope.parts = response.data;
                    }, function errorCallback(response) {}
                    );

    }

    $scope.$watch('where.status', function () {
            if($scope.where.status == 'all'){
                delete $scope.where.status;
            }
    });

    $scope.setOrderBy = function (field) {
            $scope.where.orderBy = field;
            $scope.filter();
    };
    $scope.resetOrderBy = function () {
            delete $scope.where.orderBy;
            $scope.filter();
    };

    $http
        .get('/api/parts')
        .then(function successCallback(response) {
            $scope.parts = response.data;
        }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
    });


}]);