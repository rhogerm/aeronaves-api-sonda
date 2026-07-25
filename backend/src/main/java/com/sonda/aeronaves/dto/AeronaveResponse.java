package com.sonda.aeronaves.dto;

import java.time.LocalDateTime;

public record AeronaveResponse(
        Long id,
        String nome,
        String marca,
        Integer ano,
        String descricao,
        boolean vendido,
        LocalDateTime created,
        LocalDateTime updated
) {
}
