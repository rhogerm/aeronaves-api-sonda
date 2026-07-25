package com.sonda.aeronaves.service;

import com.sonda.aeronaves.dto.AeronaveRequest;
import com.sonda.aeronaves.dto.AeronaveResponse;
import com.sonda.aeronaves.dto.DecadaEstatistica;
import com.sonda.aeronaves.dto.FabricanteEstatistica;
import com.sonda.aeronaves.dto.NaoVendidasEstatistica;
import com.sonda.aeronaves.exception.AeronaveNaoEncontradaException;
import com.sonda.aeronaves.mapper.AeronaveMapper;
import com.sonda.aeronaves.model.Aeronave;
import com.sonda.aeronaves.repository.AeronaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AeronaveServiceImpl implements AeronaveService {

    private static final int DIAS_ULTIMA_SEMANA = 7;

    private final AeronaveRepository repository;
    private final AeronaveMapper mapper;

    @Override
    public List<AeronaveResponse> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<AeronaveResponse> buscarPorTermo(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodas();
        }
        return repository.buscarPorTermo(termo.trim()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AeronaveResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public AeronaveResponse criar(AeronaveRequest request) {
        Aeronave aeronave = mapper.toEntity(request);
        return mapper.toResponse(repository.save(aeronave));
    }

    @Override
    @Transactional
    public AeronaveResponse atualizar(Long id, AeronaveRequest request) {
        Aeronave aeronave = buscarEntidadePorId(id);
        mapper.atualizarEntidade(request, aeronave);
        return mapper.toResponse(repository.save(aeronave));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Aeronave aeronave = buscarEntidadePorId(id);
        repository.delete(aeronave);
    }

    @Override
    public NaoVendidasEstatistica contarNaoVendidas() {
        return new NaoVendidasEstatistica(repository.countByVendidoFalse());
    }

    @Override
    public List<DecadaEstatistica> contarPorDecada() {
        return repository.contarPorDecada().stream()
                .map(linha -> new DecadaEstatistica(
                        ((Number) linha[0]).intValue() + "s",
                        ((Number) linha[1]).longValue()))
                .toList();
    }

    @Override
    public List<FabricanteEstatistica> contarPorFabricante() {
        return repository.contarPorFabricante().stream()
                .map(linha -> new FabricanteEstatistica(
                        (String) linha[0],
                        ((Number) linha[1]).longValue()))
                .toList();
    }

    @Override
    public List<AeronaveResponse> registradasUltimaSemana() {
        LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_ULTIMA_SEMANA);
        return repository.findByCreatedGreaterThanEqualOrderByCreatedDesc(desde).stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Aeronave buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AeronaveNaoEncontradaException(id));
    }
}
