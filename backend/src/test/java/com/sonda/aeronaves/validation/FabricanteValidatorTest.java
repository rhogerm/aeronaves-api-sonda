package com.sonda.aeronaves.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FabricanteValidatorTest {

    private final FabricanteValidator validator = new FabricanteValidator();

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Embraer", "embraer", "  Boeing  ", "AIRBUS", "Lockheed Martin", "ATR"})
    void deveAceitarFabricantesDaWhitelist(String marca) {
        assertThat(validator.isValid(marca, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Enbraer", "Boing", "ErBus", "Airbuss", "Fabricante Desconhecido"})
    void deveRejeitarFabricantesForaDaWhitelist(String marca) {
        assertThat(validator.isValid(marca, context)).isFalse();
    }

    @Test
    void deveAceitarValorNuloOuVazioDeixandoObrigatoriedadeParaOutraAnotacao() {
        assertThat(validator.isValid(null, context)).isTrue();
        assertThat(validator.isValid("", context)).isTrue();
        assertThat(validator.isValid("   ", context)).isTrue();
    }
}
