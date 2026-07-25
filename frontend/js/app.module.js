(function () {
    'use strict';

    // Em localhost (dev), usa a API local; em qualquer outro host (produção),
    // usa a API publicada no Render. Evita precisar de build/env vars no front-end estatico.
    var isLocal = ['localhost', '127.0.0.1'].indexOf(window.location.hostname) !== -1;
    var apiBaseUrl = isLocal
        ? 'http://localhost:8080/api'
        : 'https://aeronaves-api.onrender.com/api';

    angular.module('aeronavesApp', [])
        .constant('API_BASE_URL', apiBaseUrl);
})();
