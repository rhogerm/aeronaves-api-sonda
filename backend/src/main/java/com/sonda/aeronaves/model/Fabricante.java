package com.sonda.aeronaves.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Whitelist fechada dos fabricantes aceitos no cadastro.
 * Garante consistencia dos nomes (requisito do desafio: "Enbraer", "Boing", "ErBus" nao podem ser aceitos).
 * Qualquer marca fora desta lista e rejeitada na validacao (ver {@link com.sonda.aeronaves.validation.FabricanteValido}).
 */
public enum Fabricante {

    EMBRAER("Embraer"),
    BOEING("Boeing"),
    AIRBUS("Airbus"),
    BOMBARDIER("Bombardier"),
    CESSNA("Cessna"),
    ATR("ATR"),
    GULFSTREAM("Gulfstream"),
    DASSAULT("Dassault"),
    LOCKHEED_MARTIN("Lockheed Martin"),
    PIPER("Piper"),
    TEXTRON_AVIATION("Textron Aviation"),
    SAAB("Saab"),
    ANTONOV("Antonov"),
    DE_HAVILLAND("De Havilland");

    private final String nomeExibicao;

    Fabricante(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    /**
     * Resolve um fabricante a partir do texto informado pelo usuario, ignorando
     * caixa e espacos nas extremidades. Retorna vazio se nao houver correspondencia exata
     * com um fabricante da whitelist.
     */
    public static Optional<Fabricante> fromNomeExibicao(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String normalizado = texto.trim();
        return Arrays.stream(values())
                .filter(f -> f.nomeExibicao.equalsIgnoreCase(normalizado))
                .findFirst();
    }
}
