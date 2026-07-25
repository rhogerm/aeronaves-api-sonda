package com.sonda.aeronaves.validation;

import com.sonda.aeronaves.model.Fabricante;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FabricanteValidator implements ConstraintValidator<FabricanteValido, String> {

    private static final String LISTA_FABRICANTES = Arrays.stream(Fabricante.values())
            .map(Fabricante::getNomeExibicao)
            .collect(Collectors.joining(", "));

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true; // @NotBlank cuida da obrigatoriedade
        }
        boolean valido = Fabricante.fromNomeExibicao(valor).isPresent();
        if (!valido) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Fabricante '" + valor + "' invalido. Valores aceitos: " + LISTA_FABRICANTES)
                    .addConstraintViolation();
        }
        return valido;
    }
}
