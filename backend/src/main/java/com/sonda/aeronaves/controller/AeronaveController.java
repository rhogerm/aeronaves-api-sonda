package com.sonda.aeronaves.controller;

import com.sonda.aeronaves.dto.AeronaveRequest;
import com.sonda.aeronaves.dto.AeronaveResponse;
import com.sonda.aeronaves.dto.DecadaEstatistica;
import com.sonda.aeronaves.dto.FabricanteEstatistica;
import com.sonda.aeronaves.dto.NaoVendidasEstatistica;
import com.sonda.aeronaves.service.AeronaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aeronaves")
@RequiredArgsConstructor
@Tag(name = "Aeronaves", description = "Cadastro e estatisticas de aeronaves")
public class AeronaveController {

    private final AeronaveService service;

    @GetMapping
    @Operation(summary = "Lista todas as aeronaves")
    public List<AeronaveResponse> listar() {
        return service.listarTodas();
    }

    @GetMapping("/find")
    @Operation(summary = "Busca aeronaves por termo (nome, marca ou id)")
    public List<AeronaveResponse> buscarPorTermo(@RequestParam("termo") String termo) {
        return service.buscarPorTermo(termo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna os detalhes de uma aeronave")
    public AeronaveResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova aeronave")
    public ResponseEntity<AeronaveResponse> criar(@Valid @RequestBody AeronaveRequest request) {
        AeronaveResponse criada = service.criar(request);
        return ResponseEntity.created(URI.create("/api/aeronaves/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma aeronave")
    public AeronaveResponse atualizar(@PathVariable Long id, @Valid @RequestBody AeronaveRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma aeronave")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/estatisticas/nao-vendidas")
    @Operation(summary = "Quantidade de aeronaves ainda nao vendidas")
    public NaoVendidasEstatistica naoVendidas() {
        return service.contarNaoVendidas();
    }

    @GetMapping("/estatisticas/por-decada")
    @Operation(summary = "Distribuicao de aeronaves por decada de fabricacao")
    public List<DecadaEstatistica> porDecada() {
        return service.contarPorDecada();
    }

    @GetMapping("/estatisticas/por-fabricante")
    @Operation(summary = "Distribuicao de aeronaves por fabricante")
    public List<FabricanteEstatistica> porFabricante() {
        return service.contarPorFabricante();
    }

    @GetMapping("/estatisticas/ultima-semana")
    @Operation(summary = "Aeronaves registradas nos ultimos 7 dias")
    public List<AeronaveResponse> ultimaSemana() {
        return service.registradasUltimaSemana();
    }
}
