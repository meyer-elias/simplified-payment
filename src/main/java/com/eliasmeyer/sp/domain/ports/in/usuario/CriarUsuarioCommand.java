package com.eliasmeyer.sp.domain.ports.in.usuario;

public record CriarUsuarioCommand(String nome, String documento, String email, String senha) {

	public CriarUsuarioCommand {
		if (nome == null || nome.trim().isEmpty()) {
			throw new IllegalArgumentException("nome é mandatório");
		}

		if (documento == null || documento.trim().isEmpty()) {
			throw new IllegalArgumentException("documento é mandatório");
		}

		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("email é mandatório");
		}

		if (senha == null || senha.trim().isEmpty()) {
			throw new IllegalArgumentException("senha é mandatória");
		}
	}
}
