(function() {
    'use strict';

    angular
        .module('pgsqltestApp')
        .controller('ActivityDialogController', ActivityDialogController);

    ActivityDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Activity'];

    function ActivityDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Activity) {
        var vm = this;

        vm.activity = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.activity.id !== null) {
                Activity.update(vm.activity, onSaveSuccess, onSaveError);
            } else {
                Activity.save(vm.activity, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pgsqltestApp:activityUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
