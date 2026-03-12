package com.eliasmeyer.sp.core.domain.model.usuario;

/**
 * Usuário do tipo comum identificado por CPF.
 * <p>
 * Usuários comuns têm permissão para enviar dinheiro através do sistema.
 */
public class UsuarioComum extends Usuario {

	UsuarioComum(Cpf cpf, Nome nome, Email email, String senha) {
		super(cpf, nome, email, senha, TipoUsuario.COMUM);
	}

	UsuarioComum(UsuarioId usuarioId, Documento documento, Nome nome, Email email) {
		super(usuarioId, documento, nome, email, TipoUsuario.COMUM);
	}
}
