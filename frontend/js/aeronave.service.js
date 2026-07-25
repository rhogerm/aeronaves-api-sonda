(function () {
    'use strict';

    angular.module('aeronavesApp')
        .factory('AeronaveService', AeronaveService);

    AeronaveService.$inject = ['$http', 'API_BASE_URL'];

    function AeronaveService($http, API_BASE_URL) {
        var base = API_BASE_URL + '/aeronaves';

        return {
            listar: listar,
            buscarPorTermo: buscarPorTermo,
            buscarPorId: buscarPorId,
            criar: criar,
            atualizar: atualizar,
            excluir: excluir,
            naoVendidas: naoVendidas,
            porDecada: porDecada,
            porFabricante: porFabricante,
            ultimaSemana: ultimaSemana
        };

        function listar() {
            return $http.get(base).then(respostaDados);
        }

        function buscarPorTermo(termo) {
            return $http.get(base + '/find', {params: {termo: termo}}).then(respostaDados);
        }

        function buscarPorId(id) {
            return $http.get(base + '/' + id).then(respostaDados);
        }

        function criar(aeronave) {
            return $http.post(base, aeronave).then(respostaDados);
        }

        function atualizar(id, aeronave) {
            return $http.put(base + '/' + id, aeronave).then(respostaDados);
        }

        function excluir(id) {
            return $http.delete(base + '/' + id);
        }

        function naoVendidas() {
            return $http.get(base + '/estatisticas/nao-vendidas').then(respostaDados);
        }

        function porDecada() {
            return $http.get(base + '/estatisticas/por-decada').then(respostaDados);
        }

        function porFabricante() {
            return $http.get(base + '/estatisticas/por-fabricante').then(respostaDados);
        }

        function ultimaSemana() {
            return $http.get(base + '/estatisticas/ultima-semana').then(respostaDados);
        }

        function respostaDados(response) {
            return response.data;
        }
    }
})();
