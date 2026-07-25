package com.sonda.aeronaves.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {
    public ApiError(int status, String erro, String mensagem, String caminho) {
        this(LocalDateTime.now(), status, erro, mensagem, caminho, List.of());
    }

    public ApiError(int status, String erro, String mensagem, String caminho, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, caminho, detalhes);
    }
}
