package com.eliasmeyer.sp.core.domain.model.usuario;

/**
 * Factory para criação de usuários (Comum ou Lojista).
 * <p>
 * Responsável por instanciar o tipo correto de usuário baseado no tipo de documento.
 */
public class UsuarioFactory {

	private UsuarioFactory() {
	}

	/**
	 * Cria um usuário baseado no tipo de documento usando pattern matching.
	 *
	 * @param documento documento do usuário (CPF ou CNPJ)
	 * @param nome      nome do usuário
	 * @param email     email do usuário
	 * @param senha     senha hasheada do usuário
	 * @return instância de UsuarioComum ou Lojista
	 * @throws IllegalArgumentException se o tipo de documento é inválido.
	 */
	public static Usuario criar(Documento documento, Nome nome, Email email, String senha) {
		return switch (documento) {
			case Cpf cpf -> new UsuarioComum(cpf, nome, email, senha);
			case Cnpj cnpj -> new Lojista(cnpj, nome, email, senha);
			default -> throw new IllegalArgumentException(
				"Tipo de documento inválido: " + documento.getClass().getSimpleName());
		};
	}
}

