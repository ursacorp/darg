darg.controller('DargUserCtrl', 
    ['$cookies',
     '$cookieStore',
     '$location',
     '$scope', 
     '$http',
     '$routeParams',
     'user',
     function(
         $cookies,
         $cookieStore,
         $location,
         $scope, 
         $http, 
         $routeParams,
         user) {

    $scope.loggedIn = user.loggedIn
    $scope.CurrentUser = {};
    $scope.LoginForm = {
        email: "",
        password: ""
    };

    $scope.Login = function() {
        $http({
            method: "post",
            url: '/api/v1/login', 
            data: $.param($scope.LoginForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.getCurrentUser();
        })
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
            $location.path('/');
        })
    };

    getDefaultTeam = function() {
        if (user.info != null) {
            if (user.info.teams.length == 0) {
                return null; 
            } else if ($routeParams.teamId != null) {
                return $routeParams.teamId
            } else {
                return user.info.teams[0].id;
            }
        };
    };

    $scope.getCurrentUser = function() {
        $http({
            method: "get",
            url: "/api/v1/user"
        })
        .success(function(data) {
            user.info = data;
            $scope.CurrentUser = data;
            user.team = getDefaultTeam();
        })
    };

    $scope.gravatars = {
        "navbar": null,
        "timeline": null
    }

    $scope.loadGravatar = function(target, size) {
        $http({
            method: "post",
            data: $.param({"size": size}),
            url: "/api/v1/gravatar",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data, status) {
            $scope.gravatars[target] = data;
        });
    };

    $scope.ResetForm = {
        "email": ""
    }

    $scope.LoadPasswordResetPage = function() {
        $location.path('/password_reset');
    };

    $scope.resetPassword = function() {
        $http({
            method: "post",
            url: "/api/v1/password_reset",
            data: $.param($scope.ResetForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            // TODO: Provide a tooltip or something on success?
            // No, replace the entire speech bubble
        })
        .error(function(data) {
            console.log("Failed to reset password");
            console.log(data);
        });
    };

    /* watchers */
    $scope.$watch(function() {
        return user.loggedIn()
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true) {
            $scope.getCurrentUser();
            $scope.loadGravatar("navbar", 40);
            $scope.loadGravatar("timeline", 100);
        }
    });

    $scope.$watch(function() {
        return getDefaultTeam()
    }, function(oldValue, newValue) {
        user.team = getDefaultTeam();
    });
}]);

