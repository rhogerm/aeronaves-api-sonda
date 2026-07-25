package com.sonda.aeronaves.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonda.aeronaves.dto.AeronaveResponse;
import com.sonda.aeronaves.dto.NaoVendidasEstatistica;
import com.sonda.aeronaves.exception.AeronaveNaoEncontradaException;
import com.sonda.aeronaves.service.AeronaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AeronaveController.class)
class AeronaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AeronaveService service;

    private AeronaveResponse respostaExemplo() {
        return new AeronaveResponse(1L, "E2-190", "Embraer", 2014, "Jato comercial", false,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void listarDeveRetornarOk() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(respostaExemplo()));

        mockMvc.perform(get("/api/aeronaves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Embraer"));
    }

    @Test
    void buscarPorIdDeveRetornar404QuandoNaoExiste() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new AeronaveNaoEncontradaException(99L));

        mockMvc.perform(get("/api/aeronaves/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Aeronave nao encontrada para o id: 99"));
    }

    @Test
    void criarComFabricanteInvalidoDeveRetornar400() throws Exception {
        String corpoInvalido = """
                {
                    "nome": "Modelo X",
                    "marca": "Enbraer",
                    "ano": 2020,
                    "descricao": "teste",
                    "vendido": false
                }
                """;

        mockMvc.perform(post("/api/aeronaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados invalidos"));
    }

    @Test
    void criarComDadosValidosDeveRetornar201() throws Exception {
        when(service.criar(any())).thenReturn(respostaExemplo());

        String corpoValido = """
                {
                    "nome": "E2-190",
                    "marca": "Embraer",
                    "ano": 2014,
                    "descricao": "Jato comercial",
                    "vendido": false
                }
                """;

        mockMvc.perform(post("/api/aeronaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoValido))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value("Embraer"));
    }

    @Test
    void excluirDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/aeronaves/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void naoVendidasDeveRetornarQuantidade() throws Exception {
        when(service.contarNaoVendidas()).thenReturn(new NaoVendidasEstatistica(7L));

        mockMvc.perform(get("/api/aeronaves/estatisticas/nao-vendidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(7));
    }
}
