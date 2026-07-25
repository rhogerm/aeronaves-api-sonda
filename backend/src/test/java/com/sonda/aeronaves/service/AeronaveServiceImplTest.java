package com.sonda.aeronaves.service;

import com.sonda.aeronaves.dto.AeronaveRequest;
import com.sonda.aeronaves.dto.AeronaveResponse;
import com.sonda.aeronaves.dto.DecadaEstatistica;
import com.sonda.aeronaves.dto.FabricanteEstatistica;
import com.sonda.aeronaves.exception.AeronaveNaoEncontradaException;
import com.sonda.aeronaves.mapper.AeronaveMapper;
import com.sonda.aeronaves.mapper.AeronaveMapperImpl;
import com.sonda.aeronaves.model.Aeronave;
import com.sonda.aeronaves.repository.AeronaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AeronaveServiceImplTest {

    @Mock
    private AeronaveRepository repository;

    private final AeronaveMapper mapper = new AeronaveMapperImpl();

    private AeronaveService service;

    @BeforeEach
    void setUp() {
        service = new AeronaveServiceImpl(repository, mapper);
    }

    private Aeronave aeronaveExemplo() {
        return Aeronave.builder()
                .id(1L)
                .nome("E2-190")
                .marca("Embraer")
                .ano(2014)
                .descricao("Jato comercial")
                .vendido(false)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }

    @Test
    void deveCriarAeronave() {
        AeronaveRequest request = new AeronaveRequest("E2-190", "Embraer", 2014, "Jato comercial", false);
        when(repository.save(any(Aeronave.class))).thenReturn(aeronaveExemplo());

        AeronaveResponse resposta = service.criar(request);

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.marca()).isEqualTo("Embraer");
        verify(repository, times(1)).save(any(Aeronave.class));
    }

    @Test
    void deveBuscarPorIdQuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(aeronaveExemplo()));

        AeronaveResponse resposta = service.buscarPorId(1L);

        assertThat(resposta.nome()).isEqualTo("E2-190");
    }

    @Test
    void deveLancarExcecaoQuandoAeronaveNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(AeronaveNaoEncontradaException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveAtualizarAeronaveExistente() {
        Aeronave existente = aeronaveExemplo();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Aeronave.class))).thenAnswer(inv -> inv.getArgument(0));

        AeronaveRequest request = new AeronaveRequest("E2-190 (atualizado)", "Embraer", 2014, "Nova descricao", true);
        AeronaveResponse resposta = service.atualizar(1L, request);

        assertThat(resposta.nome()).isEqualTo("E2-190 (atualizado)");
        assertThat(resposta.vendido()).isTrue();
    }

    @Test
    void deveExcluirAeronaveExistente() {
        Aeronave existente = aeronaveExemplo();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(repository, times(1)).delete(existente);
    }

    @Test
    void naoDeveExcluirQuandoAeronaveNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(AeronaveNaoEncontradaException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    void deveContarNaoVendidas() {
        when(repository.countByVendidoFalse()).thenReturn(5L);

        assertThat(service.contarNaoVendidas().quantidade()).isEqualTo(5L);
    }

    @Test
    void deveAgruparPorDecada() {
        when(repository.contarPorDecada()).thenReturn(List.of(
                new Object[]{1990, 2L},
                new Object[]{2000, 3L}
        ));

        List<DecadaEstatistica> resultado = service.contarPorDecada();

        assertThat(resultado).containsExactly(
                new DecadaEstatistica("1990s", 2L),
                new DecadaEstatistica("2000s", 3L)
        );
    }

    @Test
    void deveAgruparPorFabricante() {
        when(repository.contarPorFabricante()).thenReturn(List.of(
                new Object[]{"Embraer", 14L},
                new Object[]{"Boeing", 8L}
        ));

        List<FabricanteEstatistica> resultado = service.contarPorFabricante();

        assertThat(resultado).containsExactly(
                new FabricanteEstatistica("Embraer", 14L),
                new FabricanteEstatistica("Boeing", 8L)
        );
    }

    @Test
    void deveRetornarVazioAoBuscarPorTermoEmBranco() {
        when(repository.findAll()).thenReturn(List.of(aeronaveExemplo()));

        List<AeronaveResponse> resultado = service.buscarPorTermo("   ");

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }
}
