package com.sonda.aeronaves.exception;

public class AeronaveNaoEncontradaException extends RuntimeException {

    public AeronaveNaoEncontradaException(Long id) {
        super("Aeronave nao encontrada para o id: " + id);
    }
}
