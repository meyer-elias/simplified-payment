package com.eliasmeyer.sp.core.domain.ports.in.usuario;

import java.util.Objects;

public record CriarUsuarioCommand(String nome, String documento, String email, String senha,
								  String valorInicial) {

	public CriarUsuarioCommand {
		if (Objects.isNull(nome) || nome.trim().isEmpty()) {
			throw new IllegalArgumentException("nome é mandatório");
		}

		if (Objects.isNull(documento) || documento.trim().isEmpty()) {
			throw new IllegalArgumentException("documento é mandatório");
		}

		if (Objects.isNull(email) || email.trim().isEmpty()) {
			throw new IllegalArgumentException("email é mandatório");
		}

		if (Objects.isNull(senha) || senha.trim().isEmpty()) {
			throw new IllegalArgumentException("senha é mandatória");
		}

		if (Objects.isNull(valorInicial) || valorInicial.trim().isBlank()) {
			valorInicial = "0.0";
		}
	}
}
