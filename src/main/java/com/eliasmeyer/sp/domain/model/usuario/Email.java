package com.eliasmeyer.sp.domain.model.usuario;

import java.util.regex.Pattern;

public record Email(String address) {
    public Email {
        final var regex = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        if (!regex.matcher(address).matches()) {
            throw new IllegalArgumentException("E-mail inválido!");
        }
    }
}
