(function () {
    'use strict';

    angular.module('aeronavesApp')
        .controller('AeronaveController', AeronaveController);

    AeronaveController.$inject = ['AeronaveService'];

    function AeronaveController(AeronaveService) {
        var vm = this;

        // Espelha a whitelist de fabricantes definida no backend e ordena alfabeticamente
        vm.fabricantes = [
            'Embraer', 'Boeing', 'Airbus', 'Bombardier', 'Cessna', 'ATR',
            'Gulfstream', 'Dassault', 'Lockheed Martin', 'Piper',
            'Textron Aviation', 'Saab', 'Antonov', 'De Havilland'
        ].sort(function (a, b) {
            return a.localeCompare(b);
        });

        vm.aeronaves = [];
        vm.stats = {naoVendidas: {}, porDecada: [], porFabricante: [], ultimaSemana: []};
        vm.form = formVazio();
        vm.emEdicao = false;
        vm.carregando = false;
        vm.salvando = false;
        vm.termoBusca = '';
        vm.mensagemErro = null;
        vm.mensagemSucesso = null;

        vm.salvar = salvar;
        vm.editar = editar;
        vm.cancelarEdicao = cancelarEdicao;
        vm.excluir = excluir;
        vm.buscar = buscar;
        vm.limparBusca = limparBusca;
        vm.carregarTudo = carregarTudo;

        ativar();

        function ativar() {
            carregarTudo();
        }

        function formVazio() {
            return {id: null, nome: '', marca: '', ano: null, descricao: '', vendido: false};
        }

        function carregarTudo() {
            vm.carregando = true;
            return buscar().finally(function () {
                return carregarStats();
            }).finally(function () {
                vm.carregando = false;
            });
        }

        function carregarStats() {
            return AeronaveService.naoVendidas().then(function (dados) {
                vm.stats.naoVendidas = dados;
            }).then(function () {
                return AeronaveService.porDecada();
            }).then(function (dados) {
                vm.stats.porDecada = dados;
                return AeronaveService.porFabricante();
            }).then(function (dados) {
                vm.stats.porFabricante = dados;
                return AeronaveService.ultimaSemana();
            }).then(function (dados) {
                vm.stats.ultimaSemana = dados;
            }).catch(tratarErro);
        }

        function buscar() {
            var promessa = vm.termoBusca && vm.termoBusca.trim()
                ? AeronaveService.buscarPorTermo(vm.termoBusca.trim())
                : AeronaveService.listar();

            return promessa.then(function (dados) {
                vm.aeronaves = dados;
            }).catch(tratarErro);
        }

        function limparBusca() {
            vm.termoBusca = '';
            buscar();
        }

        function salvar() {
            vm.salvando = true;
            vm.mensagemErro = null;

            var payload = {
                nome: vm.form.nome,
                marca: vm.form.marca,
                ano: Number(vm.form.ano),
                descricao: vm.form.descricao,
                vendido: !!vm.form.vendido
            };

            var operacao = vm.emEdicao
                ? AeronaveService.atualizar(vm.form.id, payload)
                : AeronaveService.criar(payload);

            operacao.then(function () {
                vm.mensagemSucesso = vm.emEdicao ? 'Aeronave atualizada com sucesso.' : 'Aeronave cadastrada com sucesso.';
                cancelarEdicao();
                return carregarTudo();
            }).catch(tratarErro).finally(function () {
                vm.salvando = false;
            });
        }

        function editar(aeronave) {
            vm.emEdicao = true;
            vm.form = {
                id: aeronave.id,
                nome: aeronave.nome,
                marca: aeronave.marca,
                ano: aeronave.ano,
                descricao: aeronave.descricao,
                vendido: aeronave.vendido
            };
            window.scrollTo({top: 0, behavior: 'smooth'});
        }

        function cancelarEdicao() {
            vm.emEdicao = false;
            vm.form = formVazio();
        }

        function excluir(aeronave) {
            AeronaveService.excluir(aeronave.id).then(function () {
                vm.mensagemSucesso = 'Aeronave #' + aeronave.id + ' removida.';
                return carregarTudo();
            }).catch(tratarErro);
        }

        function tratarErro(erro) {
            var dados = erro && erro.data;
            if (dados && dados.detalhes && dados.detalhes.length) {
                vm.mensagemErro = dados.detalhes.join(' | ');
            } else if (dados && dados.mensagem) {
                vm.mensagemErro = dados.mensagem;
            } else {
                vm.mensagemErro = 'Nao foi possivel completar a operacao. Verifique se a API esta em execucao.';
            }
        }
    }
})();
