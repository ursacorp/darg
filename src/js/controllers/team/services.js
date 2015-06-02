darg.controller('DargTeamServicesCtrl',
    [
    '$location',
    '$routeParams',
    '$scope',
    'team',
    function(
        $location,
        $routeParams,
        $scope,
        team) {

    var self = this;
    $scope.teamPage = "services";

    /*
     * Controller model
     */
    this.currentTeam = {};

    this.goToGitHubSettingsPage = function(team) {
      url = '/team/' + team.id + '/services/github'
      $location.path(url)
    };

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return team.currentTeam
    }, function(newValue, oldValue) {
        if (newValue != {}) {
          self.currentTeam = team.currentTeam;
        }
    });
}]);
