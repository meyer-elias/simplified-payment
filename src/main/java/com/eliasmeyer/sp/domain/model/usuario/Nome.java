package com.eliasmeyer.sp.domain.model.usuario;

import java.util.Objects;

public record Nome(String nome) {

    public Nome {
        if (Objects.isNull(nome) || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome inválido!");
        }
    }
}
