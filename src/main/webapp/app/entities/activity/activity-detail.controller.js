(function() {
    'use strict';

    angular
        .module('pgsqltestApp')
        .controller('ActivityDetailController', ActivityDetailController);

    ActivityDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Activity'];

    function ActivityDetailController($scope, $rootScope, $stateParams, previousState, entity, Activity) {
        var vm = this;

        vm.activity = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pgsqltestApp:activityUpdate', function(event, result) {
            vm.activity = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
