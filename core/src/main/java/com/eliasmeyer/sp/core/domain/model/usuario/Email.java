package com.eliasmeyer.sp.core.domain.model.usuario;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa um endereço de email. Valida o formato do email usando expressão
 * regular.
 */
public record Email(String address) {

	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

	public Email {
		Objects.requireNonNull(address, "Email não pode ser nulo");

		if (address.isBlank()) {
			throw new IllegalArgumentException("Email não pode estar vazio");
		}

		if (!EMAIL_PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("Email inválido: " + address);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Email(String address1))) {
			return false;
		}

		return address.equals(address1);
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}
}

