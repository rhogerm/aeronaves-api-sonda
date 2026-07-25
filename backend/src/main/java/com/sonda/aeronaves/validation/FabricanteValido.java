package com.sonda.aeronaves.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Garante que o valor do campo "marca" corresponde exatamente (ignorando caixa/espacos)
 * a um dos fabricantes cadastrados na whitelist {@link com.sonda.aeronaves.model.Fabricante}.
 * Evita inconsistencias como "Enbraer", "Boing" ou "ErBus".
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FabricanteValidator.class)
public @interface FabricanteValido {

    String message() default "Fabricante invalido. Valores aceitos: {listaFabricantes}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
