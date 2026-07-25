package com.sonda.aeronaves.service;

import com.sonda.aeronaves.dto.AeronaveRequest;
import com.sonda.aeronaves.dto.AeronaveResponse;
import com.sonda.aeronaves.dto.DecadaEstatistica;
import com.sonda.aeronaves.dto.FabricanteEstatistica;
import com.sonda.aeronaves.dto.NaoVendidasEstatistica;

import java.util.List;

public interface AeronaveService {

    List<AeronaveResponse> listarTodas();

    List<AeronaveResponse> buscarPorTermo(String termo);

    AeronaveResponse buscarPorId(Long id);

    AeronaveResponse criar(AeronaveRequest request);

    AeronaveResponse atualizar(Long id, AeronaveRequest request);

    void excluir(Long id);

    NaoVendidasEstatistica contarNaoVendidas();

    List<DecadaEstatistica> contarPorDecada();

    List<FabricanteEstatistica> contarPorFabricante();

    List<AeronaveResponse> registradasUltimaSemana();
}
