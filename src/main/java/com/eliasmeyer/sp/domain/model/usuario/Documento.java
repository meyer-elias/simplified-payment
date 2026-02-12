package com.eliasmeyer.sp.domain.model.usuario;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class Documento {

    protected final String numero;

    protected Documento(String numero) {
        this.numero = numero;
    }

    protected String validate(String numero) {
        if (Objects.isNull(numero) || numero.isBlank()) {
            throw new IllegalArgumentException("Número de documento inválido.");
        }

        return numero;
    }

    protected void addValidation(Predicate<String> validation, String errorMessage) {
        if (!validation.test(this.numero)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public abstract String getNumero();

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Documento documento)) return false;

        return numero.equals(documento.numero);
    }

    @Override
    public int hashCode() {
        return numero.hashCode();
    }

    @Override
    public String toString() {
        return "Documento{" +
                "numero='" + numero + '\'' +
                '}';
    }
}
