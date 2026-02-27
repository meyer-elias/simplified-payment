package com.eliasmeyer.sp.domain.model.usuario;

import java.util.Objects;

/**
 * Value Object que representa o nome de um usuário. Garante que o nome não seja nulo e não esteja
 * vazio.
 */
public record Nome(String nome) {

	public Nome {
		Objects.requireNonNull(nome, "Nome não pode ser nulo");

		if (nome.trim().isEmpty()) {
			throw new IllegalArgumentException("Nome não pode estar vazio");
		}
	}
}


