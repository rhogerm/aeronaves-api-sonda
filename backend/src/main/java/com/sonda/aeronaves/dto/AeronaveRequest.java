package com.sonda.aeronaves.dto;

import com.sonda.aeronaves.validation.FabricanteValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeronaveRequest(

        @NotBlank(message = "O nome (modelo) da aeronave e obrigatorio")
        @Schema(example = "E2-190")
        String nome,

        @NotBlank(message = "O fabricante e obrigatorio")
        @FabricanteValido
        @Schema(example = "Embraer")
        String marca,

        @NotNull(message = "O ano de fabricacao e obrigatorio")
        @Min(value = 1903, message = "Ano de fabricacao invalido")
        @Max(value = 2100, message = "Ano de fabricacao invalido")
        @Schema(example = "2014")
        Integer ano,

        @Schema(example = "Aeronave comercial de fuselagem estreita")
        String descricao,

        @NotNull(message = "E preciso informar se a aeronave foi vendida")
        @Schema(example = "false")
        Boolean vendido
) {
}
